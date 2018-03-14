package Subsystem;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;

public class RobotActor {
	private ArrayList<Joystick> mySticks ;
	private ADXRS450_Gyro gyro;
	public RobotActor(ArrayList<Joystick> mySticks, ADXRS450_Gyro gyro)
	{
		this.mySticks = mySticks;
		this.gyro = gyro;
	}

}
