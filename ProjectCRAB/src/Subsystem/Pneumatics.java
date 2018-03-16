package Subsystem;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Pneumatics extends Subsystem {
	private static Pneumatics instance = new Pneumatics();
	Compressor compressor;
	Controller controller; 
	DoubleSolenoid solenoid; 
	Loop_Manager loopMan;
	private Pneumatics()
	{
		compressor = new Compressor(0);
		controller = Controller.getInstance();
		solenoid = new DoubleSolenoid(4, 5);
		loopMan = Loop_Manager.getInstance();
	}
	public static Pneumatics getInstance()
	{
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
		loopMan.addLoop(new Loop() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onloop() {
				if (compressor.getPressureSwitchValue()) {
					compressor.setClosedLoopControl(true);
				}
				if(controller.solenoidOut()) {
					solenoid.set(DoubleSolenoid.Value.kForward);
				}
				else
					solenoid.set(DoubleSolenoid.Value.kReverse);
			}

			@Override
			public void stop() {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	}

}
