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
	private Controllers controllers = Controllers.getInstance();
	private int id;
	private int direction;
	private double lastAngle; 
	private double currAngle;
	private double angleError;
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
		 currAngle = (steerMotor.getSelectedSensorPosition(0));
		 currAngle = currAngle / 4096.0 * Math.PI;
		 double continuousPath = Math.abs(currAngle - wantedAngle);
		 double discontinuousPath = Math.abs(currAngle-Math.PI) + Math.abs(currAngle-Math.PI);
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
			 discontinuousPath = Math.abs(currAngle-Math.PI) + Math.abs(currAngle-Math.PI);
		 }
		 else
		 {
			 direction = 1;
		 }
		 double targetAngleDisplacement;
		 double targetPosition;
		 if(continuousPath > discontinuousPath)
		 {
			 targetAngleDisplacement = discontinuousPath / Math.PI * 4096;
		 }
		 else
		 {
			 targetAngleDisplacement = continuousPath / Math.PI * 4096;
		 }
		 
		 
		 
	}
	private int radianToEncoderUnits(double Angle)
	{
		
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
