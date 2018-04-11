/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6713.robot;

import Auton.Autos.*;
import Auton.Autos.Deprecated.TrajectoryTest;
import Auton.Autos.Deprecated.centerSwitchTraj;
import Auton.Autos.Deprecated.middleSwitchTimeBased;
import Auton.Autos.Deprecated.middleSwitchTraj;
import Auton.Autos.Deprecated.sideSwitchLeftTraj;
import Auton.Autos.Deprecated.sideSwitchRightTraj;
import Subsystem.*;
import Subsystem.Intake.systemStates;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	private static final String auto1 = "middle switch";
	private static final String auto2 = "left switch";
	private static final String auto3 = "right switch";
	private static final String auto4 = "drive straight";
	private static final String auto5 = "scale side";
	private static final String auto6 = "middle 2 switch";
	private static final String auto7 = "middle switch old one";
	private static final String auto8 = "trajectory Test";
	private Loop_Manager myLoops = Loop_Manager.getInstance();
	private Drivetrain driveTrain = Drivetrain.getInstance(); 
	private Controller controller = Controller.getInstance();
	private LED led = LED.getInstance();
	private Elevator elevator = Elevator.getInstance();
	private Intake intake = Intake.getInstance();
	//private Pneumatics pneumatics = Pneumatics.getInstance();
	int testID = 0;
	String gameData;
	private boolean isIntakeOpenLoop;
	private boolean isElevatorOpenLoop;
	private double startTime;
	private Timer intakeManualOverrideTimer = new Timer();
	private Timer elevatorManualOverrideTimer = new Timer();
	@Override
	public void robotInit() {
		driveTrain.registerLoop();
		intake.registerLoop(); 
		led.registerLoop();
		elevator.registerLoop();
		//pneumatics.registerLoop();
		myLoops.startLoops();
		CameraServer.getInstance().startAutomaticCapture();
		isIntakeOpenLoop = false;
		isElevatorOpenLoop = false;
		intakeManualOverrideTimer.start();
		elevatorManualOverrideTimer.start();
		m_chooser.addObject("middle switch", auto1);
		m_chooser.addObject("left switch", auto2);
		m_chooser.addObject("right switch", auto3);
		m_chooser.addObject("drive straight", auto4);
		m_chooser.addObject("scale side", auto5);
		m_chooser.addObject("old middle switch", auto7);
		m_chooser.addObject("traj test", auto8);
		//m_chooser.addObject("middle 2 switch", auto6);
		m_chooser.addDefault("default", "default");
		SmartDashboard.putData("Auto choices", m_chooser);
	}
	
	@Override
	public void autonomousInit() {
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		startTime = Timer.getFPGATimestamp();
		Auto.setGameData(gameData);
		//middleSwitch.main.setGameData(gameData);
		//rightSwitch.setGameData(gameData);
		String selected = m_chooser.getSelected();
		
		
		
	}
	
	@Override
	public void autonomousPeriodic() {
		myLoops.runLoops();
		//driveTrain.swerve(.2, 0.0, 0.0, Drivetrain.driveCoords.FIELDCENTRIC,Drivetrain.driveType.PERCENTPOWER);
		//SmartDashboard.putNumber("auto speed", t.getSpeed(Timer.getFPGATimestamp()-startTime));
		String selected = m_chooser.getSelected();
		if(selected.equals(auto1))
		{
			middleSwitchTraj.main.run();
		}
		else if(selected.equals(auto2))
		{
			sideSwitchLeftTraj.main.run();
		}
		else if(selected.equals(auto3))
		{
			sideSwitchRightTraj.main.run();
		}
		else if(selected.equals(auto4))
		{
			driveStraight.main.run();
		}
		else if(selected.equals(auto5))
		{
			middleSwitchLeft.main.run();
			//ScaleAuto.main.run();
		}
		else if(selected.equals(auto6))
		{
			centerSwitchTraj.main.run();
		}
		else if(selected.equals(auto7))
		{
			middleSwitchTimeBased.main.run();
		}
		else if(selected.equals(auto8))
		{
			TrajectoryTest.main.run();
		}
		//middleSwitch.main.run();
		//ScaleAuto.main.run();
		//rightSwitch.run();
		//driveStraight.main.run();
		//driveStraight.run();
	}

	@Override
	public void teleopPeriodic() {
		myLoops.runLoops();
		
		if(controller.executeAutoButton()) { driveTrain.setSystemState(Drivetrain.systemStates.AUTON);}
		else if(controller.getVisionTracking()) { 
			PixyCam.getInstance().track_cube();
			driveTrain.setSystemState(Drivetrain.systemStates.VISION);
		}
		else { driveTrain.setSystemState(Drivetrain.systemStates.DRIVE);}

		/**
		 * Drive Train States
		 */
		if(controller.getSlowFieldCentricButton() == true)
		{
			driveTrain.swerve(controller.getForward()*Constants.MAXSLOWPERCENTSPEED, 
					controller.getStrafe()*Constants.MAXSLOWPERCENTSPEED, 
					controller.getRotation()* 1.15 * Constants.MAXSLOWPERCENTSPEED, 
					Drivetrain.driveCoords.FIELDCENTRIC, 
					Drivetrain.driveType.PERCENTPOWER);
		}
		else if(controller.getSlowRobotCentricButton() == true)
		{
			driveTrain.swerve(controller.getForward()*Constants.MAXSLOWPERCENTSPEED, 
					controller.getStrafe()*Constants.MAXSLOWPERCENTSPEED, 
					controller.getRotation()* 1.15 *Constants.MAXSLOWPERCENTSPEED, 
					Drivetrain.driveCoords.ROBOTCENTRIC, 
					Drivetrain.driveType.PERCENTPOWER);
		}
		else
		{
			driveTrain.swerve(controller.getForward(),
					controller.getStrafe(), 
					controller.getRotation() * 1.15, 
					Drivetrain.driveCoords.FIELDCENTRIC, 
					Drivetrain.driveType.PERCENTPOWER);
		}
		
		/**
		 * Intake States
		 */
		if(controller.Stow() == true && controller.unStow() == true && isIntakeOpenLoop == false)
		{
			isIntakeOpenLoop = true;
			intake.setOpenLoopMode(true);
			intakeManualOverrideTimer.reset();
		}
//		else
//		{	
//			isIntakeOpenLoop = false;
//			intakeManualOverrideTimer.reset();
//		}
		if(controller.getIntakeButton() || controller.getVisionTracking()) {intake.setWantedState(systemStates.Intaking);} 
		else if(controller.getOuttakeButton()) {intake.setWantedState(systemStates.Scoring);}
		else if(controller.unjamButton()) {intake.setWantedState(systemStates.UnJamming);}
		else {intake.setWantedState(systemStates.Neutral);}
		
		if(controller.Stow()) {intake.setPosition(Intake.getInstance().neutralPosition);}
		else if(controller.unStow()) {Intake.getInstance().setPosition(intake.downPosition);}
		else
		{
			if(controller.getintakePositionJoystick()>.05 || controller.getintakePositionJoystick()<-.05)
			{
				intake.setPosition(intake.getCurrPosition()+controller.getintakePositionJoystick()*2000);
			}
		}
		
		/**
		 * Elevator States
		 */
		if(controller.elevatorHigh() == true && controller.elevatorMid() == true && isElevatorOpenLoop == false)
		{
			isElevatorOpenLoop = true;
			elevator.setWantedState(Elevator.systemStates.OPEN_LOOP);
			//elevatorManualOverrideTimer.reset();
		}
//		else
//		{
//			if(elevatorManualOverrideTimer.get()>.5)
//			{
//				isElevatorOpenLoop = false;
//				elevatorManualOverrideTimer.reset();
//			}
//		}
		if(!isElevatorOpenLoop)
		{
			elevator.setWantedState(Elevator.systemStates.POSITION_FOLLOW);
		}
		if(controller.elevatorHigh()) {elevator.setWantedFloor(Constants.SCALEHIGHHEIGHT); }
		else if (controller.elevatorMid()) {elevator.setWantedFloor(Constants.SCALEMIDHEIGHT);}
		else if (controller.elevatorLow()) {elevator.setWantedFloor(Constants.SCALELOWHEIGHT);}
		else if (controller.elevatorSwitch()) {elevator.setWantedFloor(Constants.SWITCHHEIGHT);}
		else 
		{
			if(controller.elevatorPositionJoystick()>.05||controller.elevatorPositionJoystick()<-.05)
			{
			elevator.setWantedFloor(elevator.getHeight()+(controller.elevatorPositionJoystick()*3000.0));
			}
		}
		if(controller.elevatorResetEncoder()) {elevator.zeroAllSensors();}
		
	}
	
	@Override
	public void testPeriodic() { 
		myLoops.runLoops();
		/*
		 * Runs the Pre-Match Checklist
		 */
		SmartDashboard.putNumber("test ID", testID);
		if(controller.getSlowFieldCentricButton()) {testID++;}
		switch(testID) {
		case 1: //Drive Forwards and Field-Centric test
			driveTrain.swerve(1.0, 0, 0, 
					Drivetrain.driveCoords.FIELDCENTRIC, 
					Drivetrain.driveType.PERCENTPOWER);
		case 2: //Drive Backwards
			driveTrain.swerve(-1.0, 0.0, 0.0);
		case 3: //Strafe Right
			driveTrain.swerve(0.0, 1.0, 0.0);
		case 4: //Strafe Left
			driveTrain.swerve(0.0, -1.0, 0.0);
		case 5: //Rotate Clockwise
			driveTrain.swerve(0.0, 0.0, 0.5);
		case 6: //Rotate Counter-Clockwise
			driveTrain.swerve(0.0, 0.0, -0.5);
		case 7:
			driveTrain.swerve(0, 0, 0);
		case 8: //Intake In
			intake.setWantedState(Intake.systemStates.Intaking);
		case 9: //Intake Out
			
			
			intake.setWantedState(Intake.systemStates.Scoring);
		case 10: //Intake UnStow
			intake.setWantedState(Intake.systemStates.Neutral);
			intake.setPosition(intake.downPosition);
		case 11: //Intake Stow
			intake.setPosition(0.0);
		case 12: //Elevator High Position
			elevator.setWantedState(Elevator.systemStates.POSITION_FOLLOW);
			elevator.setWantedFloor(Constants.SCALEHIGHHEIGHT);
		case 13: //Elevator Med Position
			elevator.setWantedFloor(Constants.SCALEMIDHEIGHT);
		case 14: //Elevator in Rest Position
			elevator.setWantedFloor(Constants.SCALELOWHEIGHT);
		case 15: //Set Wheels in Forward Direction
			driveTrain.swerve(.1, 0.0, 0.0,
					Drivetrain.driveCoords.FIELDCENTRIC,
					Drivetrain.driveType.PERCENTPOWER);
		case 16:
			driveTrain.swerve(0, 0, 0);
		}	
	}
}
