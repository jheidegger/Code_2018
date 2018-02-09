package Subsystem;
import Robot.Constants;
import Robot.Robot;
import Util.PIDLoop;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Drivetrain extends Subsystem {

	private static Drivetrain instance = new Drivetrain();
	
	//private ArrayList<Swervepod> Pods;
	private Loop_Manager loopMan = Loop_Manager.getInstance();
	private ArrayList<Swervepod> Pods;// = {null,null,null,null};
	private Swervepod upperRight;
	private Swervepod upperLeft;
	private Swervepod lowerLeft;
	private Swervepod lowerRight;
	
	public TalonSRX[] driveTalon = {new TalonSRX(1)};//, new TalonSRX(2), new TalonSRX(4), new TalonSRX(6)}; 
	public TalonSRX[] gearTalon = {new TalonSRX(0)};//, new TalonSRX(3), new TalonSRX(5), new TalonSRX(7)};
	
	private double kLength;
	private double kWidth;
	private double kRadius;
	
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
		VISION_TRACK_TANK
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
		//upperLeft = new Swervepod(1,driveTalon[1], gearTalon[1]);
		//lowerLeft = new Swervepod(2,driveTalon[2], gearTalon[2]);
		//lowerRight = new Swervepod(3,driveTalon[3], gearTalon[3]);
		
		//Instantiate array list
		Pods = new ArrayList<Swervepod>();
				
		//Add instantiated Pods to the array list
		//Pods[0] = upperRight;
		//Pods[1] = upperLeft;
		//Pods[2] = lowerLeft;
		//Pods[3] = lowerRight;
		Pods.add(upperRight);
		//Pods.add(upperLeft);
		//Pods.add(lowerLeft);
		//Pods.add(lowerRight);
		
		//setting constants
		kLength = Constants.DRIVETRAINLENGTH;
		kWidth = Constants.DRIVETRAINWIDTH;
		kRadius = Math.sqrt(Math.pow(kLength,2)+Math.pow(kWidth,2));
		kMaxSpeed = Constants.DRIVETRAINMAXWHEELSPEED;
		kMaxRotation = Constants.DRIVETRAINMAXROTATIONSPEED;
		
		//instantiate the gyro
		//gyro = new ADXRS450_Gyro();
		//angle = (gyro.getAngle()* Math.PI/180.0) % (2*Math.PI);
		//home all the pods
		for(Swervepod pod:Pods)
		{
			pod.homePod();
		}
		//initialize the commands
		forwardCommand = 0.0;
		strafeCommand = 0.0;
		spinCommand = 0.0;
	}
	
	public static Drivetrain getInstance(){
		return instance;
	}

	private void updateAngle(){
		//angle = (gyro.getAngle()* Math.PI/180.0) % (2*Math.PI);
	}
	
	private void manualDrive() {
		double[] podDrive = new double[4];
		double[] podGear = new double[4];
		
		//Calculating components
		double a = strafeCommand - spinCommand * kLength/2; 
		double b = strafeCommand + spinCommand * kLength/2; 
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
		
		if(rel_max_speed > kMaxSpeed) {
			for(int idx = 0; idx < Pods.size(); idx++) {
				podDrive[idx] /= rel_max_speed;
			}
		}
		
		for(int idx = 0; idx < Pods.size(); idx++) {
			//sending power from 0 to 13.5 ft/s and position -pi to pi
			Pods.get(idx).setPod(podDrive[0],podGear[0]); 
		}
	}
	
	public void swerve(double forwardCommand, double strafeCommand, double spinCommand, driveCoords Coords, driveType commandType){
		if(Coords == driveCoords.ROBOTCENTRIC) {
			this.forwardCommand = forwardCommand;
			this.strafeCommand = strafeCommand;
			this.spinCommand = spinCommand/17;
			if(commandType == driveType.PERCENTPOWER) {
				this.forwardCommand *= kMaxSpeed;
				this.strafeCommand *= kMaxSpeed;
				this.spinCommand *= kMaxRotation;
			}
		}
		else {
			final double temp = forwardCommand * Math.cos(angle) + strafeCommand * Math.sin(angle);
		    this.strafeCommand = -forwardCommand * Math.sin(angle) + strafeCommand * Math.cos(angle);
		    this.forwardCommand = temp;
		    this.spinCommand = spinCommand;
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
			currentState = systemStates.DRIVE;
			requestedState = systemStates.DRIVE;		
		}
		@Override
		public void onloop() {
			//updateAngle();
			switch(currentState) {
				case NEUTRAL:
					if(requestedState!=currentState)
					{
						currentState = requestedState;
					}
				case DRIVE:
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
	

