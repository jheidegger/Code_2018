package Auton.Autos;

import Auton.AutoManager;
import Auton.Commands.*;
import Auton.Paths.*;
import Subsystem.Loop;
import edu.wpi.first.wpilibj.hal.ConstantsJNI;

import org.usfirst.frc.team6713.robot.*;

public class scaleLeft extends Auto{
	public static scaleLeft main = new scaleLeft();
	private AutoManager manager = new AutoManager();
	private scaleLeft()
	{
		manager.qeueCommand(new ParallelCommand(new DriveTrajectory(leftToLeftScale.main.get()),
												new GoToElevatorHeight(Constants.SCALEHIGHHEIGHT),
												new DeployIntake()));
		manager.qeueCommand(new SlowScoring(1.0));
		manager.qeueCommand(new GoToElevatorHeight(0.0));
		super.registerManager(manager);
	}


}
