package Auton.Commands;

import java.util.ArrayList;

import Auton.Commands.Command.commandType;
import Subsystem.Intake;
import Subsystem.Loop;
import Subsystem.Intake.systemStates;

public class ParallelCommand extends Command {
	private ArrayList<Command> commands;
	public ParallelCommand(Command[] c) {
		super(commandType.triggerBased);
		commands = new ArrayList<Command>();
		for (Command command: c)
		{
			commands.add(command);
		}
		super.setLoop(new Loop() {
			@Override
			public void onStart() {
				
			}
			@Override
			public void onloop() {
				for(Command command:commands)
				{
					command.run();
				}
				setTrigger(areAllCommandsFinished());
			}
			@Override
			public void stop() {

			}
		});
	}
	private boolean areAllCommandsFinished()
	{
		boolean isDone = true;
		for(Command command:commands)
		{
			if(!command.getIsFinished())
			{
				isDone = false;
			}
		}
		return isDone;
	}
	public void setTrigger(boolean t)
	{
		super.setTrigger(t);
	}
}
