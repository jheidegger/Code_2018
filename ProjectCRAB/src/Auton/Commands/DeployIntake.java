package Auton.Commands;

import Subsystem.Intake;
import Subsystem.Intake.systemStates;
import Subsystem.Loop;
import Subsystem.Superstructure;

public class DeployIntake extends Command {

	public DeployIntake() {
		super(commandType.timeBased,.5);
		super.setLoop(new Loop() {
			@Override
			public void onStart() {
				Intake.getInstance().setWantedState(systemStates.Neutral);
				Intake.getInstance().setPosition(Intake.getInstance().downPosition_p);
			}
			@Override
			public void onloop() {
				
			}
			@Override
			public void stop() {

			}
		});
	}
}