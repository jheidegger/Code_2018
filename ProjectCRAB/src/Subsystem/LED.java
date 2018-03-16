package Subsystem;

import edu.wpi.first.wpilibj.I2C;

public class LED extends Subsystem{
	private static LED instance = new LED(); 
	private I2C arduino;
	
	private LED() {
		//arduino = new I2C();
	}
	
	public enum ledStates{
		CUBE_INTAKED,
		INTAKING,
		LIGHTSHOW
	}
	
	private ledStates wantedState; 
	
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
				switch(wantedState) {
				case CUBE_INTAKED:
				case INTAKING:
				case LIGHTSHOW:
				}
				
			}	
			@Override
			public void stop() {				
			}
		});	
		}
		
	}

