package Subsystem;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;

import org.usfirst.frc.team6713.robot.Constants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import Util.PIDLoop;
import Vision.PixyException;

public class Drivetrain extends Subsystem {

	private static Drivetrain instance = new Drivetrain();

	private Loop_Manager loopMan = Loop_Manager.getInstance();
	
	private Controller controller = Controller.getInstance(); 
	private PixyCam cam = PixyCam.getInstance();
	
	private ADXRS450_Gyro gyro;
	
	private ArrayList<Swervepod> Pods;
	private Swervepod upperRight;
	private Swervepod upperLeft;
	private Swervepod lowerLeft;
	private Swervepod lowerRight;
	
	public TalonSRX[] driveTalon = {new TalonSRX(1), new TalonSRX(2), new TalonSRX(3), new TalonSRX(4)}; 
	public TalonSRX[] gearTalon = {new TalonSRX(11), new TalonSRX(22), new TalonSRX(33), new TalonSRX(44)};
	
	private double kLength;
	private double kWidth;
	private double kRadius;
	
	private PIDLoop pidLoop;
	private PIDLoop pidForward;
	
	private double kMaxSpeed;
	private double kMaxRotation;
	
	private double rel_max_speed = 0;
	private double angle;
	private double forwardCommand;
	private double strafeCommand;
	private double spinCommand;
	
	public enum systemStates{
		NEUTRAL,
		HOMING,
		DRIVE,
		PARK,
		VISION
	}
	
	public enum driveCoords{
		ROBOTCENTRIC,
		FIELDCENTRIC,
	}
	
	public enum driveType{
		PERCENTPOWER,
		VELOCITY
	}
	
	private systemStates currentState;
	private systemStates requestedState;
	
	private Drivetrain(){
		//instantiate the pods
		upperRight = new Swervepod(0,driveTalon[0], gearTalon[0]);
		upperLeft = new Swervepod(1,driveTalon[1], gearTalon[1]);
		lowerLeft = new Swervepod(2,driveTalon[2], gearTalon[2]);
		lowerRight = new Swervepod(3,driveTalon[3], gearTalon[3]);
		
		//Instantiate array list
		Pods = new ArrayList<Swervepod>();
		
		pidLoop = new PIDLoop(0.0007,0,0);
		pidForward = new PIDLoop(0.001,0,0);
				
		//Add instantiated Pods to the array list
		Pods.add(upperRight);
		Pods.add(upperLeft);
		Pods.add(lowerLeft);
		Pods.add(lowerRight);
		
		//setting constants
		kLength = Constants.DRIVETRAINLENGTH;
		kWidth = Constants.DRIVETRAINWIDTH;
		kRadius = Math.sqrt(Math.pow(kLength,2)+Math.pow(kWidth,2));
		kMaxSpeed = Constants.DRIVETRAINMAXWHEELSPEED;
		kMaxRotation = Constants.DRIVETRAINMAXROTATIONSPEED;
		
		//instantiate the gyro
		gyro = new ADXRS450_Gyro();
		gyro.calibrate();
		updateAngle();

		//initialize the commands
		forwardCommand = 0.0;
		strafeCommand = 0.0;
		spinCommand = 0.0;
	}
	
	public static Drivetrain getInstance(){
		return instance;
	}

	private void updateAngle(){
		//-pi to pi 0 = straight ahead
		angle = -((gyro.getAngle()* Math.PI/180.0))% (2*Math.PI);
		SmartDashboard.putNumber("Angle", angle);
	}
	
