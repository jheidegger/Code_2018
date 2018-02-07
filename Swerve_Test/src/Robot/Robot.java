/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package Robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import Subsystem.*;
import Subsystem.Drivetrain.systemStates;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	private Loop_Manager myLoops = Loop_Manager.getInstance();
	private Drivetrain driveTrain = Drivetrain.getInstance(); 
	//private PixyCam visionCam = PixyCam.getInstance();
	private Controllers controllers = Controllers.getInstance();
	private Joystick velocityStick;
	private Joystick thetaStick;

	@Override
	public void robotInit() {
		velocityStick = new Joystick(Constants.DRIVE_JOYSTICK);
		thetaStick = new Joystick(Constants.GEAR_JOYSTICK);
		//visionCam.registerLoop();
		//driveTrain.swerve(0.0, 0.0, 0.0, Drivetrain.driveCoords.FIELDCENTRIC,Drivetrain.driveType.PERCENTPOWER );
		driveTrain.registerLoop();
		//driveTrain.setSystemState(Drivetrain.systemStates.DRIVE);
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
		driveTrain.swerve(velocityStick.getY(), velocityStick.getX(), thetaStick.getX(), Drivetrain.driveCoords.ROBOTCENTRIC, Drivetrain.driveType.VELOCITY);
	}

	@Override
	public void testPeriodic() {
	}
	
}
