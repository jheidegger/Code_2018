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

		manager.qeueCommand(new DriveTrajectory(middleToLeftSwitch.main.get()));
		manager.qeueCommand(new Scoring(1.0));
		manager.qeueCommand(new ParallelCommand(new DriveTrajectory(LeftSwitchToCenterCube.main.get()),new Intaking()));
		manager.qeueCommand(new DriveTrajectory(CenterCubeToLeftSwitch.main.get()));
		manager.qeueCommand(new Scoring(1.0));
		super.registerManager(manager);
	}


}
