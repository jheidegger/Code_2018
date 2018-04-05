package Auton.Commands;

import Subsystem.Loop;

public class Command {
	private double timeToComplete = 0.0;
	private boolean isTriggered = false;
	private boolean isFinished =false;
	private Loop commandLoop;
	private boolean firstTime = true;
	private double currTime = 0.0;
	public double startTimeOfCommand;
	public enum commandType {
		timeBased,
		triggerBased
	}
	private commandType type;
	public Command(commandType type, double timeToComplete)
	{
		this.type = type;
		this.timeToComplete = timeToComplete;
	}
	public Command(commandType type)
	{
		this.type = type;
	}
	public void setLoop(Loop l)
	{
		commandLoop = l;
	}
	private boolean checkEndCondition()
	{
		if(type == commandType.timeBased)
		{
			if(currTime <= timeToComplete) {return false;} else {return true;}
		}
		else
		{
			if(!isTriggered) {return false;} else {return true;}
		}
	}
	public void setTrigger(boolean isTriggered)
	{
		this.isTriggered = isTriggered;
	}
	public void run()
	{
		currTime = Time;
		if(firstTime)
		{
			startTimeOfCommand = Timer.getFPGATimestamp();
			commandLoop.onStart();
			firstTime = false;
		}
		else if(checkEndCondition())
		{
			commandLoop.onloop();
		}
		else
		{
			commandLoop.stop();
		}
	}

}
