package Auton.Autos;

import Auton.AutoManager;
import Auton.Commands.*;
import Auton.Paths.*;
import Subsystem.Loop;

public class middleSwitchLeft extends Auto{
	public static middleSwitchLeft main = new middleSwitchLeft();
	private AutoManager manager = new AutoManager();
	private middleSwitchLeft()
	{
		if(super.getGameData().substring(0,1).equals("L"))
		{
			manager.qeueCommand(new DriveTrajectory(middleToLeftSwitch.main.get()));
		}
		else
		{
			manager.qeueCommand(new DriveTrajectory(middleToRightSwitch.main.get()));
		}
		manager.qeueCommand(new Scoring(2.0));
		super.registerManager(manager);
	}
	
	
}
