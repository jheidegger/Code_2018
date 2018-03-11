/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6713.robot;

import Auton.Autos.driveStraight;
import Auton.Autos.leftSwitch;
import Auton.Autos.middleSwitch;
import Subsystem.*;
import Subsystem.Intake.systemStates;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.hal.DIOJNI;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	private static final String auto1 = "middle switch";
	private static final String auto2 = "left switch";
	private static final String auto3 = "right switch";
	private static final String auto4 = "drive straight";
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
		m_chooser.addObject("middle switch", auto1);
		m_chooser.addObject("left switch", auto2);
		m_chooser.addObject("right switch", auto3);
		m_chooser.addObject("drive straight", auto4);
		m_chooser.addDefault("default", "default");
		SmartDashboard.putData("Auto choices", m_chooser);
	}
	
	@Override
	public void autonomousInit() {
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		middleSwitch.setGameData(gameData);
		String selected = m_chooser.getSelected();
		switch(selected)
		{
		case auto1:
			middleSwitch.setGameData(gameData);
			break;
		case auto2:
			driveStraight.setGameData(gameData);
			break;
		case auto3:
			leftSwitch.setGameData(gameData);
			break;
		case auto4:
			leftSwitch.setGameData(gameData);
			break;
		default:
			break;
		}
		
		
	}
	
	@Override
	public void autonomousPeriodic() {
		myLoops.runLoops();
		String selected = m_chooser.getSelected();
		switch(selected)
		{
		case auto1:
			middleSwitch.run();
			break;
		case auto2:
			driveStraight.run();
			break;
		case auto3:
			leftSwitch.run();
			break;
		case auto4:
			leftSwitch.run();
			break;
		}
		//middleSwitch.run();
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
			driveTrain.swerve(controller.getForward(), 
					controller.getStrafe(), 
					-controller.getRotation(), 
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
			driveTrain.swerve(controller.getForward()*Constants.MAXSLOWPERCENTSPEED,
					controller.getStrafe()*Constants.MAXSLOWPERCENTSPEED, 
					-controller.getRotation()*Constants.MAXSLOWPERCENTSPEED * 1.4, 
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
		else if(controller.unStow()) {intake.setPosition(-18000);}
		else {
			intake.setPosition(intake.getCurrPosition()+(controller.actuatorOpenLoop()*2000.0) );
		}

	
		
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
