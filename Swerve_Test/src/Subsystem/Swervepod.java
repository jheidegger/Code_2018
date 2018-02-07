package Subsystem;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

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
	
	private double PI = Math.PI;
	
	Swervepod(int id,TalonSRX driveMotor,TalonSRX steerMotor) {
		this.id = id;
		this.driveMotor = driveMotor;
		this.steerMotor = steerMotor;
		direction = 1; 
		this.driveMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,0,0);
		this.steerMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute,0,0);
	}
	
	public void setPod(double Speed, double Angle){
		//convert Angle from -pi to pi into 0 to 2pi
		if(Angle<0)
		{
			Angle = Angle + 2 * Math.PI;
		}
		System.out.println("Angle: " + Angle);
		
		double steerPosition = ((Angle/(2*PI))*4096)%4096;
		if(controllers.getForward() == 0 && controllers.getStrafe() ==0 && controllers.getRotation() == 0) {
			steerPosition = lastAngle; 
		}
		lastAngle = steerPosition;
		
		//double steerPosition = findSteerPosition(Angle) % 4096;
		System.out.println("Steer: " + steerPosition);
		//System.out.println("Speed: " + Speed);
		Speed = Speed * 10000;
		//System.out.println(Speed);
		//SmartDashboard.putNumber("Steer", (steerMotor.getSelectedSensorPosition(0)%4096));
		//SmartDashboard.putNumber("Absolute", driveMotor.getSelectedSensorVelocity(0));
		steerMotor.set(ControlMode.Position, steerPosition);
		driveMotor.set(ControlMode.Velocity, Speed);	
	}
	
	private double findSteerPosition(double wantedAngle){
		currAngle = (((steerMotor.getSelectedSensorPosition(0))/4096) * (2*PI));
		wantedAngle = (wantedAngle/(2*PI))*4096;
		angleError = Math.abs((wantedAngle - currAngle));
		if(angleError < (PI/2) || angleError > (3*PI/2)) {
			direction = 1;
			return ((wantedAngle/(2*PI))*4096);
		}
		else {
			direction = -1; 
			return (((wantedAngle+PI)/(2*PI))*4096);
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
