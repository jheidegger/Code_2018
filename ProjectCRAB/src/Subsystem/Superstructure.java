package Subsystem;

import Subsystem.Intake.systemStates;

public class Superstructure {
	private static Superstructure instance = new Superstructure();
	private Intake intake;
	private Elevator elevator;
	private systemStates currState;
 	private systemStates lastState;
 	private wantedStates wantedState;
 	private Loop_Manager loopMan = Loop_Manager.getInstance();
 	public enum systemStates{
 		Intaking,
 		UnJamming,
 		MovingToPosition,
 		Neutral, 
 		Holding,
 		Scoring
 	};
 	public enum wantedStates{
 		Intaking,
 		Switch,
 		ScaleLow,
 		ScaleMid,
 		ScaleHigh,
 		Score,
 		Unjamming,
 		Neutral
 	}
	private Superstructure()
	{
		intake = Intake.getInstance();
		elevator = Elevator.getInstance();
	}
	public static Superstructure getInstance()
	{
		return instance;
	}
	
	public void registerLoop() {
 		loopMan.addLoop(new Loop() {
 			
			@Override
			public void onStart() {
				currState = systemStates.Neutral;
			 	lastState = systemStates.Neutral;
			 	wantedState = wantedStates.Neutral;
			}
			
			@Override
			public void onloop() {
				switch(currState)
				{
				case Intaking:
					if(elevator.getHeight() > .1)
					{
						elevator.setThrottleValue(0.0);
					}
					else
					{
						intake.setWantedState(Intake.systemStates.Intaking);
					}
					lastState = systemStates.Intaking;
					checkState();
					break;
				case MovingToPosition:
					break;
				case Holding:
					break;
				case Neutral:
					break;
				case UnJamming:
					break;
				default:
					break;
				
				}
				
			}

			@Override
			public void stop() {
				// TODO Auto-generated method stub
				
			}
		});

 		}
	private void checkState()
	{
		switch(wantedState)
		{
		case Intaking:
			currState = systemStates.Intaking;
			break;
		case Neutral:
			break;
		case ScaleHigh:
			break;
		case ScaleLow:
			break;
		case ScaleMid:
			break;
		case Score:
			currState = systemStates.Scoring;
			break;
		case Switch:
			break;
		case Unjamming:
			break;
		default:
			break;
		
		}
	}
}
