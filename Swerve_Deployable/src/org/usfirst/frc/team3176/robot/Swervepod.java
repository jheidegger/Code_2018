package org.usfirst.frc.team3176.robot;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.usfirst.frc.team3176.robot.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Swervepod extends Subsystem {
	
	private TalonSRX driveMotor;
	private TalonSRX steerMotor;
	private int id;

	private double PI = Math.PI;
	private double kEncoderUnits = Constants.ENCODER_UNITS; //# of ticks on Mag Encoder
	
	private double lastAngle; //Previous position in encoder units
	
	private double encoderError; //Error in encoder units
	private double encoderPosition; //Current position in encoder units
	private double positionSetpoint; //Position wanted in encoder units
	
	private double radianError; //Error in radians
	private double radianPosition; //Current position in radian units
	
	private double velocitySetpoint; //Wanted velocity in ft/s
	private double fps2ups = 10000;//4096.0 / (Constants.WHEELDIAMETER/12.0);

	private boolean forwardPath = false;
	
	Swervepod(int id,TalonSRX driveMotor,TalonSRX steerMotor) {
		this.id = id;
		this.driveMotor = driveMotor;
		this.steerMotor = steerMotor;
		this.driveMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,0,0);
		this.steerMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute,0,0);
	}
	
	public void setPod(double Speed, double Angle){
		
		velocitySetpoint  = Speed * fps2ups * 1.5;

		positionSetpoint = findSteerPosition(Angle); 
		
		if(Speed == 0) {
			positionSetpoint = lastAngle; 
		}
		lastAngle = positionSetpoint;
		
		driveMotor.set(ControlMode.Velocity, -velocitySetpoint);
		steerMotor.set(ControlMode.Position, positionSetpoint);
	}
	
	private double findSteerPosition(double wantedAngle){
		 encoderPosition = steerMotor.getSelectedSensorPosition(0) -Constants.OFFSETS[id];
		 radianPosition = encoderUnitsToRadian(encoderPosition);
		 radianError = wantedAngle - radianPosition; 
		 if (!forwardPath) { //Uses Reverse Method
			 if (Math.abs(radianError) > (PI/2)) {
			      radianError -= Math.copySign(PI, radianError);
			      
			      velocitySetpoint = -velocitySetpoint;
			    }
			 }
		 else {
			 if (Math.abs(radianError) > (PI)) {
			      radianError -= Math.copySign((2*PI), radianError);
			 }
		}
		 encoderError = radianToEncoderUnits(radianError);
		 return (encoderPosition + encoderError + Constants.OFFSETS[id]);
	}
	
	public double getPhi() {
		return radianError;
	}
	
	public double getFinal() {
		return encoderError; 
	}
	
	public double getCur() {
		return radianPosition;
	}
	
	public double getSpeed() {
		return velocitySetpoint;
	}
	
	private double radianToEncoderUnits(double Angle)
	{
		double encoderUnits = ((Angle / (2.0*Math.PI)) * kEncoderUnits);
		return encoderUnits;
	}
	
	private double encoderUnitsToRadian(double EncoderUnits)
	{
		EncoderUnits = EncoderUnits % kEncoderUnits;
		if(EncoderUnits < 0)
		{
			EncoderUnits+= kEncoderUnits;
		}
		EncoderUnits -= (kEncoderUnits/2.0);
		double Angle = (EncoderUnits/kEncoderUnits) * (2 * Math.PI);
		return Angle;
	}
	
	public double getPosition()
	{
		return (steerMotor.getSelectedSensorPosition(0) % kEncoderUnits);
	}
	
	public void outputToSmartDashboard(Controllers.driveStationStates type) {
		switch(type) {
		case All:
			
		case Programming:
			
		case Driver:
		}
	}
	
	@Override
	public void zeroAllSensors() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean checkSystem() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void registerLoop() {
		// N/A
	}

}