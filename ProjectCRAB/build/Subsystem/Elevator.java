package Subsystem;

import org.usfirst.frc.team6713.robot.Constants;

import Util.PIDLoop;
import edu.wpi.first.wpilibj.*;

public class Elevator extends Subsystem {
	private static Elevator instance = new Elevator();
	
	private Victor driveMotor;
	private PIDLoop elevatorControlLoop; 
	private Encoder encoder;
	private Controller joystick; 
	
	private double throttleValue; 
	private double kMaxHeight = Constants.MAX_HEIGHT_ENCODER_TICKS;
	
	public enum systemStates{
		NEUTRAL,
		POSITION_FOLLOW,
		OPEN_LOOP
	}
	
	private systemStates currentState;
	private systemStates requestedState;
	
	private Elevator() {
		encoder = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
		driveMotor = new Victor(Constants.ELEVATORMOTOR);
		joystick = Controller.getInstance();
		elevatorControlLoop = new PIDLoop(Constants.ELEVATOR_KP, //Proportional Gain
											Constants.ELEVATOR_KI, //Integral Gain
											Constants.ELEVATOR_KD, //Derivative Gain
											1); //Max Speed
	}
	
	public Elevator getInstance() {
		return instance; 
	}
	
	private void setFloor(double height) {
		double liftSpeed = elevatorControlLoop.returnOutput(encoder.getRaw(), height);
		
		if(liftSpeed > 1) { liftSpeed = 1; }
		else if(liftSpeed < -1) { liftSpeed = -1; }
		
		driveMotor.set(liftSpeed);
	}
	
	public void setThrottleValue(double throttleValue) {
		this.throttleValue = throttleValue;
	}

	@Override
	public void zeroAllSensors() {
	}

	@Override
	public boolean checkSystem() {
		return false;
	}

	@Override
	public void registerLoop() {
		Loop_Manager.getInstance().addLoop(new Loop()
		{

			@Override
			public void onStart() {
				currentState = systemStates.NEUTRAL;
				requestedState = systemStates.NEUTRAL;				
			}

			@Override
			public void onloop() {
				switch(currentState){
					case NEUTRAL:
						if(currentState!=requestedState) {
							currentState=requestedState;
						}
					case OPEN_LOOP:
						//throttleValue = joystick.getElevatorDrive() * kMaxHeight; 
						setFloor(throttleValue);
					case POSITION_FOLLOW:
						setFloor(throttleValue);
				}
			}

			@Override
			public void stop() {
				// TODO Auto-generated method stub
				
			}
	
		});
	}
}
