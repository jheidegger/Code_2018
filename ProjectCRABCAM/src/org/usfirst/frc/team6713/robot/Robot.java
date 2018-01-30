/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6713.robot;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.interfaces.Gyro;

public class Robot extends IterativeRobot {
	PixyCam cam = PixyCam.getInstance();
	Drivetrain drive = Drivetrain.getInstance();
	Joystick stick = new Joystick(0);
	Timer timer = new Timer();
	
	@Override
	public void robotInit() {
	}

	@Override
	public void autonomousInit() {
		//get side and set boolean
		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		timer.reset();
		timer.start();
		if(gameData.charAt(0) == 'L')
		{
			drive.autoLeft(timer);
		} else {
			drive.autoRight(timer);
		}
		
		
	}
	
	@Override
	public void autonomousPeriodic() {
		
	}
	@Override
	public void teleopPeriodic() {
		if(stick.getRawButton(0)) {
		cam.track_cube();
		drive.driveTowardsCube();
		}
		else {
			drive.stop();
		}
	}
	
	@Override
	public void testPeriodic() {
	}
}
