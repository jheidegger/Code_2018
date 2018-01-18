package org.usfirst.frc.team6713.subsystem;
import Robot.Constants;
import java.util.ArrayList;

public class Drivetrain extends Subsystem {

	private static Drivetrain instance;
	private ArrayList<Swervepod> Pods;
	private Swervepod upperRight;
	private Swervepod upperLeft;
	private Swervepod lowerLeft;
	private Swervepod lowerRight;
	private double kLength;
	private double kWidth;
	private double kRadius;
	
	private enum systemStates{
		NEUTRAL,
		HOMING,
		DRIVE,
		PARK
	}
	private systemStates currentState;
	private Drivetrain()
	{
		//instantiate the pods
		upperRight = new Swervepod(0);
		upperLeft = new Swervepod(1);
		lowerLeft = new Swervepod(2);
		lowerRight = new Swervepod(3);
		
		//Add instantiated Pods to the array list
		Pods.add(upperRight);
		Pods.add(upperLeft);
		Pods.add(lowerLeft);
		Pods.add(lowerRight);
		//setting constants
		kLength = Constants.DRIVETRAINLENGTH;
		kWidth = Constants.DRIVETRAINWIDTH;
		kRadius = Math.sqrt(Math.pow(kLength,2)+Math.pow(kWidth,2));
		
	}
	public Drivetrain getInstance()
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
	public void zeroAllSensors() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean checkSystem() {
		// TODO Auto-generated method stub
		return false;
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
								
							}
						}
					}	

					@Override
					public void stop() {
						// TODO Auto-generated method stub
						
					}
			});
		}
		public synchronized void manualDrive(double forward, double strafe, double spin) {
			double[] podDrive = new double[4];
			double[] podGear = new double[4];
			//converting degrees to radians
			//final double angle = gyro.getYaw() * Math.PI / 180.0;
		    //final double temp = forward * Math.cos(angle) + strafe * Math.sin(angle);
		    //strafe = -forward * Math.sin(angle) + strafe * Math.cos(angle);
		    //forward = temp;
			//Calculating components
			double VUpperRightx = strafe + spin * kLength / 2;
			double VUpperRighty = strafe - spin * kWidth / 2;
			
			double VUpperLeftx = strafe + spin * kLength / 2;
			double VUpperLefty = strafe + spin * kWidth / 2;
			
			double VLowerLeftx = strafe - spin * kLength / 2;
			double VLowerLefty = strafe + spin * kWidth / 2;
			
			double VLowerRightx = strafe - spin * kLength / 2;
			double VLowerRighty = strafe - spin * kWidth / 2;
			
			double UpperRightV = Math.sqrt(Math.pow(VUpperRightx, 2)+ Math.pow(VUpperRighty, 2));
			double UpperRightA = Math.atan2(VUpperRighty, VUpperRightx);
			
			double UpperLeftV = Math.sqrt(Math.pow(VUpperLeftx, 2)+ Math.pow(VUpperLefty, 2));
			double UpperLeftA = Math.atan2(VUpperLefty, VUpperLeftx);
			
			double LowerLeftV = Math.sqrt(Math.pow(VLowerLeftx, 2)+ Math.pow(VLowerLefty, 2));
			double LowerLeftA = Math.atan2(VLowerLefty, VLowerLeftx);
			
			double LowerRightV = Math.sqrt(Math.pow(VLowerRightx, 2)+ Math.pow(VLowerRighty, 2));
			double LowerRightA = Math.atan2(VLowerRighty, VLowerRightx);
			
			
			
		
			
			
		}

}
	

