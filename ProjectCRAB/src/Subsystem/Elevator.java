package Subsystem;

import Robot.Constants;
import Util.PIDLoop;
import edu.wpi.first.wpilibj.*;

public class Elevator extends Subsystem {
	private Victor driveMotor;
	private PIDLoop elevatorControlLoop; 
	private Encoder encoder;
	private static Elevator instance = new Elevator();
	
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
		elevatorControlLoop = new PIDLoop(Constants.ELEVATOR_KP, //Proportional Gain
											Constants.ELEVATOR_KI, //Integral Gain
											Constants.ELEVATOR_KD, //Derivative Gain
											1); //Max Speed
	}
	
	public Elevator getInstance() {
		return instance; 
	}
	
	public void setFloor(double height) {
		double liftSpeed = elevatorControlLoop.returnOutput(encoder.getDistance(), height);
		
		if(liftSpeed > 1) { liftSpeed = 1; }
		else if(liftSpeed < -1) { liftSpeed = -1; }
		
		driveMotor.set(liftSpeed);
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
				}
			}

			@Override
			public void stop() {
				// TODO Auto-generated method stub
				
			}
	
		});
	}
}
