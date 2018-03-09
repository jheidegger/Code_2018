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
import edu.wpi.first.wpilibj.DriverStation;
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
	private Superstructure superStructure = Superstructure.getInstance();
	//private Elevator elevator = Elevator.getInstance();
	//private Intake intake = Intake.getInstance();
	//private Victor elevator;

	
	@Override
	public void robotInit() {
		//elevator = new Victor(2);
		driveTrain.registerLoop(); //First in array list
		superStructure.registerLoop();
		//intake.registerLoop(); //Second in array list
		//elevator.registerLoop();
		myLoops.startLoops();
	}
	
	@Override
	public void autonomousInit() {
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
	}
	
	@Override
	public void autonomousPeriodic() {
		myLoops.runLoops();
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
		if(controllers.getIntakeButton()) {
			superStructure.setWantedState(Superstructure.wantedStates.Intaking);
			//intake.setWantedState(systemStates.Intaking);
		}
		else if(controllers.getOuttakeButton()) {
			superStructure.setWantedState(Superstructure.wantedStates.Intaking);
			//intake.setWantedState(systemStates.Scoring);
		}
		else if(controllers.unjamButton()) {
			superStructure.setWantedState(Superstructure.wantedStates.Intaking);
			//intake.setWantedState(systemStates.UnJamming);
		}
		else {
			superStructure.setWantedState(Superstructure.wantedStates.Neutral);
			//intake.setWantedState(systemStates.Neutral);
		}
	}

	@Override
	public void testPeriodic() {
	}
	
}
