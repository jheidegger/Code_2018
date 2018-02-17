/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6713.robot;

import Subsystem.*;
import Util.PIDLoop;
import Vision.PixyException;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	private Loop_Manager myLoops = Loop_Manager.getInstance();
	private Drivetrain driveTrain = Drivetrain.getInstance(); 
	private Controller controllers = Controller.getInstance();
	private PixyCam pixyCam = PixyCam.getInstance();
	private PIDLoop pidVision;
	private double error;
	private double output;
	
	@Override
	public void robotInit() {
		driveTrain.registerLoop();
		myLoops.startLoops();
	}
	
	@Override
	public void autonomousInit() {
	}
	
	@Override
	public void autonomousPeriodic() {
		myLoops.runLoops();
	}

	@Override
	public void teleopPeriodic() {
		myLoops.runLoops();
		pixyCam.track_cube();
		driveTrain.swerve(-controllers.getForward(), -controllers.getStrafe(), -controllers.getRotation(), Drivetrain.driveCoords.FIELDCENTRIC, Drivetrain.driveType.VELOCITY);

	}

	@Override
	public void testPeriodic() {
	}
	
}
