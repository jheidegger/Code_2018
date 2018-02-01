package org.usfirst.frc.team6713.subsystem;
import Robot.Constants;
import Robot.Robot;
import Util.PIDLoop;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Drivetrain extends Subsystem {

	private static Drivetrain instance;
	private ADXRS450_Gyro gyro;
	private PixyCam cam = PixyCam.getInstance();
//	private PIDLoop pidX;
//	private PIDLoop pidArea;
//	int centerX = Constants.PIXY_CENTER_X;
//	int targetArea = 1200; 
//	private double output,areaoutput=0;
	
	private ArrayList<Swervepod> Pods;
	private Swervepod upperRight;
	private Swervepod upperLeft;
	private Swervepod lowerLeft;
	private Swervepod lowerRight;
	public TalonSRX[] driveTalon = {new TalonSRX(0), new TalonSRX(2), new TalonSRX(4), new TalonSRX(6)}; 
	public TalonSRX[] gearTalon = {new TalonSRX(1), new TalonSRX(3), new TalonSRX(5), new TalonSRX(7)};
	private double kLength;
	private double kWidth;
	private double kRadius;
	private double rel_max_speed =0;
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
	public enum driveType
	{
		PERCENTPOWER,
		VELOCITY
	}
	private systemStates currentState;
	private systemStates requestedState;
	
	private Drivetrain()
	{
		//instantiate the pods
		upperRight = new Swervepod(0,driveTalon[0], gearTalon[0]);
		upperLeft = new Swervepod(1,driveTalon[1], gearTalon[1]);
		lowerLeft = new Swervepod(2,driveTalon[2], gearTalon[2]);
		lowerRight = new Swervepod(3,driveTalon[3], gearTalon[3]);
				
		//Add instantiated Pods to the array list
		Pods.add(upperRight);
		Pods.add(upperLeft);
		Pods.add(lowerLeft);
		Pods.add(lowerRight);
		
		//setting constants
		kLength = Constants.DRIVETRAINLENGTH;
		kWidth = Constants.DRIVETRAINWIDTH;
		kRadius = Math.sqrt(Math.pow(kLength,2)+Math.pow(kWidth,2));
		//instantiate the gyro
		gyro = new ADXRS450_Gyro();
		angle = (gyro.getAngle()* Math.PI/180.0) % (2*Math.PI);
		//initialize the commands
		forwardCommand = 0.0;
		strafeCommand = 0.0;
		spinCommand = 0.0;
//		pidX = new PIDLoop(.0027,0.000003,0.00002);
//		pidArea = new PIDLoop(.001,0,0,.2);
	}
	
	public static Drivetrain getInstance()
	{
		if(instance == null)
		{
			instance = new Drivetrain();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	@Override
	public void registerLoop()
	{
		Loop_Manager.getInstance().addLoop(new Loop() {
		@Override
		public void onStart() {
		// TODO Auto-generated method stub
			currentState = systemStates.NEUTRAL;
			requestedState = systemStates.NEUTRAL;
						
		}
		@Override
		public void onloop() {
			updateAngle();
			switch(currentState) {
				case NEUTRAL:
					if(requestedState!=currentState)
					{
						currentState = requestedState;
					}
				case DRIVE:
					manualDrive();
				case VISION_TRACK_TANK:
					//vision_track(cam.getAvgX(), cam.getAvgArea());
				default:
					break;			
				}
		}	

		@Override
		public void stop() {
			// TODO Auto-generated method stub
						
		}
		});
	}
	private void updateAngle()
	{
		angle = (gyro.getAngle()* Math.PI/180.0) % (2*Math.PI);
	}
	
	private void manualDrive() {
		double[] podDrive = new double[4];
		double[] podGear = new double[4];
		
		//converting degrees to radians
	  
		
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
		
		for(int idx = 0; idx < 4; idx++) {
			if(podDrive[idx]>rel_max_speed) {
				
			}
		}
		for(int idx = 0; idx < Pods.size(); idx++) {
			(Pods.get(idx)).setPod(podDrive[idx],podGear[idx]); 
		}
		
		
		
	}
	
//	public void vision_track(double avgX, double avgArea) {
//		output = pidX.returnOutput(avgX, centerX);
//		areaoutput = pidArea.returnOutput(avgArea, targetArea);
//	}
	
	@Override
	public void zeroAllSensors() {
		for(int idx = 0; idx < Pods.size(); idx++)
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
	
	public void swerve(double forwardCommand, double strafeCommand, double spinCommand, driveCoords Coords, driveType commandType)
	{
		if(Coords == driveCoords.ROBOTCENTRIC && commandType == driveType.VELOCITY)
		{
			this.forwardCommand = forwardCommand;
			this.strafeCommand = strafeCommand;
			this.spinCommand = spinCommand;
		}
		if(Coords == driveCoords.FIELDCENTRIC && commandType == driveType.VELOCITY)
		{
			final double temp = forwardCommand * Math.cos(angle) + strafeCommand * Math.sin(angle);
		    this.strafeCommand = -forwardCommand * Math.sin(angle) + strafeCommand * Math.cos(angle);
		    this.forwardCommand = temp;
		    this.spinCommand = spinCommand;
		}
		if(Coords == driveCoords.ROBOTCENTRIC && commandType == driveType.PERCENTPOWER)
		{
			this.forwardCommand = forwardCommand*Constants.DRIVETRAINMAXWHEELSPEED;
			this.strafeCommand = strafeCommand*Constants.DRIVETRAINMAXWHEELSPEED;
			this.spinCommand = spinCommand*Constants.DRIVETRAINMAXROTATIONSPEED;
		}
		if(Coords == driveCoords.FIELDCENTRIC && commandType == driveType.PERCENTPOWER)
		{
			final double temp = (forwardCommand * Math.cos(angle) + strafeCommand * Math.sin(angle))*Constants.DRIVETRAINMAXWHEELSPEED;
		    this.strafeCommand = (-forwardCommand * Math.sin(angle) + strafeCommand * Math.cos(angle))*Constants.DRIVETRAINMAXWHEELSPEED;
		    this.forwardCommand = temp;
		    this.spinCommand = spinCommand*Constants.DRIVETRAINMAXROTATIONSPEED;
		}
		
	}


}
	

