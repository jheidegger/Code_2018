package org.usfirst.frc.team6713.subsystem;

import Robot.Constants;
import Util.PIDLoop;
import edu.wpi.first.wpilibj.*;

public class Elevator extends Subsystem {
	private Victor driveMotor;
	private PIDLoop elevatorControlLoop; 
	public Elevator() {
		
		driveMotor = new Victor(Constants.ELEVATORMOTOR);
		elevatorControlLoop = new PIDLoop(Constants.ElevatorKp,
											Constants.ElevatorKi, 
											Constants.ElevatorKd,
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
		super.Loop_Manager_Instance.addLoop(new Loop()
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
