package Subsystem;

import org.usfirst.frc.team6713.robot.Constants;
import Util.PIDLoop;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator extends Subsystem {
	private static Elevator instance = new Elevator();
	private Controller joystick = Controller.getInstance(); 
	private DigitalInput ZeroSwitch = new DigitalInput(Constants.ELEVATOR_ZERO_SWITCH);
	private Victor driveMotor;
	private PIDLoop elevatorControlLoop; 
	private Encoder encoder;
	
	private double wantedFloor;

	private double liftSpeed;
	
	public enum systemStates {
		NEUTRAL,
		POSITION_FOLLOW,
		OPEN_LOOP
	}
	
	private systemStates currentState;
	private systemStates wantedState;
	private boolean isOpenLoop;
	private Elevator() {
		encoder = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
		driveMotor = new Victor(Constants.ELEVATORMOTOR);
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
	}

	public void setWantedFloor(double wF) {this.wantedFloor = wF;}
	public double getHeight() {return -encoder.getRaw();}
 	public void setWantedState(systemStates wantedState) {this.currentState = wantedState;}
 	public systemStates getState() {return currentState;}
	@Override public void zeroAllSensors() { encoder.reset();}
	@ Override public boolean checkSystem() {return false;}
 	
 	private void checkState() {
 		if(currentState!=wantedState) {
			currentState=wantedState;
		}
 	}
 	
 	private void checkEncoder() {
 		if(!ZeroSwitch.get()) {
 			encoder.reset();
 		}
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
				outputToSmartDashboard();
				double openLoopAdjust = joystick.elevatorPositionJoystick()*2000.0;
				switch(currentState){
					case NEUTRAL:
					driveMotor.set(0.0);
						checkState();
						break;		
					case OPEN_LOOP:
						driveMotor.set(joystick.elevatorPositionJoystick());
						checkState();
						break;
					case POSITION_FOLLOW:			
						setFloor(wantedFloor);
						checkState();
//						if(joystick.elevatorHigh()) {
//							wantedFloor = kMaxHeight;
//							currentHeight=wantedFloor;
//						}
//						else if(joystick.elevatorMid()) {
//							wantedFloor = kMidHeight;
//							currentHeight=wantedFloor;
//						}
//						else if(joystick.elevatorLow()) {
//							wantedFloor = kLowHeight;
//							currentHeight = wantedFloor;
//						}
//						currentHeight += openLoopAdjust;
//						if(currentHeight < 0) {currentHeight = 0;}
//						else if(currentHeight > 90000) {currentHeight = 90000;}
//						setFloor(currentHeight);
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
		SmartDashboard.putNumber("Lift Speed", liftSpeed);	driveMotor.set(liftSpeed);
		SmartDashboard.putNumber("Lift Speed", liftSpeed);	driveMotor.set(liftSpeed);
		SmartDashboard.putString("elevator state", currentState.toString());
		SmartDashboard.putNumber("elevator wanted floor", wantedFloor);
		SmartDashboard.putNumber("position", getHeight());
		SmartDashboard.putNumber("Rate of spin", encoder.getRate());
	}
}
