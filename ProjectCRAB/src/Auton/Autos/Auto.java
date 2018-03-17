package Auton.Autos;

import Subsystem.Loop;

public class Auto {
	private Loop autoLoop;
	public static String gameData;
	private  boolean firstTime = true;
	public Auto()
	{
		
	}
	public void registerLoop(Loop loop)
	{
		autoLoop = loop;
	}
	public void setGameData(String gD)
	{
		gameData = gD;
	}
	public static String getGameData()
	{
		return gameData;
	}
	public void run()
	{
		if(firstTime)
		{
			autoLoop.onStart();
			firstTime = false;
		}
		else
		{
			autoLoop.onloop();
		}
	}	
}


