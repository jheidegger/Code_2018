package org.usfirst.frc.team6713.subsystem;

public abstract class Subsystem {
	/*
	 * The Subsystem class is used for each distinct robot function.
	 * Subsystems are coded as state machines and will be given Loops to accomplish tasks 
	 */
	public Loop_Manager Loop_Manager_Instance;
	public Subsystem()
	{
		Loop_Manager_Instance = Loop_Manager.getInstance();
	}
	public abstract void zeroAllSensors();
	
	//called in the test phase before matches. tests all motors and sensors and returns the result
	public abstract boolean checkSystem();
	
	public abstract void registerLoop();
	
}
