package Auton;

import java.util.ArrayList;

import Auton.Commands.Command;
import edu.wpi.first.wpilibj.Timer;

public class AutoManager {
	private ArrayList<Command> commands;
	private int commandIdx;
	private double startTime;
	private boolean isFirstTime = true;
	public AutoManager()
	{
		
	}
	public void qeueCommand(Command c)
	{
		commands.add(c);
	}
	public void run()
	{
		if(isFirstTime)
		{
			startTime = Timer.getFPGATimestamp();
			System.out.println("------Auto Started--------");
		}
		else
		{
			commands.get(commandIdx).run();
			if(commands.get(commandIdx).getIsFinished() && commandIdx<commands.size())
			{
				System.out.println("------Finished " + commands.get(commandIdx).toString() + "--------");
				commandIdx++;
			}
		}
	}
	
}
