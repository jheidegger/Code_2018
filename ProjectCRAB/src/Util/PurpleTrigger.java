package Util;

import edu.wpi.first.wpilibj.Joystick;

public class PurpleTrigger {
	private boolean lastActivated; 
	private Joystick joyStick; 
	private int buttonID;
	
	public PurpleTrigger(Joystick joyStick, int buttonID) {
		this.joyStick = joyStick; 
		this.buttonID = buttonID;
	}
	
	public boolean getTrigger() {
		if(joyStick.getRawButton(buttonID)) {
			if(!lastActivated) {
				lastActivated = true; 
				return true; 
			}
		}
		else {
			lastActivated = false;
		}
		return false;
	}
}
