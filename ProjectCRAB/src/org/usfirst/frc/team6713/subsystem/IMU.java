package org.usfirst.frc.team6713.subsystem;

import Util.ADIS16448_IMU;

public class IMU extends Subsystem {

	private ADIS16448_IMU myIMU;
	private static IMU instance; 
	private IMU()
	{
		myIMU = new ADIS16448_IMU();
	}
	public static IMU getInstance()
	{
		if(instance == null)
		{
			instance = new IMU();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	@Override
	public void zeroAllSensors() {
		myIMU.reset();
	}

	@Override
	public boolean checkSystem() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerLoop() {
		// TODO Auto-generated method stub

	}

}
