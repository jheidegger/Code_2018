package Auton.Commands;

import Auton.Commands.Command.commandType;
import Subsystem.Elevator;
import Subsystem.Intake;
import Subsystem.Loop;
import Subsystem.Intake.systemStates;

public class goToElevatorHeight extends Command {
	private double Height;
	public goToElevatorHeight(double Height) {
		super(commandType.triggerBased);
		this.Height = Height;
		super.setLoop(new Loop() {
			@Override
			public void onStart() {
				Elevator.getInstance().setWantedState(Elevator.systemStates.POSITION_FOLLOW);
				Elevator.getInstance().setWantedFloor(Height);
			}
			@Override
			public void onloop() {
				//Elevator.getInstance().setWantedState(Elevator.systemStates.POSITION_FOLLOW);
				setTrigger(isAtHeight());
			}
			@Override
			public void stop() {

			}
		});
	}
	public boolean isAtHeight()
	{
		if(Math.abs(Elevator.getInstance().getHeight()-Height)<10000)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public void setTrigger(boolean t)
	{
		super.setTrigger(t);
	}

}
