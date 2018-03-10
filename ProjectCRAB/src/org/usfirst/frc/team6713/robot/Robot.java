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
	private Controller controllers = Controller.getInstance();
	//private Superstructure superStructure = Superstructure.getInstance();
	private Elevator elevator = Elevator.getInstance();
	private Intake intake = Intake.getInstance();
	//private Victor elevator;
	int testID = 0;
	String gameData;
	
	@Override
	public void robotInit() {
		//elevator = new Victor(2);
		driveTrain.registerLoop(); //First in array list
		//superStructure.registerLoop();
		intake.registerLoop(); //Second in array list
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
		
		if(controllers.executeAutoButton()) { driveTrain.setSystemState(Drivetrain.systemStates.AUTON);}
		else if(controllers.resetAutoButton()) { driveTrain.clearAuton();}
		else { driveTrain.setSystemState(Drivetrain.systemStates.DRIVE);}
		
		if(controllers.getSlowFieldCentricButton() == true)
		{
			driveTrain.swerve(controllers.getForward()*Constants.MAXSLOWPERCENTSPEED,
					controllers.getStrafe()*Constants.MAXSLOWPERCENTSPEED, 
					-controllers.getRotation()*Constants.MAXSLOWPERCENTSPEED, 
					Drivetrain.driveCoords.FIELDCENTRIC, 
					Drivetrain.driveType.PERCENTPOWER);
		}
		else if(controllers.getSlowRobotCentricButton() == true)
		{
			driveTrain.swerve(controllers.getForward()*Constants.MAXSLOWPERCENTSPEED, 
					controllers.getStrafe()*Constants.MAXSLOWPERCENTSPEED, 
					-controllers.getRotation()*Constants.MAXSLOWPERCENTSPEED, 
					Drivetrain.driveCoords.ROBOTCENTRIC, 
					Drivetrain.driveType.PERCENTPOWER);
		}
		else
		{
			driveTrain.swerve(controllers.getForward(), 
					controllers.getStrafe(), 
					-controllers.getRotation(), 
					Drivetrain.driveCoords.FIELDCENTRIC, 
					Drivetrain.driveType.PERCENTPOWER);
		}
		if(controllers.getIntakeButton()) {intake.setWantedState(systemStates.Intaking);}
		else if(controllers.getOuttakeButton()) {intake.setWantedState(systemStates.Scoring);}
		else if(controllers.unjamButton()) {intake.setWantedState(systemStates.UnJamming);}
		else {intake.setWantedState(systemStates.Neutral);
		}
		//elevator.setWantedState(Elevator.systemStates.OPEN_LOOP);
		

	}

	@Override
	public void testPeriodic() { 
		myLoops.runLoops();
		if(controllers.getSlowFieldCentricButton()) {testID++;}
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
