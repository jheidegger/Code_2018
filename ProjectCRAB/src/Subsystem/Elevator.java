package Subsystem;

import java.util.ArrayList;

import org.usfirst.frc.team6713.robot.Constants;

import Auton.Trajectory;
import Auton.Waypoint;
import Util.PIDLoop;
import Util.Trajectory1D;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator extends Subsystem {
	private static Elevator instance = new Elevator();
	private Controller joystick = Controller.getInstance(); 
	//private DigitalInput ZeroSwitch = new DigitalInput(Constants.ELEVATOR_ZERO_SWITCH);
	private Victor driveMotor;
	private Victor driveMotor2;
	private PIDLoop elevatorControlLoop; 
	private Encoder encoder;
	private double wantedFloor;
	private Trajectory1D motionProfileTrajectory;
	private double liftSpeed;
	private double motionProfileStartTime;
	public enum systemStates {
		NEUTRAL,
		POSITION_FOLLOW,
		MOTION_PROFILE,
		OPEN_LOOP
	}
	
	private systemStates currentState;
	private systemStates wantedState;
	private systemStates lastState;
	private boolean isOpenLoop;
	private Elevator() {
		encoder = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
		driveMotor = new Victor(Constants.ELEVATORMOTOR);
		driveMotor2 = new Victor(6);
		elevatorControlLoop = new PIDLoop(Constants.ELEVATOR_KP, //Proportional Gain
											Constants.ELEVATOR_KI, //Integral Gain
											Constants.ELEVATOR_KD,
											1); //Max Speed
	}
	
	public static Elevator getInstance() {
		return instance; 
	}
	
	private void setFloor(double wantedHeight) {
		//wantedHeight = wantedHeight / .17 * Math.PI * 2048;
		liftSpeed = elevatorControlLoop.returnOutput(-encoder.getRaw(),wantedHeight);
		driveMotor.set(liftSpeed);
		driveMotor2.set(liftSpeed);
	}
	
	public void setWantedFloor(double wF) {this.wantedFloor = wF;}
	public double getHeight() {return -encoder.getRaw();}
 	public void setWantedState(systemStates wantedState) {this.wantedState = wantedState;}
 	public systemStates getState() {return currentState;}
	@Override public void zeroAllSensors() { encoder.reset();}
	@ Override public boolean checkSystem() {return false;}
 	
 	private void checkState() {
 		if(currentState!=wantedState) {
			currentState=wantedState;
		}
 	}
 	
 	private void checkEncoder() {
 		/*if(!ZeroSwitch.get()) {
 			encoder.reset();
 		}*/
 	}
 	
	@Override
	public void registerLoop() {
		Loop_Manager.getInstance().addLoop(new Loop()
		{
			@Override
			public void onStart() {
				currentState = systemStates.NEUTRAL;
				wantedState = systemStates.NEUTRAL;
				isOpenLoop = false;
			}
			@Override
			public void onloop() {
				//checkEncoder();
				//outputToSmartDashboard();
				double openLoopAdjust = joystick.elevatorPositionJoystick()*2000.0;
				switch(currentState){
					case NEUTRAL:
						driveMotor.set(0.0);
						driveMotor2.set(0.0);
						checkState();
						lastState = systemStates.NEUTRAL;
						break;		
					case OPEN_LOOP:
						driveMotor.set(joystick.elevatorPositionJoystick());
						driveMotor2.set(joystick.elevatorPositionJoystick());
						checkState();
						lastState = systemStates.OPEN_LOOP;
						break;
					case POSITION_FOLLOW:			
						checkState();
						if(Math.abs(wantedFloor-getHeight())>10000)
						{
							currentState = systemStates.MOTION_PROFILE;
						}
						else
						{
							setFloor(wantedFloor);
						}
						lastState = systemStates.POSITION_FOLLOW;
						break;
					case MOTION_PROFILE:
						if(lastState != systemStates.MOTION_PROFILE)
						{
							if(getHeight()<wantedFloor)
							{
								motionProfileTrajectory = new Trajectory1D(100000.0,400000.0);
							}
							else
							{
								motionProfileTrajectory = new Trajectory1D(50000.0,100000.0);
							}
							motionProfileTrajectory.addWaypoint(new Waypoint(getHeight(),0.0,0.0));
							motionProfileTrajectory.addWaypoint(new Waypoint(wantedFloor,0.0,0.0));
							motionProfileTrajectory.calculateTrajectory();
							motionProfileStartTime = Timer.getFPGATimestamp();
						}
						else if(Timer.getFPGATimestamp()-motionProfileStartTime<motionProfileTrajectory.getTimeToComplete())
						{
							setFloor(motionProfileTrajectory.getPosition(Timer.getFPGATimestamp()-motionProfileStartTime));
						}
						else
						{
							currentState = systemStates.POSITION_FOLLOW;
						}
						lastState = systemStates.MOTION_PROFILE;
						break;
				}
			}
			@Override
			public void stop() {
				driveMotor.set(0);
			}
			@Override
			public void test() {
				//Smartdashboard.putNumber("Encoder Units", encoder.getRaw());
			}
		});
	}

	@Override
	public void outputToSmartDashboard() {
		//SmartDashboard.putNumber("Lift Speed", liftSpeed);	driveMotor.set(liftSpeed);
		//SmartDashboard.putNumber("Lift Speed", liftSpeed);	driveMotor.set(liftSpeed);
		SmartDashboard.putString("elevator state", currentState.toString());
		SmartDashboard.putString("elevator wantedState", wantedState.toString());
		SmartDashboard.putNumber("elevator wanted floor", wantedFloor);
		SmartDashboard.putNumber("position", getHeight());
		SmartDashboard.putNumber("Rate of spin", encoder.getRate());
	}
}
