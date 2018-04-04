package Auton.Commands;

import Subsystem.Intake;
import Subsystem.Intake.systemStates;
import Subsystem.Loop;
import Subsystem.Superstructure;

public class Intaking extends Command {

	public Intaking(commandType type) {
		super(type);
		
		super.setLoop(new Loop() {
			@Override
			public void onStart() {
				Intake.getInstance().setWantedState(systemStates.Intaking);
			}
			@Override
			public void onloop() {
				
				Intake.getInstance().isCubeIn()
			}
			@Override
			public void stop() {
				
			}
		});
	}
	
	
	
}
