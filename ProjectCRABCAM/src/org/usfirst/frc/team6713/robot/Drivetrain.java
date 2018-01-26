package org.usfirst.frc.team6713.robot;

import org.usfirst.frc.team6713.robot.*;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Drivetrain {
	TalonSRX leftMaster = new TalonSRX(0); //fix port
	TalonSRX leftSlave = new TalonSRX(1); //fix port
	TalonSRX rightMaster = new TalonSRX(2); //fix port
	TalonSRX rightSlave = new TalonSRX(3); //fix port
	
	PixyCam cam = PixyCam.getInstance();
	
	PIDLoop turnPID = new PIDLoop(0.0027,0.000003,0.00002);
	PIDLoop drivePID = new PIDLoop(.001,0,0);
	
	
	
	public void driveTowardsCube() {
		double gearOutput = turnPID.returnOutput(cam.getAvgX(), 160);
		double driveOutput = drivePID.returnOutput(cam.getAvgArea(), 1200);
		
		leftMaster.set(ControlMode.PercentOutput, gearOutput+driveOutput);
		leftSlave.set(ControlMode.PercentOutput,gearOutput+driveOutput);
		rightMaster.setInverted(true);
		rightSlave.setInverted(true);
		rightMaster.set(ControlMode.PercentOutput, -gearOutput+driveOutput);
		rightSlave.set(ControlMode.PercentOutput,-gearOutput+driveOutput);
	}
	
}
