/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6713.robot;

import Subsystem.*;
import Subsystem.Intake.systemStates;
import Util.PIDLoop;
import Vision.PixyException;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	private Loop_Manager myLoops = Loop_Manager.getInstance();
	private Drivetrain driveTrain = Drivetrain.getInstance(); 
	private Controller controllers = Controller.getInstance();
	private Elevator elevator = Elevator.getInstance();
	private Intake intake = Intake.getInstance();
	//private Victor elevator;
	private Victor actuator;
	
	@Override
	public void robotInit() {
		//elevator = new Victor(2);
		actuator = new Victor(4);
		driveTrain.registerLoop(); //First in array list
		intake.registerLoop(); //Second in array list
		elevator.registerLoop();
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
		elevator.setWantedState(Elevator.systemStates.OPEN_LOOP);
		//pixyCam.track_cube();
		if(controllers.executeAutoButton()) {
			driveTrain.setSystemState(Drivetrain.systemStates.AUTON);
		}
		else if(controllers.resetAutoButton())
		{
			driveTrain.clearAuton();
		}
		else {
			driveTrain.setSystemState(Drivetrain.systemStates.DRIVE);
		}
		actuator.set(controllers.actuatorOpenLoop());
		if(controllers.getSlowFieldCentricButton() == true)
		{
			driveTrain.swerve(controllers.getForward()*Constants.MAXSLOWPERCENTSPEED,
					controllers.getStrafe()*Constants.MAXSLOWPERCENTSPEED, 
					-controllers.getRotation()*Constants.MAXSLOWPERCENTSPEED, 
					Drivetrain.driveCoords.FIELDCENTRIC, 
					Drivetrain.driveType.VELOCITY);
		}
		else if(controllers.getSlowRobotCentricButton() == true)
		{
			driveTrain.swerve(controllers.getForward()*Constants.MAXSLOWPERCENTSPEED, 
					controllers.getStrafe()*Constants.MAXSLOWPERCENTSPEED, 
					-controllers.getRotation()*Constants.MAXSLOWPERCENTSPEED, 
					Drivetrain.driveCoords.ROBOTCENTRIC, 
					Drivetrain.driveType.VELOCITY);
		}
		else
		{
			driveTrain.swerve(controllers.getForward(), 
					controllers.getStrafe(), 
					-controllers.getRotation(), 
					Drivetrain.driveCoords.FIELDCENTRIC, 
					Drivetrain.driveType.VELOCITY);
		}
		if(controllers.getIntakeButton()) {
			intake.setWantedState(systemStates.Intaking);
		}
		else if(controllers.getOuttakeButton()) {
			intake.setWantedState(systemStates.Scoring);
		}
		else if(controllers.unjamButton()) {
			intake.setWantedState(systemStates.UnJamming);
		}
		else {
			intake.setWantedState(systemStates.Neutral);
		}
		//elevator.set(controllers.elevatorOpenLoop());
	}

	@Override
	public void testPeriodic() {
	}
	
}
