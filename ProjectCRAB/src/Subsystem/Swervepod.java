package Subsystem;
import org.usfirst.frc.team6713.robot.Constants;

import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Swervepod extends Subsystem {
	
	private TalonSRX driveMotor;
	private TalonSRX steerMotor;
	private int id;

	private double PI = Math.PI;
	private double kEncoderUnits = Constants.ENCODER_UNITS; //# of ticks on Mag Encoder
	private double kConstants[] = Constants.OFFSETS;
	
	private double lastEncoderPosition; //Previous position in encoder units	
	private double encoderError; //Error in encoder units
	private double encoderPosition; //Current position in encoder units
	private double encoderSetpoint; //Position wanted in encoder units
	private double driveCommand =0 ;
	
	private double radianError; //Error in radians
	private double radianPosition; //Current position in radian units
	
	private double velocitySetpoint; //Wanted velocity in ft/s
	private double fps2ups = Constants.fps2ups; //Converts Feet/s to Encoder Units (770.24)
	
	Swervepod(int id,TalonSRX driveMotor,TalonSRX steerMotor) {
		this.id = id;
		this.driveMotor = driveMotor;
		this.steerMotor = steerMotor;
		this.driveMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,0,0);
		this.steerMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute,0,0);
		
		this.steerMotor.config_kP(0, Constants.SWERVE_kP, 0);
		this.steerMotor.config_kI(0, Constants.SWERVE_kI, 0);
		this.steerMotor.config_kD(0, Constants.SWERVE_kD, 0);
		
		
		this.driveMotor.config_kP(0,Constants.DRIVE_kP, 0);
		this.driveMotor.config_kI(0,Constants.DRIVE_kI, 0);
		this.driveMotor.config_kD(0,Constants.DRIVE_kD, 0);
		this.driveMotor.config_kF(0, Constants.DRIVE_kF,0);
		this.driveMotor.config_IntegralZone(0, Constants.DRIVE_IZONE, 0);
		this.driveMotor.configClosedloopRamp(Constants.DRIVE_RAMPRATE, 0);
		
		this.steerMotor.configAllowableClosedloopError(0, Constants.SWERVE_ALLOWABLE_ERROR, 0);
		this.steerMotor.configAllowableClosedloopError(0, Constants.DRIVE_ALLOWABLE_ERROR, 0);
	}
	
	public void setPod(double Speed, double Angle) {
		
		//Speed = 13.0;
		velocitySetpoint  = Speed * fps2ups;
		encoderSetpoint = findSteerPosition(Angle);
		
		if(Speed != 0.0) {
			steerMotor.set(ControlMode.Position, encoderSetpoint);
			lastEncoderPosition = encoderSetpoint;
		}
		else {
			steerMotor.set(ControlMode.Position, lastEncoderPosition);
		}
		driveMotor.set(ControlMode.Velocity, velocitySetpoint);
		outputToSmartDashboard();
	}
	
	private double findSteerPosition(double wantedAngle) {
		encoderPosition = steerMotor.getSelectedSensorPosition(0) - kConstants[id];
		radianPosition = encoderUnitsToRadian(encoderPosition);
		radianError = wantedAngle - radianPosition;
		if(Math.abs(radianError) > (3*PI/2)) {
			radianError -= Math.copySign(2*PI, radianError);
		}
		else if (Math.abs(radianError) > (PI/2)) {
			radianError -= Math.copySign(PI, radianError);
			velocitySetpoint = -velocitySetpoint;
		}
		encoderError = radianToEncoderUnits(radianError);
		driveCommand = encoderError + encoderPosition + kConstants[id];
		return (driveCommand);
	}
	
	public boolean isStopped() {
		if(Math.abs(driveMotor.getSelectedSensorVelocity(0))<300)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private double radianToEncoderUnits(double Angle) {
		double encoderUnits = ((Angle / (2.0*Math.PI)) * kEncoderUnits);
		return encoderUnits;
	}
	
	private double encoderUnitsToRadian(double EncoderUnits) {
		EncoderUnits = EncoderUnits % kEncoderUnits;
		if(EncoderUnits < 0) {
			EncoderUnits+= kEncoderUnits;
		}
		EncoderUnits -= (kEncoderUnits/2.0);
		double Angle = (EncoderUnits/kEncoderUnits) * (2 * Math.PI);
		return Angle;
	}
	
	//returns between 0 - 4096 absolute 
	public double getPosition() {return (steerMotor.getSelectedSensorPosition(0));}
	
	//encoder units traveled total
	public double getWheelDisplacment() {return(driveMotor.getSelectedSensorPosition(0));}
	
	// encoder units per second
	public double getWheelSpeed() {return (driveMotor.getSelectedSensorVelocity(0));}
	
	public double getPhi() {return radianError;}
	
	public double getFinal() {return encoderError;}
	
	public double getCur() {return encoderPosition;}
	
	public double getSpeed() {return velocitySetpoint;}
	
	public double getRawSpeed() {return driveMotor.getSelectedSensorVelocity(0);}
	
	@Override
	public void zeroAllSensors() {
	}

	@Override
	public boolean checkSystem() {
		return false;
	}
	
	@Override
	public void registerLoop() {
		// N/A
	}

	@Override
	public void outputToSmartDashboard() {
		SmartDashboard.putNumber(id + " enc pos", encoderPosition);
		SmartDashboard.putNumber("ups", fps2ups);
		SmartDashboard.putNumber(id+ " position", steerMotor.getSelectedSensorPosition(0));
		SmartDashboard.putNumber(id + " velocity", driveMotor.getSelectedSensorVelocity(0));
		//SmartDashboard.putNumber(id + " error", 11096 - driveMotor.getSelectedSensorVelocity(0));
	}
}