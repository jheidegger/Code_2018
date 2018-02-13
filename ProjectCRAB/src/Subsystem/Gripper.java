package Subsystem;

import Robot.Constants;
import Subsystem.Elevator.systemStates;
import edu.wpi.first.wpilibj.Solenoid;

public class Gripper extends Subsystem {
	private Solenoid gripSolenoid;
	private Solenoid flipperSolenoid;
	private static Gripper instance = new Gripper();
	
	public enum systemStates{
		NEUTRAL,
		RELEASE_CUBE,
		GRIP_CUBE,
		OPEN_LOOP,
		FLIP_UP,
		FLIP_DOWN
	}
	
	private systemStates currentState;
	private systemStates requestedState;
	
	private Gripper()
	{
		gripSolenoid = new Solenoid(Constants.GRIPSOLENOID);
		flipperSolenoid = new Solenoid(Constants.FLIPPERSOLENOID);
	}
	
	
	@Override
	public void zeroAllSensors() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean checkSystem() {
		// TODO Auto-generated method stub
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
