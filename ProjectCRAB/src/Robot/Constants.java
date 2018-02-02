package Robot;

import edu.wpi.first.wpilibj.Timer;

/*
 * All constants are stored in this file as static constant variables
 */
public class Constants {
	public static double DEFAULTDELTATIME = .02;
	
	//Robot Port Map NOTE: only change if the robot is rewired
	public static int ELEVATORMOTOR = 4;
	public static int INTAKERIGHTSIDE = 1;
	public static int INTAKELEFTSIDE = 2;
	
	
	//Subsystem constants
	
	//Drive Train constants
	public static double DRIVETRAINLENGTH = 5;
	public static double DRIVETRAINWIDTH = 5;
	// ft/s
	public static double DRIVETRAINMAXWHEELSPEED = 13.5;
	// radians/s
	public static double DRIVETRAINMAXROTATIONSPEED = 5;
	
	//vision constants
	public static int AVG_LIST_SIZE = 10;
	public static int PIXY_CENTER_X = 160; 
	
	
	//Elevator control loop
	public static double ElevatorKp = 0;
	public static double ElevatorKi = 0;
	public static double ElevatorKd = 0;
	//intake subsystem
	public static double INTAKESPEED = .9;
	public static double INTAKESCORESPEED = -.6;
	public static double INTAKEHANDOFFSPEED = .3;

}
