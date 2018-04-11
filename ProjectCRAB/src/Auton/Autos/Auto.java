
package Auton.Autos;

import Auton.AutoManager;
import Subsystem.Loop;

public class Auto {
	private Loop autoLoop;
	private AutoManager manager;
	public static String gameData;
	private  boolean firstTime = true;
	public Auto()
	{
		
	}
	public void registerLoop(Loop loop)
	{
		autoLoop = loop;
	}
	public void registerManager(AutoManager manager)
	{
		this.manager = manager;
	}
	public static void setGameData(String gD)
	{
		gameData = gD;
	}
	public static String getGameData()
	{
		return gameData;
	}
	public void run()
	{
		if(autoLoop == null)
		{
			manager.run();
		}
		else
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
}


