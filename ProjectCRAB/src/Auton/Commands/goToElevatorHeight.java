package Auton.Commands;

import Auton.Commands.Command.commandType;
import Subsystem.Elevator;
import Subsystem.Intake;
import Subsystem.Loop;
import Subsystem.Intake.systemStates;

public class goToElevatorHeight extends Command {
	public goToElevatorHeight() {
		super(commandType.triggerBased);
		super.setLoop(new Loop() {
			@Override
			public void onStart() {
				Elevator.getInstance().setWantedState(Subsystem.Elevator.systemStates.POSITION_FOLLOW);
				Elevator.getInstance().setWantedFloor(0.0);
			}
			@Override
			public void onloop() {
				setTrigger(Intake.getInstance().isCubeIn());
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
