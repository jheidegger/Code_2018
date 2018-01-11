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
		upperRight = new Swervepod();
		upperLeft = new Swervepod();
		lowerRight = new Swervepod();
		lowerLeft = new Swervepod();
		
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
			
		}

		@Override
		public void stop() {
			// TODO Auto-generated method stub
			
		}

	};
	

}
