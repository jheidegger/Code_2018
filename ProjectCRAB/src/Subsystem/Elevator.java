package Subsystem;

import org.usfirst.frc.team6713.robot.Constants;

import Subsystem.Intake.systemStates;
import Util.PIDLoop;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator extends Subsystem {
	private static Elevator instance = new Elevator();
	
	private Victor driveMotor;
	private PIDLoop elevatorControlLoop; 
	private Encoder encoder;
	private Controller joystick = Controller.getInstance(); 
	private double throttleValue; 
	double lastHeight = 0; 
	private double kMaxHeight = 7;//Constants.MAX_HEIGHT_ENCODER_TICKS;
	
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
		wantedHeight = wantedHeight / .17 * Math.PI * 2048;

		double liftSpeed = elevatorControlLoop.returnOutput(encoder.getRaw(),wantedHeight);
		SmartDashboard.putNumber("Enc", encoder.getRaw());
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
						if(joystick.elevatorHigh()) {
							setFloor(7);
							lastHeight=7;
						}
						else if(joystick.elevatorMid()) {
							setFloor(3.5);
							lastHeight=3.5;
						}
						else if(joystick.elevatorLow()) {
							setFloor(0);
							lastHeight = 0;
						}
						else {
							setFloor(lastHeight);
						}
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
				//Smartdashboard.putNumber("Encoder Units", encoder.getRaw());
			}
	
		});
	}

	
}
