package Subsystem;

import Robot.Constants;
import Util.PIDLoop;
import edu.wpi.first.wpilibj.*;

public class Elevator extends Subsystem {
	private Victor driveMotor;
	private PIDLoop elevatorControlLoop; 
	public Elevator() {
		
		driveMotor = new Victor(Constants.ELEVATORMOTOR);
		elevatorControlLoop = new PIDLoop(Constants.ELEVATOR_KP,
											Constants.ELEVATOR_KI, 
											Constants.ELEVATOR_KD,
											1);
		
		
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
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onloop() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void stop() {
				// TODO Auto-generated method stub
				
			}
	
		});
	}
}
