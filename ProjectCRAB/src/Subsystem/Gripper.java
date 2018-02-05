package Subsystem;

import Robot.Constants;
import edu.wpi.first.wpilibj.Solenoid;

public class Gripper extends Subsystem {
	private Solenoid gripSolenoid;
	private Solenoid flipperSolenoid;
	private static Gripper instance = new Gripper();
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
		// TODO Auto-generated method stub

	}

}
