package Subsystem;

import org.usfirst.frc.team6713.robot.Constants;
import Util.PIDLoop;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator extends Subsystem {
	private static Elevator instance = new Elevator();
	private Controller joystick = Controller.getInstance(); 
	
	private Victor driveMotor;
	private PIDLoop elevatorControlLoop; 
	private Encoder encoder;
	
	private double wantedFloor;
	private double currentHeight;
	private double kMaxHeight = Constants.MAX_HEIGHT_ENCODER_TICKS;
	private double kMidHeight = Constants.MID_HEIGHT_ENCODER_TICKS;
	private double kLowHeight = Constants.LOW_HEIGHT_ENCODER_TICKS;
	
	public enum systemStates{
		NEUTRAL,
		POSITION_FOLLOW,
		OPEN_LOOP
	}
	
	private systemStates currentState;
	private systemStates wantedState;
	private systemStates lastState;
	
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
		double liftSpeed = elevatorControlLoop.returnOutput(encoder.getRaw(),wantedHeight);
		driveMotor.set(liftSpeed);
	}

	public void setWantedFloor(double wantedFloor) {this.wantedFloor = wantedFloor;}
	public double getHeight() {return 0;}
 	public void setWantedState(systemStates wantedState) {this.currentState = wantedState;}
	@Override public void zeroAllSensors() {}
	@ Override public boolean checkSystem() {return false;}
 	
 	private void checkState() {
 		if(currentState!=wantedState) {
			currentState=wantedState;
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
			}
			@Override
			public void onloop() {
				double openLoopAdjust = joystick.elevatorOpenLoop()*2000.0;
				if(joystick.elevatorResetEncoder()) {encoder.reset();}
				switch(currentState){
					case NEUTRAL:
						lastState = systemStates.NEUTRAL;
						checkState();
						break;		
					case OPEN_LOOP:
						setFloor(wantedFloor);
						lastState = systemStates.POSITION_FOLLOW;
						checkState();
						break;
					case POSITION_FOLLOW:
						if(joystick.elevatorHigh()) {
							wantedFloor = kMaxHeight;
							currentHeight=wantedFloor;
						}
						else if(joystick.elevatorMid()) {
							wantedFloor = kMidHeight;
							currentHeight=wantedFloor;
						}
						else if(joystick.elevatorLow()) {
							wantedFloor = kLowHeight;
							currentHeight = wantedFloor;
						}
						currentHeight += openLoopAdjust;
						if(currentHeight < 0) {currentHeight = 0;}
						else if(currentHeight > 90000) {currentHeight = 90000;}
						setFloor(currentHeight);
						lastState = systemStates.OPEN_LOOP;
						checkState();
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
}
