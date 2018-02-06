package Subsystem;

import Subsystem.Drivetrain.systemStates;

public class LED extends Subsystem{
	private LED instance; 
	
	private LED() {
	}
	
	public LED getInstance() {
		return instance; 
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
		Loop_Manager.getInstance().addLoop(new Loop() {
			@Override
			public void onStart() {
			}
			@Override
			public void onloop() {
			}	
			@Override
			public void stop() {				
			}
		});	
		}
		
	}

