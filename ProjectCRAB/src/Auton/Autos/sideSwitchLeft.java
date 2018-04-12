package Auton.Autos;

import Auton.AutoManager;
import Auton.Commands.*;
import Auton.Paths.*;
import Subsystem.Loop;
import edu.wpi.first.wpilibj.hal.ConstantsJNI;

import org.usfirst.frc.team6713.robot.*;

public class sideSwitchLeft extends Auto{
	public static sideSwitchLeft main = new sideSwitchLeft();
	private AutoManager manager = new AutoManager();
	private sideSwitchLeft()
	{
		if(getGameData().substring(0,1).equals("R"))
		{
			manager.qeueCommand(new ParallelCommand(new DriveTrajectory(leftToLeftSwitch.main.get()),
					new GoToElevatorHeight(Constants.SCALEHIGHHEIGHT),
					new DeployIntake()));
			manager.qeueCommand(new SlowScoring(1.0));
			manager.qeueCommand(new GoToElevatorHeight(0.0));
		}
		else
		{
			manager.qeueCommand(new DriveTrajectory(leftToLeftSwitch.main.get()));
		}
		super.registerManager(manager);
	}


}
