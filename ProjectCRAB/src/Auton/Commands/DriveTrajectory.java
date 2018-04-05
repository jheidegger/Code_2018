package Auton.Commands;

import Subsystem.Intake;
import Subsystem.Intake.systemStates;
import Subsystem.Loop;
import Subsystem.Superstructure;

public class DriveTrajectory extends Command {
  private Trajectroy t;
  private PathFollower p;
	public DriveTrajectory(Trajectroy t) {
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
    super(commandType.timeBased,t.getTimeToComplete());
	}
	public setTrigger(boolean t)
	{
		super.setTrigger(t);
	}
}
