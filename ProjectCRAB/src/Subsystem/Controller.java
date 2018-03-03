package Subsystem;

import org.usfirst.frc.team6713.robot.*;

import edu.wpi.first.wpilibj.Joystick;

public class Controller extends Subsystem{
	private static Controller instance = new Controller(); 
	
	private Joystick velocityStick;
	private Joystick thetaStick;
	
	//private PurpleTrigger trigger; 
	private Joystick buttonMonkey;
	
	//private PurpleTrigger ledTest;
	
	public Controller(){
		velocityStick = new Joystick(Constants.DRIVE_JOYSTICK);
		thetaStick = new Joystick(Constants.GEAR_JOYSTICK);
		buttonMonkey = new Joystick(Constants.BUTTON_MONKEY);
	}
	
	public enum driveStationStates{
		ALL,
		DRIVER,
		PROGRAMMING
	}
	
	public static Controller getInstance() {
		return instance; 
	}
	/** 
	 * @return double deadbanded Y axis
	 */
	public double getForward() {
		if(Math.abs(velocityStick.getY())<.06) {
			return 0.0;
		}
		else {
			return Math.pow(velocityStick.getY(),1);
		}
	}
	/** 
	 * @return double deadbanded X axis
	 */
	public double getStrafe() {
		if(Math.abs(velocityStick.getX())<.06) {
			return 0;
		}
		else {
			return Math.pow(velocityStick.getX(),1);
		}
	}
	public double elevatorOpenLoop() {
		return -buttonMonkey.getY()*.6;
	}
	/**
	 * @return double deadbanded X axis
	 */
	public double getRotation() {
		if(Math.abs(thetaStick.getX())<.06) {
			return 0.0;
		}
		else {
			return Math.pow(thetaStick.getX(),1)/3.5;
		}
	}
	public boolean getGyroResetButton() {
		return velocityStick.getRawButton(8);
	}
	public boolean getOuttakeButton() {
		return buttonMonkey.getRawButton(4);
	}
	public boolean getIntakeButton() {
		return buttonMonkey.getRawButton(2);
	}
	public boolean unjamButton() {
		return buttonMonkey.getRawButton(1);
	}
	public boolean getSlowFieldCentricButton()
	{
		return velocityStick.getRawButton(1);
	}
	public boolean getSlowRobotCentricButton()
	{
		return thetaStick.getRawButton(1);
	}
	public boolean executeAutoButton() {
		return velocityStick.getRawButton(2);
	}
	public boolean resetAutoButton()
	{
		return velocityStick.getRawButton(3);
	}
	public boolean forwardTest() {
		return velocityStick.getRawButton(4);
	}
	/*public boolean testElevator() {
		return buttonMonkey.get
	}
	public boolean testIntake() {
		
	}
	public boolean testDrivetrain() {
		
	}*/

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
		//N/A
		
	}
	
}

