package org.usfirst.frc.team6713.subsystem;

import java.util.ArrayList;

public class Loop_Manager {
	private static Loop_Manager instance;
	private ArrayList<Loop> Loops;
	//private constructor
	private Loop_Manager()
	{
		
	}
	// So there is only ever one instance of the loop manager
	public static Loop_Manager getInstance()
	{
		if(instance == null)
		{
			instance = new Loop_Manager();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	public void addLoop(Loop L)
	{
		Loops.add(L);
	}
	public void startLoops()
	{
		for(Loop L:Loops)
		{
			L.onStart();
		}
	}
	public void runLoops()
	{
		for(Loop L:Loops)
		{
			L.onloop();;
		}
	}
	public void endLoops()
	{
		for(Loop L:Loops)
		{
			L.stop();
		}
	}

}
