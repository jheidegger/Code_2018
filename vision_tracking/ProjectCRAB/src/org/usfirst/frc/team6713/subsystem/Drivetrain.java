package org.usfirst.frc.team6713.subsystem;
import Robot.Constants;
import Util.PIDLoop;
import edu.wpi.first.wpilibj.Joystick;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Drivetrain extends Subsystem {

	private static Drivetrain instance;
	private PixyCam cam = PixyCam.getInstance();
	private PIDLoop pidX;
	private PIDLoop pidArea;
	TalonSRX leftMaster = new TalonSRX(0); 
	TalonSRX leftSlave = new TalonSRX(1); 
	TalonSRX rightMaster = new TalonSRX(2); 
	TalonSRX rightSlave = new TalonSRX(3);
	int centerX = Constants.PIXY_CENTER_X;
	int targetArea = 1200; 
	private double output,areaoutput=0;
	/*
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
	*/
	
	public enum systemStates{
		NEUTRAL,
		HOMING,
		DRIVE,
		PARK,
		VISION_TRACK_TANK
	}
	
	private systemStates currentState;
	
	public Drivetrain()
	{
		//instantiate the pods
		/*
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
		*/
		
		pidX = new PIDLoop(.0027,0.000003,0.00002);
		pidArea = new PIDLoop(.001,0,0,.2);
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
		super.Loop_Manager_Instance.addLoop(new Loop() {
		@Override
		public void onStart() {
		// TODO Auto-generated method stub
						
		}
		@Override
		public void onloop() {
			// TODO Auto-generated method stub
			synchronized(Drivetrain.this) {
				switch(currentState) {
				case DRIVE:
					//manualDrive();
				case VISION_TRACK_TANK:
					vision_track(cam.getAvgX(), cam.getAvgArea());			
				}
			}
		}	

		@Override
		public void stop() {
			// TODO Auto-generated method stub
						
		}
		});
	}
	/*
	public synchronized void manualDrive(double forward, double strafe, double spin) {
		double[] podDrive = new double[4];
		double[] podGear = new double[4];
		
		//converting degrees to radians
		//final double angle = gyro.getYaw() * Math.PI / 180.0;
	    //final double temp = forward * Math.cos(angle) + strafe * Math.sin(angle);
	    //strafe = -forward * Math.sin(angle) + strafe * Math.cos(angle);
	    //forward = temp;
		
		//Calculating components
		double a = strafe - spin * kLength/2; 
		double b = strafe + spin * kLength/2; 
		double c = forward - spin * kWidth/2; 
		double d = forward + spin * kWidth/2; 
		
		
		podDrive[0] = Math.sqrt(Math.pow(b, 2)+ Math.pow(c, 2));
		podGear[0] = Math.atan2(b,c);
		
		podDrive[1] = Math.sqrt(Math.pow(b, 2)+ Math.pow(d, 2));
		podGear[1] = Math.atan2(b,d);
		
		podDrive[2] = Math.sqrt(Math.pow(a, 2)+ Math.pow(d, 2));
		podGear[2] = Math.atan2(a,d);
		
		podDrive[3] = Math.sqrt(Math.pow(a, 2)+ Math.pow(c, 2));
		podGear[3] = Math.atan2(a,c);
		
		for(int idx = 0; idx < Pods.size(); idx++) {
			(Pods.get(idx)).setPod(podDrive[idx],podGear[idx]); 
		}
		
		
		
	}
	*/
	public void vision_track(double avgX, double avgArea) {
		output = pidX.returnOutput(avgX, centerX);
		areaoutput = pidArea.returnOutput(avgArea, targetArea);
		
		leftMaster.set(ControlMode.PercentOutput, output-areaoutput);
		leftSlave.set(ControlMode.PercentOutput,output-areaoutput);
		rightMaster.setInverted(true);
		rightSlave.setInverted(true);
		rightMaster.set(ControlMode.PercentOutput,-output-areaoutput);
		rightSlave.set(ControlMode.PercentOutput,-output-areaoutput);
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
	public void setSystemState(systemStates wanted) {
		currentState = wanted;
	}
	

}
	

