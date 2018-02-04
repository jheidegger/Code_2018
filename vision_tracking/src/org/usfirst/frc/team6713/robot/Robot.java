/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6713.robot;

import java.util.ArrayList;

import org.usfirst.frc.team6713.robot.PixyCam;

import org.usfirst.frc.team6713.robot.PixyException;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	final String defaultAuto = "Default";
	int xSum = 0;
	TalonSRX leftMaster = new TalonSRX(0); //fix port
	TalonSRX leftSlave = new TalonSRX(1); //fix port
	TalonSRX rightMaster = new TalonSRX(2); //fix port
	TalonSRX rightSlave = new TalonSRX(3); //fix port
	final String customAuto = "My Auto";
	String autoSelected;
	Joystick j = new Joystick(0);
	SendableChooser<String> chooser = new SendableChooser<>();
	PixyCam cam = new PixyCam();
	double[] xValues = {0,0,0,0,0,0,0};
	double[] areaValues = {0,0,0,0,0,0,0};
	//ArrayList<Integer> xValues = new ArrayList<Integer>();
	static final int SIZE = 5;
	double averageX;
	int lastX ;
	double speed;
	double speed2;
	double speed3;
	double error;
	double totalError;
	double dError;
	double prevError;
	double output;
	double MAXforwardspeed;
	int averageArea;
	int areaSum;
	int xIDX = 0; 
	int areaIDX = 0;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			break;
		case defaultAuto:
		default:
			// Put default auto code here
			break;
		}
	}

	public void teleopInit() {
//		timer.reset();
//		timer.start();
		xSum = 0;
		//xValues.clear();
	}
	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		try {
			cam.readPacket(1);
		} catch (PixyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		xValues[xIDX] = cam.getX();
		areaValues[areaIDX] = cam.getArea();
		//timer.reset();
//		if(j.getRawButton(1)) {
//			printValues();
//		}
//		widthSum += cam.getWidth();
//		numOfWidths ++;
//		System.out.println("Width: " + 
//		widthSum / numOfWidths);
		double kP = 0.0027; 
		double kI = 0.000003;
		double kD = 0.00002;
		double kkP = .001;
		dError = error-prevError;
		System.out.println("X" +cam.getX());
		//xValues.add(cam.getX());
		System.out.println("Cam: " +cam.getX());
		//if (xValues.size() == SIZE) {
			//for (int value : xValues) {
				//xSum += value;
			//}
		
			
			//lastX = averageX;
			//averageX = xSum / SIZE;
			averageX = (xValues[0]+xValues[1]+xValues[2]+xValues[3]+xValues[4]+xValues[5]+xValues[6])/7;
			System.out.println("Avg: "+ averageX);
			//xValues.clear();
			xSum = 0;
			System.out.println("Average X: " + averageX);
			System.out.println("xSum: "+ xSum);
			//System.out.println("Size of Array List: " + xValues.size());
			System.out.println("LastX: " + lastX);
			
			
			error = 160-averageX ;
			
		

			double errorArea = 1200 - cam.getArea();
			totalError = totalError + error;
			double forward = kkP * errorArea;
			speed = error*kP; 
			speed2 = totalError*kI;
		    speed3 = dError*kD;
		    prevError = error;
		    System.out.println("output " + output);
		    output = speed + speed2 + speed3;
			
			if(output > .6)
			{
				output = .6;
			}
			if(output < -.6)
			{
				output = -.6;
			}
			if(forward > .32)
			{
				forward = .32;
			}
			if(forward < 0)
			{
				forward = 0;
			}
			if(j.getRawButton(1)) {
				leftMaster.set(ControlMode.PercentOutput, output+forward);
				leftSlave.set(ControlMode.PercentOutput,output+forward);
				rightMaster.setInverted(true);
				rightSlave.setInverted(true);
				rightMaster.set(ControlMode.PercentOutput, -output+forward);
				rightSlave.set(ControlMode.PercentOutput,-output+forward);
			}
			else {
				leftMaster.set(ControlMode.PercentOutput,0);
				leftSlave.set(ControlMode.PercentOutput,0);
				rightMaster.set(ControlMode.PercentOutput, 0);
				rightSlave.set(ControlMode.PercentOutput,0);
			}
			xIDX++;
			if(xIDX > 6) {
				xIDX = 0; 
			}
		}
		
		//}
		
	
				
			
		
		
	
//		else 
//		{
//			leftMaster.set(ControlMode.PercentOutput, 0); //change value
//			leftSlave.set(ControlMode.PercentOutput, 0 );//change value
//		}
//	}
//	

	/*public void printValues() {
//		System.out.println("X: " + cam.getX());
//		System.out.println("Y: " + cam.getY());
//		System.out.println("Width: " + cam.getWidth());
//		System.out.println("Height: " + cam.getHeight());
		int middleX= 80; 
		
		
	
		
		//			areaValues.add(cam.getArea());
//			if (areaValues.size() == SIZE) {
//				for (int values : areaValues) {
//					areaSum += values;
//				}
//				averageArea= areaSum / SIZE;
//			}
//			
//			System.out.println("Average Area:" + averageArea);
//			System.out.println("Area: " + cam.getArea());
//			areaValues.clear();
//			areaSum = 0;
 * 
		
		//leftMaster.set(ControlMode.PercentOutput, //Value from -1-1 based on PID loop);
		//leftSlave.set(ControlMode.PercentOutput, //Same value as leftMaster);
		//rightMaster.set(ControlMode.PercentOutput, //Value from -1-1 based on PID loop);
		//rightSlave.set(ControlMode.PercentOutput, //Same value as rightMaster);
				
		
	}*/
	
	
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}