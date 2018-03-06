
package org.usfirst.frc.team6713.robot;

/**
 * Important static variables
 */

public class Constants {
	//Robot Port Map NOTE: only change if the robot is rewired
	
	public static int ELEVATORMOTOR = 4;
	
	public static final int INTAKERIGHTSIDE = 0;
	public static final int INTAKELEFTSIDE = 1;
	public static final int INTAKESTOWINGMOTOR = 3;

	//Driverstation constants
	public static int DRIVE_JOYSTICK = 0; 
	public static int GEAR_JOYSTICK = 1;
	public static int BUTTON_MONKEY = 2;
	
	//Subsystem constants
	
	//Drive Train constants
	public static double OFFSETS[] = {3248.0,284.0,899.0,2347.0};
	//public static double OFFSETS[] = {1571.0,3763.0,1664.0,1386.0};
	public static double DRIVETRAINLENGTH = 13;
	public static double DRIVETRAINWIDTH = 17.5;
	public static final double WHEELDIAMETER = 4;
	public static double DRIVETRAINMAXWHEELSPEED = 13.5; // ft/s
	public static double DRIVETRAINMAXROTATIONSPEED = 5; // radians/s
	public static double ENCODER_UNITS = 4096.0;
	//Swerve Pod constants
	public static final double SWERVE_kP = 5.0;
	public static final double SWERVE_kI = 0.0023;
	public static final double SWERVE_kD = 210.0;
	public static final int SWERVE_ALLOWABLE_ERROR = 12;
	//vision constants
	public static int AVG_LIST_SIZE = 10;
	public static int PIXY_CENTER_X = 160; 
	//Elevator control loop
	public static double ELEVATOR_KP = 0;
	public static double ELEVATOR_KI = 0;
	public static double ELEVATOR_KD = 0;
	public static double MAX_HEIGHT_ENCODER_TICKS = 10000; 
	//intake constants
	public static final double INTAKESPEED = .6;
	public static final double INTAKESCORESPEED = -1.0;
	public static final double INTAKEHANDOFFSPEED = 0;
	public static final double UNSTOWINGTIME = .5;
	public static final double STOWINGTIME = .5;

	public static final double SWERVE_kF = 0.0;

	public static final double DRIVE_kP = .073;

	public static final double DRIVE_kI = 0.0000;

	public static final double DRIVE_kD = 8.5;

	public static final int DRIVE_ALLOWABLE_ERROR = 10;

	//public static final double[] DRIVE_kF = {0.09399982,0.09588528,0.09201295,.09662794};
	public static final double DRIVE_kF = 0.0;//0.08571429;
	public static final double MAXSLOWPERCENTSPEED = .5;

	public static final double DRIVE_RAMPRATE = .3;

	

	

	
	
	//public static double getKF(int id) {
		//return DRIVE_kF[id];
	//}

	
	

}
