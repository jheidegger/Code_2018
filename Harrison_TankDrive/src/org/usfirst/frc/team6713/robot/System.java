package org.usfirst.frc.team6713.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;

public class System {
	
	
	private static double speed;
	private static double turn; 
	private static double leftMotor;
	private static double rightMotor;
	


	public static void wheelDrive(RobotDrive myRobot, Joystick wheel) {
		// TODO Auto-generated method stub
		speed = wheel.getThrottle(); 
		turn = wheel.getX();
		leftMotor = speed + turn; 
		rightMotor = speed - turn;
		
		if(leftMotor >= 1) {leftMotor = 1;}
		else if(leftMotor <= -1) {leftMotor = -1;}
		if(rightMotor >= 1) {rightMotor =1;}
		else if(rightMotor <= -1) {rightMotor = -1;}
		
		myRobot.tankDrive(leftMotor, rightMotor);
	}
	
}