	private void manualDrive() {
		double[] podDrive = new double[4];
		double[] podGear = new double[4];
		
		//Calculating components
		double a = strafeCommand + spinCommand * kLength/2; 
		double b = strafeCommand - spinCommand * kLength/2; 
		double c = forwardCommand - spinCommand * kWidth/2; 
		double d = forwardCommand + spinCommand * kWidth/2; 
		
		
		
		podDrive[0] = Math.sqrt(Math.pow(b, 2)+ Math.pow(c, 2));
		podGear[0] = Math.atan2(b,c);
		
		podDrive[1] = Math.sqrt(Math.pow(b, 2)+ Math.pow(d, 2));
		podGear[1] = Math.atan2(b,d);
		
		podDrive[2] = Math.sqrt(Math.pow(a, 2)+ Math.pow(d, 2));
		podGear[2] = Math.atan2(a,d);
		
		podDrive[3] = Math.sqrt(Math.pow(a, 2)+ Math.pow(c, 2));
		podGear[3] = Math.atan2(a,c);
		
		for(int idx = 0; idx < Pods.size(); idx++) {
			if(podDrive[idx]>rel_max_speed) {
				rel_max_speed = podDrive[idx];
			}
		}
		
		if(rel_max_speed > 1) {
			for(int idx = 0; idx < Pods.size(); idx++) {
				podDrive[idx] /= rel_max_speed;
			}
		}
				
		for(int idx = 0; idx < Pods.size(); idx++) {
			//sending power from 0 to 13.5 ft/s and position -pi to pi
			Pods.get(idx).setPod(podDrive[idx],podGear[idx]); 
		}
	}
	
	public void setPod(double angle, double speed, Swervepod[] pods)
	{
		for(int idx = 0; idx < pods.length; idx++) {
			//Give angle from -PI to PI and power of 0 to 13.5 ft/s
			Pods.get(idx).setPod(speed,angle); 
		}
	}
	
	public void resetGyro() {
		gyro.reset();
	}
	public double getAngle()
	{
		return angle;
	}
	public void swerve(double forwardCommand, double strafeCommand, double spinCommand, driveCoords Coords, driveType commandType){
		if(Coords == driveCoords.ROBOTCENTRIC) {
			this.forwardCommand = forwardCommand * 1.5;
			this.strafeCommand = strafeCommand;
			this.spinCommand = -spinCommand/12.0;
			if(commandType == driveType.PERCENTPOWER) {
				this.forwardCommand *= kMaxSpeed;
				this.strafeCommand *= kMaxSpeed;
				this.spinCommand *= kMaxRotation;
			}
		}
		else {
			final double temp = forwardCommand * Math.cos(angle) + strafeCommand * Math.sin(angle);
		    this.strafeCommand = (-forwardCommand * Math.sin(angle) + strafeCommand * Math.cos(angle))*1.5;
		    this.forwardCommand = temp;
		    this.spinCommand = -spinCommand/12.0;
		    if(commandType == driveType.PERCENTPOWER) {
				this.forwardCommand *= kMaxSpeed;
				this.strafeCommand *= kMaxSpeed;
				this.spinCommand *= kMaxRotation;
			}
		}
	}
	
	@Override
	public void zeroAllSensors() {
		for(int idx = 0; idx < 4; idx++)
		{
			Pods.get(idx).zeroAllSensors();
		}
	}

	@Override
	public boolean checkSystem() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setSystemState(systemStates wanted) {
		requestedState = wanted;
	}
	
	@Override
	public void registerLoop()
	{
		loopMan.addLoop(new Loop() {
		@Override
		public void onStart() {
			currentState = systemStates.VISION;
			requestedState = systemStates.VISION;		
		}
		@Override
		public void onloop() {
			if(controller.getGyroReset()) {
				resetGyro();
			}
			updateAngle();
			switch(currentState) {
				case NEUTRAL:
					if(requestedState!=currentState)
					{
						currentState = requestedState;
					}
				case DRIVE:
					manualDrive();
				case VISION:
					spinCommand = -pidLoop.returnOutput(cam.getAvgX(), 160);
					forwardCommand = pidForward.returnOutput(cam.getAvgArea(), 5000);
					if(forwardCommand < -.2) {
						forwardCommand = 0; 
					}
					SmartDashboard.putNumber("Area", cam.getAvgArea());
					SmartDashboard.putNumber("Camera", cam.getAvgX());
					manualDrive();
					
				default:
					break;			
				}
		}	
		@Override
		public void stop() {				
		}
	});
	}
}
	

