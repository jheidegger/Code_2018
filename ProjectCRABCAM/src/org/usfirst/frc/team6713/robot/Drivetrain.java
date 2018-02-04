package org.usfirst.frc.team6713.robot;

import org.usfirst.frc.team6713.robot.*;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.interfaces.Gyro;

public class Drivetrain {
	static Drivetrain instance = new Drivetrain(); 
	TalonSRX leftMaster = new TalonSRX(0); //fix port
	TalonSRX leftSlave = new TalonSRX(1); //fix port
	TalonSRX rightMaster = new TalonSRX(2); //fix port
	TalonSRX rightSlave = new TalonSRX(3); //fix port
	
	
	PixyCam cam = PixyCam.getInstance();
	
	PIDLoop turnPID = new PIDLoop(0.0027,0.000003,0.00002);
	PIDLoop drivePID = new PIDLoop(.001,0,0);
	
	public static Drivetrain getInstance() {
		return instance; 
	}
	public Drivetrain() {
		
	}
	
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
	public void stop() {
		leftMaster.set(ControlMode.PercentOutput, 0);
		leftSlave.set(ControlMode.PercentOutput,0);
		rightMaster.set(ControlMode.PercentOutput, 0);
		rightSlave.set(ControlMode.PercentOutput,0);
	}
	public void autoRight(Timer timer)
	{
		if(timer.get()<=.5)
		{
			leftMaster.set(ControlMode.PercentOutput, .5);
			leftSlave.set(ControlMode.PercentOutput, .5);
			rightMaster.set(ControlMode.PercentOutput, -.5);
			rightSlave.set(ControlMode.PercentOutput, -.5);
		}
		stop();
		
	}
	public void autoRight2()
	{
		
	}
	public void autoLeft(Timer timer)
	{
		if(timer.get()<=.5)
		{
			leftMaster.set(ControlMode.PercentOutput, -.5);
			leftSlave.set(ControlMode.PercentOutput, -.5);
			rightMaster.set(ControlMode.PercentOutput, .5);
			rightSlave.set(ControlMode.PercentOutput, .5);
		}
		stop();
		
	}
	public void autoLeft2()
	{
		
	}
//	public void driveRight(double timer, double gyro) {
//		if (timer <= .5) {
//			leftMaster.set(ControlMode.PercentOutput, .5);
//			leftSlave.set(ControlMode.PercentOutput, .5);
//			rightMaster.set(ControlMode.PercentOutput, -.5);
//			rightSlave.set(ControlMode.PercentOutput, -.5);
//		}
//		if (timer > .5 && timer < 1) {
//			stop();
//		}
//		if (gyro <= 90) {
//			leftMaster.set(ControlMode.PercentOutput, .5);
//			leftSlave.set(ControlMode.PercentOutput, .5);
//			rightMaster.set(ControlMode.PercentOutput, .5);
//			rightSlave.set(ControlMode.PercentOutput, .5);
//		}
//		if (gyro > 90) {
//			stop();
//		}
//	}
	
}
