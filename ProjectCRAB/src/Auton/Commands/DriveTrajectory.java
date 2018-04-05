package Auton.Commands;

import Subsystem.Intake;
import Subsystem.Intake.systemStates;
import Subsystem.Loop;
import Subsystem.Superstructure;

public class DriveTrajectory extends Command {
  private Trajectroy t;
  private PathFollower p;
	public DriveTrajectory() {
		super(timeBased);
		super.setLoop(new Loop() {
			@Override
			public void onStart() {
				t.calculateTrajectory();
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
  public setTrajectory(Trajectroy t)
  {
    this.t = t;
  }
	public setTrigger(boolean t)
	{
		super.setTrigger(t);
	}
}
