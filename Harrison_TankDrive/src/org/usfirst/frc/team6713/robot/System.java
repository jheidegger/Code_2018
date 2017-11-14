package org.usfirst.frc.team6713.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;

public class System {
	RobotDrive myRobot = new RobotDrive(0, 1);
	Joystick wheel = new Joystick(0);
	
	private double speed;
	private double turn; 
	private double leftMotor;
	private double rightMotor;
	
	public void WheelDrive() {
		
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
