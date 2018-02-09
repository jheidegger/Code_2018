package Subsystem;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import Robot.Constants;
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
	private double fps2ups = 4096.0 / (Constants.WHEELDIAMETER/12.0);
	private double PI = Math.PI;
	
	Swervepod(int id,TalonSRX driveMotor,TalonSRX steerMotor) {
		this.id = id;
		this.driveMotor = driveMotor;
		this.steerMotor = steerMotor;
		direction = 1; 
		this.driveMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,0,0);
		this.steerMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,0,0);
	}
	public void homePod()
	{
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
		offset = averagePosition - steerMotor.getSelectedSensorPosition(0);
	}
	public void setPod(double Speed, double Angle){
		
		double steerPosition = findSteerPosition(Angle);
		if(Speed == 0) {
			steerPosition = lastAngle; 
		}
		lastAngle = steerPosition;
		
		Speed *= fps2ups;
		
		steerMotor.set(ControlMode.Position, steerPosition);
		driveMotor.set(ControlMode.Velocity, Speed);	
	}
	private double findSteerPosition(double wantedAngle){
		 double rawAngle = (steerMotor.getSelectedSensorPosition(0)) + offset;
		 currAngle = encoderUnitsToRadian(rawAngle);
		 double continuousPath = Math.abs(currAngle - wantedAngle);
		 double discontinuousPath = Math.abs(currAngle-Math.PI) + Math.abs(wantedAngle-Math.PI);
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
			 discontinuousPath = Math.abs(currAngle-Math.PI) + Math.abs(wantedAngle-Math.PI);
		 }
		 else
		 {
			 direction = 1;
		 }
		 double targetAngleDisplacement;
		 double targetPosition;
		 if(discontinuousPath < continuousPath)
		 {
			 targetAngleDisplacement = discontinuousPath / (Math.PI*2.0) * 4096;
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
			 targetAngleDisplacement = continuousPath / (Math.PI*2.0) * 4096;
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
		EncoderUnits -= 2048;
		double Angle = (EncoderUnits/4096.0) * 2 * Math.PI;
		return Angle;
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
