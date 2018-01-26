/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6713.robot;

import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {
	PixyCam cam = PixyCam.getInstance();
	Drivetrain drive = Drivetrain.getInstance();
	Joystick stick = new Joystick(0);
	@Override
	public void robotInit() {
	}

	@Override
	public void autonomousInit() {
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
