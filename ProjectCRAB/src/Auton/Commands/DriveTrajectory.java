package Auton.Commands;

import Auton.PathFollower;
import Auton.Trajectory;
import Subsystem.Intake;
import Subsystem.Intake.systemStates;
import Subsystem.Loop;
import Subsystem.Superstructure;

public class DriveTrajectory extends Command {
  private Trajectory t;
  private PathFollower p;
	public DriveTrajectory(Trajectory t) {
	super(commandType.timeBased,t.getTimeToComplete());
    t.calculateTrajectory();
		super.setLoop(new Loop() {
			@Override
			public void onStart() {
        p = new PathFollower(t);
        p.init();
			}
			@Override
			public void onloop() {
				p.run();
			}
			@Override
			public void stop() {

			}
		});
    
	}
	public void setTrigger(boolean t)
	{
		super.setTrigger(t);
	}
}
