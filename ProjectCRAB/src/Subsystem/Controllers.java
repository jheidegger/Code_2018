package Subsystem;

import Robot.Constants;
//import Subsystem.PixyCam.systemStates;
//import Util.PurpleTrigger;
//import Vision.PixyException;
import edu.wpi.first.wpilibj.Joystick;

public class Controllers extends Subsystem{
	private static Controllers instance = new Controllers(); 
	
	private Joystick velocityStick;
	private Joystick thetaStick;
	//private Joystick buttonMonkey;
	
	//private PurpleTrigger ledTest;
	
	public Controllers(){
		velocityStick = new Joystick(Constants.DRIVE_JOYSTICK);
		thetaStick = new Joystick(Constants.GEAR_JOYSTICK);
		//buttonMonkey = new Joystick(Constants.BUTTON_MONKEY);
	}
	
	public enum driveStationStates{
		ALL,
		DRIVER,
		PROGRAMMING
	}
	
	public static Controllers getInstance() {
		return instance; 
	}
	
	public double getForward() {
		if(Math.abs(velocityStick.getY())<.08) {
			return 0;
		}
		else {
			return velocityStick.getY();
		}
	}
	
	public double getStrafe() {
		if(Math.abs(velocityStick.getX())<.08) {
			return 0;
		}
		else {
			return velocityStick.getX();
		}
	}
	public boolean getGyroReset() {
		return velocityStick.getRawButton(1);
	}
	public boolean getButton2() {
		return velocityStick.getRawButton(2);
	}
	public double getRotation() {
		if(Math.abs(thetaStick.getX())<.08) {
			return 0;
		}
		else {
			return thetaStick.getX();
		}
	}
	public boolean alignButton()
	{
		return velocityStick.getTrigger();
	}

	public void checkTriggers() {
		
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
			}

			@Override
			public void onloop() {
				checkTriggers();
			}

			@Override
			public void stop() {
			}
	
		});
		
	}
}
