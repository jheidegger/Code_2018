package Subsystem;
 
 import org.usfirst.frc.team6713.robot.Constants;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
/**
 * Intake class for 2018 FRC robot 
 * @author Jonathan Heidegger
 *
 */
public class Intake extends Subsystem {
	//the static instance of the intake so that it is not double instantiated. 
 	public static Intake instance = new Intake();
 	
 	private Victor rightSideWheel;
 	private Victor leftSideWheel;
 	private Timer unJamTimer;
 	private DigitalInput isCubeIn;
 	
 	private systemStates currState;
 	private systemStates lastState;
 	private systemStates wantedState;
 	
 	enum systemStates{
 		Intaking,
 		Scoring,
 		UnJamming,
 		Neutral
 	};
 	private Intake()
 	{
 		rightSideWheel = new Victor(Constants.INTAKERIGHTSIDE);
 		leftSideWheel = new Victor(Constants.INTAKELEFTSIDE);
 		isCubeIn = new DigitalInput(0);
 		unJamTimer = new Timer();
 	}
 	/**
 	 * Main control of the intake through the state based logic
 	 * @param wantedState requested state for the system to switch into
 	 */
 	public void setWantedState(systemStates wantedState)
 	{
 		this.wantedState = wantedState;
 	}
 	@Override
 	public void zeroAllSensors() {
 		// N/A
 
 	}
 
 	@Override
 	public boolean checkSystem() {
 		return false;
 	}
 
 	@Override
 	public void registerLoop() {
 		Loop_Manager.getInstance().addLoop(new Loop() {

			@Override
			public void onStart() {
				currState = systemStates.Neutral;
				lastState = systemStates.Neutral;
				wantedState = systemStates.Neutral;
			}

 			@Override
 			public void onloop() {
 				switch(currState)
 				{
 				//idle and wait for commands
 				case Neutral:
 					rightSideWheel.set(0.0);
 					leftSideWheel.set(0.0);
 					lastState = systemStates.Neutral;
 					if(wantedState != systemStates.Neutral)
 					{
 						currState = wantedState;
 					}
 					break;
 				//spins wheels in to intake the Power Cube
 				case Intaking:
 					if(!isCubeIn.get())
 					{
	 					rightSideWheel.set(Constants.INTAKESPEED);
	 					leftSideWheel.set(Constants.INTAKESPEED);
	 					lastState = systemStates.Intaking;
	 					if(wantedState != systemStates.Intaking)
	 					{
	 						currState = wantedState;
	 					}
 					}
 					else
 					{
 						currState = systemStates.Neutral;
 					}
 					break;
 				//spins the wheels outward to score
 				case Scoring:
 					rightSideWheel.set(Constants.INTAKESCORESPEED);
 					leftSideWheel.set(Constants.INTAKESCORESPEED);
 					lastState = systemStates.Scoring;
 					if(wantedState != systemStates.Scoring)
 					{
 						currState = wantedState;
 					}
 					break;
 				//Spins the wheels out then in to right the Power Cubes
 				case UnJamming:
 					if(lastState != systemStates.UnJamming)
 					{
 						unJamTimer.start();
 						unJamTimer.reset();
 					}
 					if(unJamTimer.get()<.2)
 					{
 						rightSideWheel.set(-Constants.INTAKESPEED);
 	 					leftSideWheel.set(-Constants.INTAKESPEED);
 					}
 					else if(unJamTimer.get() < .4)
 					{
 						rightSideWheel.set(Constants.INTAKESPEED);
 	 					leftSideWheel.set(Constants.INTAKESPEED);
 	 					currState = systemStates.Neutral;
 					}
 					else
 					{
 						if(wantedState != systemStates.UnJamming)
 	 					{
 	 						currState = wantedState;
 	 					}
 					}
 					lastState = systemStates.UnJamming;
 					break;
 				}
 				
 			}
 
 			@Override
 			public void stop() {
 				// TODO Auto-generated method stub
 				rightSideWheel.set(0.0);
				leftSideWheel.set(0.0);
 			}
 			
 		});
 
 	}
 
 }
