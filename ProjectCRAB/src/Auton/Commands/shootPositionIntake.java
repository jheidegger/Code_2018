package Auton.Commands;

import Subsystem.Intake;
import Subsystem.Intake.systemStates;
import Subsystem.Loop;
import Subsystem.Superstructure;

public class shootPositionIntake extends Command {

	public shootPositionIntake() {
		super(commandType.timeBased,.5);
		super.setLoop(new Loop() {
			@Override
			public void onStart() {
				Intake.getInstance().setWantedState(systemStates.Neutral);
				Intake.getInstance().setPosition(-8000);
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
