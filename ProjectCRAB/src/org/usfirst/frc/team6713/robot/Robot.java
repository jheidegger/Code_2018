/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6713.robot;

import Auton.Autos.middleSwitch;
import Subsystem.*;
import Subsystem.Intake.systemStates;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Robot extends IterativeRobot {
	
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	private Loop_Manager myLoops = Loop_Manager.getInstance();
	private Drivetrain driveTrain = Drivetrain.getInstance(); 
	private Controller controller = Controller.getInstance();
	private Elevator elevator = Elevator.getInstance();
	private Intake intake = Intake.getInstance();
	int testID = 0;
	String gameData;
	
	@Override
	public void robotInit() {
		driveTrain.registerLoop();
		intake.registerLoop(); 
		elevator.registerLoop();
		myLoops.startLoops();
	}
	
	@Override
	public void autonomousInit() {
		gameData = DriverStation.getInstance().getGameSpecificMessage();
	}
	
	@Override
	public void autonomousPeriodic() {
		myLoops.runLoops();
		middleSwitch.main.run(gameData);
	}

	@Override
	public void teleopPeriodic() {
		myLoops.runLoops();
		
		if(controller.executeAutoButton()) { driveTrain.setSystemState(Drivetrain.systemStates.AUTON);}
		else if(controller.resetAutoButton()) { driveTrain.clearAuton();}
		else { driveTrain.setSystemState(Drivetrain.systemStates.DRIVE);}
		/**
		 * Drive Train States
		 */
		if(controller.getSlowFieldCentricButton() == true)
		{
			driveTrain.swerve(controller.getForward()*Constants.MAXSLOWPERCENTSPEED,
					controller.getStrafe()*Constants.MAXSLOWPERCENTSPEED, 
					-controller.getRotation()*Constants.MAXSLOWPERCENTSPEED, 
					Drivetrain.driveCoords.FIELDCENTRIC, 
					Drivetrain.driveType.PERCENTPOWER);
		}
		else if(controller.getSlowRobotCentricButton() == true)
		{
			driveTrain.swerve(controller.getForward()*Constants.MAXSLOWPERCENTSPEED, 
					controller.getStrafe()*Constants.MAXSLOWPERCENTSPEED, 
					-controller.getRotation()*Constants.MAXSLOWPERCENTSPEED, 
					Drivetrain.driveCoords.ROBOTCENTRIC, 
					Drivetrain.driveType.PERCENTPOWER);
		}
		else
		{
			driveTrain.swerve(controller.getForward(), 
					controller.getStrafe(), 
					-controller.getRotation(), 
					Drivetrain.driveCoords.FIELDCENTRIC, 
					Drivetrain.driveType.PERCENTPOWER);
		}
		/**
		 * Intake States
		 */
		if(controller.getIntakeButton()) {intake.setWantedState(systemStates.Intaking);} 
		else if(controller.getOuttakeButton()) {intake.setWantedState(systemStates.Scoring);}
		else if(controller.unjamButton()) {intake.setWantedState(systemStates.UnJamming);}
		else {intake.setWantedState(systemStates.Neutral);}
		
		if(controller.Stow()) {intake.setPosition(0);}
		else if(controller.unStow()) {intake.setPosition(80);}
		else {intake.setPosition(intake.getCurrPosition()+(controller.actuatorOpenLoop()*20.0));}
		
		//elevator.setWantedState(Elevator.systemStates.OPEN_LOOP);
	}

	@Override
	public void testPeriodic() { 
		myLoops.runLoops();
		if(controller.getSlowFieldCentricButton()) {testID++;}
		switch(testID) {
		case 1:
			driveTrain.swerve(1.0, 0, 0, 
					Drivetrain.driveCoords.FIELDCENTRIC, 
					Drivetrain.driveType.PERCENTPOWER);
		case 2:
			driveTrain.swerve(-1.0, 0.0, 0.0);
		case 3:
			driveTrain.swerve(0.0, 1.0, 0.0);
		case 4:
			driveTrain.swerve(0.0, -1.0, 0.0);
		case 5:
			driveTrain.swerve(0.0, 0.0, 0.5);
		case 6:
			driveTrain.swerve(0.0, 0.0, -0.5);
		case 7:
			intake.setWantedState(Intake.systemStates.Intaking);
		case 8:
			intake.setWantedState(Intake.systemStates.Scoring);
		case 9:
			intake.setWantedState(Intake.systemStates.Stowing);
		case 10: 
			intake.setWantedState(Intake.systemStates.unStowing);
		}	
	}
}
