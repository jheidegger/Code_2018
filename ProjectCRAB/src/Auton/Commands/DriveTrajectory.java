package Auton.Commands;

import Auton.PathFollower;
import Auton.Trajectory;
import Subsystem.Intake;
import Subsystem.Intake.systemStates;
import Subsystem.Loop;
import Subsystem.*;

public class DriveTrajectory extends Command {
  private Trajectory t;
  private PathFollower p;
	public DriveTrajectory(Trajectory t) {
		super(commandType.timeBased,t.getTimeToComplete());
		super.setLoop(new Loop() {
			@Override
			public void onStart() {
				p = new PathFollower(t);
				p.init();
			}
			@Override
			public void onloop() {
				//System.out.println("running path");
				p.run();
			}
			@Override
			public void stop() {
				Drivetrain.getInstance().swerve(0.0,0.0,0.0);
			}
		});
    
	}

}
