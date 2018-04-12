package Auton.Autos;

import Auton.AutoManager;
import Auton.Commands.*;
import Auton.Paths.*;
import Subsystem.Loop;
import edu.wpi.first.wpilibj.hal.ConstantsJNI;

import org.usfirst.frc.team6713.robot.*;

public class scaleRightStart extends Auto{
	public static scaleRightStart main = new scaleRightStart();
	private AutoManager manager = new AutoManager();
	private scaleRightStart()
	{
		if(super.getGameData().substring(1,2).equals("L"))
		{
			manager.qeueCommand(new ParallelCommand(new DriveTrajectory(rightToLeftScale.main.get()),
					new GoToElevatorHeight(Constants.SCALEHIGHHEIGHT)));
		}
		else
		{
			manager.qeueCommand(new ParallelCommand(new DriveTrajectory(rightToRightScale.main.get()),
					new GoToElevatorHeight(Constants.SCALEHIGHHEIGHT)));
		}
		manager.qeueCommand(new DeployIntake());
		manager.qeueCommand(new SlowScoring(1.0));
		manager.qeueCommand(new GoToElevatorHeight(0.0));
		super.registerManager(manager);
	}


}
