package Auton.Autos;

import Auton.AutoManager;
import Auton.Commands.*;
import Auton.Paths.*;
import Subsystem.Loop;
import edu.wpi.first.wpilibj.hal.ConstantsJNI;

import org.usfirst.frc.team6713.robot.*;

public class middleSwitchLeft extends Auto{
	public static middleSwitchLeft main = new middleSwitchLeft();
	private AutoManager manager = new AutoManager();
	private middleSwitchLeft()
	{
		manager.qeueCommand(new ParallelCommand(new DriveTrajectory(middleToLeftSwitch.main.get()),new goToElevatorHeight(Constants.SWITCHHEIGHT),new DeployIntake()));
		manager.qeueCommand(new Scoring(1.0));
		manager.qeueCommand(new ParallelCommand(new DriveTrajectory(LeftSwitchToCenterCube.main.get()),new Intaking()));
		manager.qeueCommand(new ParallelCommand(new DriveTrajectory(CenterCubeToLeftSwitch.main.get()),new goToElevatorHeight(Constants.SWITCHHEIGHT)));
		manager.qeueCommand(new Scoring(1.0));
		super.registerManager(manager);
	}


}
