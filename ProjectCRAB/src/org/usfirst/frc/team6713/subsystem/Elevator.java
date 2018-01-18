package org.usfirst.frc.team6713.subsystem;

import Robot.Constants;
import edu.wpi.first.wpilibj.*;

public class Elevator extends Subsystem {
	
	
	private Victor driveMotor;
	
	public Elevator() {
		// TODO Auto-generated constructor stub
		driveMotor = new Victor(Constants.ELEVATORMOTOR);
		
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
