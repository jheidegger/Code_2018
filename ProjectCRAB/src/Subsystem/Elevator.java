package Subsystem;

import org.usfirst.frc.team6713.robot.Constants;

import Subsystem.Intake.systemStates;
import Util.PIDLoop;
import edu.wpi.first.wpilibj.*;

public class Elevator extends Subsystem {
	private static Elevator instance = new Elevator();
	
	private Victor driveMotor;
	private PIDLoop elevatorControlLoop; 
	private Encoder encoder;
	private Controller joystick = Controller.getInstance(); 
	private double throttleValue; 
	private double kMaxHeight = Constants.MAX_HEIGHT_ENCODER_TICKS;
	
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
	
	
	private void setFloor(double height) {
		double liftSpeed = elevatorControlLoop.returnOutput(encoder.getRaw(),height);
		driveMotor.set(-liftSpeed);
	}
	
	public void setThrottleValue(double throttleValue) {
		this.throttleValue = throttleValue;
		//throttleValue *= (Encoder Ticks) / (Circumference of drum) 
		//
	}
	
	public double getHeight() {
		return 0;
	}
	
	@Override
	public void zeroAllSensors() {
	}

	@Override
	public boolean checkSystem() {
		return false;
	}
	
 	public void setWantedState(systemStates wantedState)
 	{
 		this.currentState = wantedState;
 	}
 	
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
				switch(currentState){
					case NEUTRAL:
						lastState = systemStates.NEUTRAL;
						checkState();
						break;
					case OPEN_LOOP:
						System.out.println(encoder.getRaw());
						throttleValue = joystick.elevatorOpenLoop() * kMaxHeight; 
						setFloor(throttleValue);
						lastState = systemStates.OPEN_LOOP;
						checkState();
						break;
					case POSITION_FOLLOW:
						setFloor(throttleValue);
						lastState = systemStates.POSITION_FOLLOW;
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
				Smartdashboard.putNumber("Encoder Units", encoder.getRaw());
			}
	
		});
	}

	
}
