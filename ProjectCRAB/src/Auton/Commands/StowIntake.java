package Auton.Commands;

import Subsystem.Intake;
import Subsystem.Intake.systemStates;
import Subsystem.Loop;
import Subsystem.Superstructure;

public class StowIntake extends Command {

	public StowIntake() {
		super(commandType.timeBased,.5);
		super.setLoop(new Loop() {
			@Override
			public void onStart() {
				Intake.getInstance().setWantedState(systemStates.Neutral);
				Intake.getInstance().setPosition(0.0);
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
