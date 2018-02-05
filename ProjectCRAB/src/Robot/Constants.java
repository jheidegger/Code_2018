package Robot;

import edu.wpi.first.wpilibj.Timer;

/*
 * All constants are stored in this file as static constant variables
 */
public class Constants {
	//Robot Port Map NOTE: only change if the robot is rewired
	public static int ELEVATORMOTOR = 4;
	
	//Driverstation constants
	public static int DRIVE_JOYSTICK = 0; 
	public static int GEAR_JOYSTICK = 1;
	public static int BUTTON_MONKEY = 2;
	
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
	public static double ELEVATOR_KP = 0;
	public static double ELEVATOR_KI = 0;
	public static double ELEVATOR_KD = 0;
	

}
