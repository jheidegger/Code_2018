package Auton;

import java.util.ArrayList;

import Auton.Commands.Command;
import edu.wpi.first.wpilibj.Timer;

public class AutoManager {
	private ArrayList<Command> commands;
	private int commandIdx = 0;
	private double startTime;
	private boolean isFirstTime = true;
	public AutoManager()
	{
		commands = new ArrayList<Command>();
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
			for(Command c: commands)
			{
				System.out.println("qeuded: " + c.toString());
			}
			isFirstTime = false;
		}
		else
		{
			commands.get(commandIdx).run();
			if(commands.get(commandIdx).getIsFinished() && commandIdx<commands.size()-1)
			{
				System.out.println("------Finished " + commands.get(commandIdx).toString() + "--------");
				commandIdx++;
			}
		}
	}
	
}
