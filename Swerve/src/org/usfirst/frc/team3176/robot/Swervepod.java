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
	private int direction;
	private double lastAngle; 
	private double currAngle;
	private double angleError;
	private double offset;
	//ft/s to u/s
	private double fps2ups = 10000;//4096.0 / (Constants.WHEELDIAMETER/12.0);
	private double PI = Math.PI;
	private Controllers controller = Controllers.getInstance(); 
	double steerFinal;
	double deltaPhi;
	
	Swervepod(int id,TalonSRX driveMotor,TalonSRX steerMotor) {
		this.id = id;
		this.driveMotor = driveMotor;
		this.steerMotor = steerMotor;
		direction = 1; 
		this.driveMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,0,0);
		this.steerMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute,0,0);
	}
	public void homePod()
	{
		/*
		double positions[] = new double[10];
		for(int i=0; i<positions.length ;i++)
		{
			positions[i] = steerMotor.getSensorCollection().getPulseWidthPosition();
		}
		double averagePosition = 0;
		for(int i=0; i<positions.length ;i++)
		{
			averagePosition += positions[i] / (double) positions.length;
		}
		
		offset = steerMotor.getSensorCollection().getPulseWidthPosition() - steerMotor.getSelectedSensorPosition(0);
		if(id == 0)
		{
			SmartDashboard.putNumber("offset:" , offset);
		}
		*/
		//offset = steerMotor.getSensorCollection().getPulseWidthPosition() - steerMotor.getSelectedSensorPosition(0);
		
		/*if(steerMotor.getSensorCollection().getPulseWidthPosition() > 1995 && steerMotor.getSensorCollection().getPulseWidthPosition() < 2005) {
			steerMotor.set(ControlMode.PercentOutput, .1);
		}*/
		//else {
			steerMotor.setSelectedSensorPosition(steerMotor.getSensorCollection().getPulseWidthPosition(), 0, 10);
		//}
		
		//steerMotor.set(ControlMode.Position, 2000);
		
		
	}
	public void setPod(double Speed, double Angle){

		//steerMotor.set(ControlMode.Position, -500);
		
		Speed  = Speed * fps2ups * direction; 
		//double steerPosition = ((((Angle/(2*PI)) * 4096)-(2000-Constants.OFFSETS[id])+4096*2)%4096);
		double steerPosition = findSteerPosition(Angle); 
		if(Speed == 0) {
			steerPosition = lastAngle; 
		}
		lastAngle = steerPosition;
		driveMotor.set(ControlMode.Velocity, Speed* direction);
		steerMotor.set(ControlMode.Position, steerPosition);
		
	}
	private double findSteerPosition(double wantedAngle){
		/*
		 * test statement by statement  
		 */
		 double rawAngle = (steerMotor.getSelectedSensorPosition(0));// + Constants.OFFSETS[id];
		 currAngle = encoderUnitsToRadian(rawAngle);
		 deltaPhi = wantedAngle - currAngle; 
		 if(Math.abs(deltaPhi) <= (PI/2)) {
			 direction = 1;
		 }
		 else if(deltaPhi < (-PI/2) && deltaPhi >= (-(3/2)*PI)) {
			 direction = -1;
			 deltaPhi = deltaPhi + PI; 
		 }
		 else if(deltaPhi < (-(3/2)*PI)) {
			 direction = 1;
			 deltaPhi = (2*PI) + deltaPhi;
		 }
		 else if(deltaPhi > (PI/2) && deltaPhi <= ((3/2)*PI)) {
			 direction = -1;
			 deltaPhi = deltaPhi - PI;
		 }
		 else if(deltaPhi > ((3/2)*PI)) {
			 direction = 1;
			 deltaPhi = deltaPhi - (2*PI);
		 }
		 steerFinal = ((deltaPhi /(2*PI))*4096);
		 return (rawAngle + steerFinal);
		 /*
		 double continuousPath = Math.abs(currAngle - wantedAngle);
		 double discontinuousPath = Math.abs(PI - currAngle) + Math.abs(PI - wantedAngle);
		 if(Math.min(continuousPath, discontinuousPath)>Math.PI/2)
		 {
			 //reversing the angle
			 direction = -1;
			 if(wantedAngle>0)
			 {
				 wantedAngle-=Math.PI;
			 }
			 else
			 {
				 wantedAngle+=Math.PI;
			 }
			 continuousPath = Math.abs(currAngle - wantedAngle);
			 discontinuousPath = Math.abs(PI - currAngle) + Math.abs(PI - wantedAngle);
		 }
		 else
		 {
			 direction = 1;
		 }
		 double targetAngleDisplacement;
		 double targetPosition;
		 if(discontinuousPath < continuousPath)
		 {
			 targetAngleDisplacement = (discontinuousPath / (Math.PI*2.0)) * 4096;
			 if(currAngle > 0)
			 {
				 targetPosition = rawAngle + targetAngleDisplacement;
			 }
			 else
			 {
				 targetPosition = rawAngle - targetAngleDisplacement;
			 }
		 }
		 else
		 {
			 targetAngleDisplacement = (continuousPath / (Math.PI*2.0)) * 4096;
			 if(currAngle > 0)
			 {
				 targetPosition = rawAngle - targetAngleDisplacement;
			 }
			 else
			 {
				 targetPosition = rawAngle + targetAngleDisplacement;
			 }
		 }
		 return targetPosition;
		 */
	}
	public double getPhi() {
		return deltaPhi;
	}
	public double getFinal() {
		return steerFinal; 
	}
	
	private double radianToEncoderUnits(double Angle)
	{
		Angle += Math.PI;
		double encoderUnits = (Angle / (2.0*Math.PI) * 4096.0);
		return encoderUnits;
		
	}
	private double encoderUnitsToRadian(double EncoderUnits)
	{
		EncoderUnits = EncoderUnits % 4096;
		if(EncoderUnits < 0)
		{
			EncoderUnits+= 4096;
		}
		EncoderUnits -= 2048;
		double Angle = (EncoderUnits/4096.0) * 2 * Math.PI;
		return Angle;
	}
	public double getPosition()
	{
		return steerMotor.getSelectedSensorPosition(0);
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
