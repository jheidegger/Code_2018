package Auton.Commands;

import Subsystem.Loop;


public class WaitAction extends Command {

	public WaitAction(double waitTime) {
		super(commandType.timeBased,waitTime);
		super.setLoop(new Loop() {
			@Override
			public void onStart() {

			}
			@Override
			public void onloop() {
        //N/A
			}
			@Override
			public void stop() {

			}
		});
    
	}
}
