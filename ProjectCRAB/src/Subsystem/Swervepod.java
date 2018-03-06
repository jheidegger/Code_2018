package Subsystem;
import org.usfirst.frc.team6713.robot.Constants;

import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Swervepod extends Subsystem {
	
	private TalonSRX driveMotor;
	private TalonSRX steerMotor;
	private int id;
	private Controller controller = Controller.getInstance();

	private double PI = Math.PI;
	private double kEncoderUnits = Constants.ENCODER_UNITS; //# of ticks on Mag Encoder
	
	private double lastAngle; //Previous position in encoder units
	
	private double encoderError; //Error in encoder units
	private double encoderPosition; //Current position in encoder units
	private double positionSetpoint; //Position wanted in encoder units
	private double driveCommand =0 ;
	
	private double radianError; //Error in radians
	private double radianPosition; //Current position in radian units
	private double radianCommand;
	
	private double velocitySetpoint; //Wanted velocity in ft/s
	private double fps2ups = 17000;//4096.0 / (Constants.WHEELDIAMETER/12.0);

	private boolean forwardPath = false;
	
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
		this.driveMotor.config_kI(0,  Constants.DRIVE_kI, 0);
		this.driveMotor.config_kD(0,Constants.DRIVE_kD, 0);
		this.driveMotor.config_kF(0, Constants.DRIVE_kF,0);//DRIVE_kF, 0);
		this.driveMotor.configClosedloopRamp(Constants.DRIVE_RAMPRATE, 0);
		
		this.steerMotor.configAllowableClosedloopError(0, Constants.SWERVE_ALLOWABLE_ERROR, 0);
		this.steerMotor.configAllowableClosedloopError(0, Constants.DRIVE_ALLOWABLE_ERROR, 0);
	}
	
	public void setPod(double Speed, double Angle){
		
		velocitySetpoint  = Speed * fps2ups;
		
		positionSetpoint = findSteerPosition(Angle); 
		
		if(Speed != 0) {
			steerMotor.set(ControlMode.Position, positionSetpoint);
		}
		SmartDashboard.putNumber(id+ " position", steerMotor.getSelectedSensorPosition(0));
		SmartDashboard.putNumber(id + " velocity", driveMotor.getSelectedSensorVelocity(0));
		
		driveMotor.set(ControlMode.Velocity, velocitySetpoint);
	}
	
	private double findSteerPosition(double wantedAngle){
		 encoderPosition = steerMotor.getSelectedSensorPosition(0) -Constants.OFFSETS[id];
		 SmartDashboard.putNumber(id + " enc pos", encoderPosition);
		 radianPosition = encoderUnitsToRadian(encoderPosition);
		radianError = wantedAngle - radianPosition;
		 //radianCommand = encoderUnitsToRadian(driveCommand-Constants.OFFSETS[id]); 
		 //radianError = wantedAngle - radianCommand; 
		 if (!forwardPath) { //Uses Reverse Method
			 if(Math.abs(radianError) > (3*PI/2)) {
				 radianError -= Math.copySign(2*PI, radianError);
			 }
			 else if (Math.abs(radianError) > (PI/2)) {
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
		 driveCommand = encoderError + encoderPosition+Constants.OFFSETS[id];
		 //driveCommand = driveCommand  + encoderError + Constants.OFFSETS[id];
		 return (driveCommand);
	}
	
	public double getPhi() {
		return radianError;
	}
	
	public double getFinal() {
		return encoderError; 
	}
	
	public double getCur() {
		return encoderPosition;
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
	//returns between 0 - 4096 absolute 
	public double getPosition()
	{
		return (steerMotor.getSelectedSensorPosition(0));
	}
	//encoder units traveled total
	public double getWheelDisplacment()
	{
		return(driveMotor.getSelectedSensorPosition(0));
	}
	// encoder units per second
	public double getWheelSpeed()
	{
		return (driveMotor.getSelectedSensorVelocity(0));
	}
	
	public void outputToSmartDashboard(Controller.driveStationStates type) {
		switch(type) {
		case ALL:
			
		case PROGRAMMING:
			
		case DRIVER:
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