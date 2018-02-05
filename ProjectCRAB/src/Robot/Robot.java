/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package Robot;

import Subsystem.*;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	private Loop_Manager myLoops = Loop_Manager.getInstance();
	private Drivetrain driveTrain = Drivetrain.getInstance(); 
	private PixyCam visionCam = PixyCam.getInstance();
	private Driverstation driverStation = Driverstation.getInstance();

	@Override
	public void robotInit() {
		visionCam.registerLoop();
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
		if(driverStation.getVisionTrack()) {
			visionCam.setSystemState(PixyCam.systemStates.TRACKING_CUBE);
			driveTrain.setSystemState(Drivetrain.systemStates.VISION_TRACK_TANK);
		}
		else {
			driveTrain.swerve(driverStation.getForward(), driverStation.getStrafe(), driverStation.getRotation(), Drivetrain.driveCoords.FIELDCENTRIC, Drivetrain.driveType.PERCENTPOWER);
		}
	}

	@Override
	public void testPeriodic() {
	}
	
}
