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
 		Neutral
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
					break;
				case MovingToPosition:
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
}
