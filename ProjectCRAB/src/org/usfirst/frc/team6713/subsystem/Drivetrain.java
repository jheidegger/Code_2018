package org.usfirst.frc.team6713.subsystem;

import java.util.ArrayList;

public class Drivetrain extends Subsystem {

	private static Drivetrain instance;
	private ArrayList<Swervepod> Pods;
	private Swervepod upperRight;
	private Swervepod upperLeft;
	private Swervepod lowerRight;
	private Swervepod lowerLeft;
	
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
		lowerRight = new Swervepod(2);
		lowerLeft = new Swervepod(3);
		
		//Add instantiated Pods to the array list
		Pods.add(upperRight);
		Pods.add(upperLeft);
		Pods.add(lowerRight);
		Pods.add(lowerLeft);
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
	
	private Loop main = new Loop()
	{

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
					
				}
			}
		}	

		@Override
		public void stop() {
			// TODO Auto-generated method stub
			
		}
		
		public synchronized void manualDrive() {
			double[] pDrive = new double[4];
			double[] pGear = new double[4];
			
			final double angle = gyro.getYaw() * Math.PI / 180.0;
		    final double temp = forward * Math.cos(angle) + strafe * Math.sin(angle);
		    strafe = -forward * Math.sin(angle) + strafe * Math.cos(angle);
		    forward = temp;
		}

	};
	

}
