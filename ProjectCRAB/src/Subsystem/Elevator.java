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
	
	private Elevator() {
		encoder = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
		driveMotor = new Victor(Constants.ELEVATORMOTOR);
		elevatorControlLoop = new PIDLoop(Constants.ELEVATOR_KP, //Proportional Gain
											Constants.ELEVATOR_KI, //Integral Gain
											Constants.ELEVATOR_KD); //Max Speed
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
						if(currentState!=wantedState) {
							currentState=wantedState;
						}
					case OPEN_LOOP:
						System.out.println(encoder.getRaw());
						throttleValue = joystick.elevatorOpenLoop() * kMaxHeight; 
						setFloor(throttleValue);
					case POSITION_FOLLOW:
						setFloor(throttleValue);
				}
			}

			@Override
			public void stop() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void test() {
				
			}
	
		});
	}
}
