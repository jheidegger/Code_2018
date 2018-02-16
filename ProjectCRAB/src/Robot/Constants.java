package Robot;

import edu.wpi.first.wpilibj.Timer;

/*
 * All constants are stored in this file as static constant variables
 */
public class Constants {
	//Robot Port Map NOTE: only change if the robot is rewired
	
	public static int ELEVATORMOTOR = 4;
	
	public static final int INTAKERIGHTSIDE = 1;
	public static final int INTAKELEFTSIDE = 2;

	
	//Driverstation constants
	public static int DRIVE_JOYSTICK = 0; 
	public static int GEAR_JOYSTICK = 1;
	public static int BUTTON_MONKEY = 2;
	
	//Subsystem constants
	
	//Drive Train constants
	public static double OFFSETS[] = {2041.0,2049.0,3481.0,574.0};
	public static double DRIVETRAINLENGTH = 32;
	public static double DRIVETRAINWIDTH = 28;
	public static final double WHEELDIAMETER = 4;
	public static double DRIVETRAINMAXWHEELSPEED = 13.5; // ft/s
	public static double DRIVETRAINMAXROTATIONSPEED = 5; // radians/s
	public static double ENCODER_UNITS = 4096.0;
	//Swerve Pod constants
	public static final double SWERVE_kP = 2.0;
	public static final double SWERVE_kI = 0.0;
	public static final double SWERVE_kD = 0.0;
	public static final int SWERVE_ALLOWABLE_ERROR = 10;
	//vision constants
	public static int AVG_LIST_SIZE = 10;
	public static int PIXY_CENTER_X = 160; 
	//Elevator control loop
	public static double ELEVATOR_KP = 0;
	public static double ELEVATOR_KI = 0;
	public static double ELEVATOR_KD = 0;
	public static double MAX_HEIGHT_ENCODER_TICKS = 10000; 
	//intake constants
	public static final double INTAKESPEED = 0;
	public static final double INTAKESCORESPEED = 0;
	public static final double INTAKEHANDOFFSPEED = 0;

	
	

}
