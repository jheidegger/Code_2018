package Auton.Autos;

import Auton.AutoManager;
import Auton.Commands.*;
import Auton.Paths.middleToLeftSwitch;
import Subsystem.Loop;

public class middleSwitchManager extends Auto{
	public static middleSwitchManager main = new middleSwitchManager();
	private AutoManager manager = new AutoManager();
	private middleSwitchManager()
	{
		if(super.getGameData().substring(0,1).equals("L"))
		{
			manager.qeueCommand(new DriveTrajectory(middleToLeftSwitch.main.get()));
		}
		manager.qeueCommand(new Scoring(2.0));
		super.registerLoop(new Loop() {
			@Override
			public void onStart() {
			}

			@Override
			public void onloop() {
				manager.run();
			}

			@Override
			public void stop() {
				
			}
		});
	}
	
	
}
