package org.usfirst.frc.team3176.robot;
import edu.wpi.first.wpilibj.*;

public class DriveTrain {
	private Victor RightSide;
	private Victor LeftSide;
	private static DriveTrain instance = null;
	private DriveTrain()
	{
		RightSide = new Victor(Constants.RightSidePort);
		LeftSide = new Victor(Constants.leftSidePort);	
	}
	public static DriveTrain getInstance()
	{
		if(instance == null)
		{
			instance = new DriveTrain();
		}
		return instance;
	}
	public void drive(double speed, double turning)
	{
		double rightPower = speed + turning;
		double leftPower = speed - turning;
		if(rightPower>1 && rightPower>leftPower)
		{
			rightPower /= rightPower;
			leftPower /= rightPower;
		}
		if (leftPower>1)
		{
			rightPower /= leftPower;
			leftPower /= leftPower;
		}
		RightSide.set(rightPower);
		LeftSide.set(leftPower);
	}
}
