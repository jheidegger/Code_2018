package Subsystem;
 
 import org.usfirst.frc.team6713.robot.Constants;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * Intake class for 2018 FRC robot 
 * @author Jonathan Heidegger
 *
 */
public class Intake extends Subsystem {
	//the static instance of the intake so that it is not double instantiated. 
 	private static Intake instance = new Intake();
 	
 	private Victor rightSideWheel;
 	private Victor leftSideWheel;
 	private Victor stowingMotor;
 	private Timer unJamTimer;
 	private Timer stowingTimer;
 	private DigitalInput isCubeIn;
 	private DigitalInput isIntakeStowed;
 	
 	private systemStates currState;
 	private systemStates lastState;
 	private systemStates wantedState;
 	private Loop_Manager loopMan = Loop_Manager.getInstance();
 	public enum systemStates{
 		Intaking,
 		Scoring,
 		UnJamming,
 		Stowing,
 		unStowing,
 		Stowed,
 		Neutral
 	};
 	public static Intake getInstance() {
 		return instance;
 	}
 	private Intake()
 	{
 		rightSideWheel = new Victor(Constants.INTAKERIGHTSIDE);
 		leftSideWheel = new Victor(Constants.INTAKELEFTSIDE);
 		stowingMotor = new Victor(Constants.INTAKESTOWINGMOTOR);
 		isCubeIn = new DigitalInput(0);
 		isIntakeStowed = new DigitalInput(1);
 		unJamTimer = new Timer();
 		stowingTimer = new Timer();
 	}
 	/**
 	 * Main control of the intake through the state based logic
 	 * @param wantedState requested state for the system to switch into
 	 */
 	public void setWantedState(systemStates wantedState)
 	{
 		this.wantedState = wantedState;
 	}
 	public boolean isStowed()
 	{
 		if(currState == systemStates.Stowed)
 		{
 			return true;
 		}
 		else
 		{
 			return false;
 		}
 	}
 	@Override
 	public void zeroAllSensors() {
 		// N/A
 
 	}
 
 	@Override
 	public boolean checkSystem() {
 		return false;
 	}
 	private void checkState()
 	{
 		if(wantedState != currState)
		{
			currState = wantedState;
		}
 	}
 	@Override
 	public void registerLoop() {
 		loopMan.addLoop(new Loop() {

			@Override
			public void onStart() {
				currState = systemStates.Neutral;
				lastState = systemStates.Neutral;
				wantedState = systemStates.Neutral;
			}

 			@Override
 			public void onloop() {
 				SmartDashboard.putString("intakeState", currState.toString());
 				//System.out.print("imworking");
 				switch(currState)
 				{
 				//idle and wait for commands
 				case Neutral:
 					rightSideWheel.set(0.0);
 					leftSideWheel.set(0.0);
 					lastState = systemStates.Neutral;
 					checkState();
 					break;
 				//spins wheels in to intake the Power Cube
 				case Intaking:
 					//if(!isCubeIn.get())
 					//{
	 					rightSideWheel.set(Constants.INTAKESPEED);
	 					leftSideWheel.set(-Constants.INTAKESPEED);
	 					lastState = systemStates.Intaking;
	 					checkState();
 					//}
 					//else
 					//{
 					//	currState = systemStates.Neutral;
 					//}
 					break;
 				//spins the wheels outward to score
 				case Scoring:
 					rightSideWheel.set(Constants.INTAKESCORESPEED);
 					leftSideWheel.set(-Constants.INTAKESCORESPEED);
 					lastState = systemStates.Scoring;
 					checkState();
 					break;
 				//Spins the wheels out then in to right the Power Cubes
 				case UnJamming:
 					if(lastState != systemStates.UnJamming)
 					{
 						unJamTimer.start();
 						unJamTimer.reset();
 					}
 					if(unJamTimer.get()<.05)
 					{
 						rightSideWheel.set(Constants.INTAKESPEED);
 	 					leftSideWheel.set(-Constants.INTAKESPEED);
 					}
 					else if(unJamTimer.get() < .4)
 					{
 						rightSideWheel.set(-Constants.INTAKESPEED);
 	 					leftSideWheel.set(Constants.INTAKESPEED);
 	 					//currState = systemStates.Neutral;
 					}
 					else
 					{
 						checkState();
 					}
 					lastState = systemStates.UnJamming;
 					break;
				case Stowed:
					//move to any state through unStowing
					if(wantedState != currState)
					{
						currState = systemStates.unStowing;
					}
					lastState = systemStates.Stowed;
					stowingMotor.set(0.0);
					break;
				case Stowing:
					if(lastState != systemStates.Stowing)
					{
						stowingTimer.start();
						stowingTimer.reset();
					}
					if(stowingTimer.get()<Constants.STOWINGTIME)
					{
						stowingMotor.set(1.0);
					}
					else
					{
						stowingMotor.set(0.0);
						currState = systemStates.Neutral;
					}
					if(wantedState != currState)
					{
						currState = wantedState;
						stowingMotor.set(0.0);
					}
					break;
				case unStowing:
					if(lastState != systemStates.unStowing)
					{
						stowingTimer.start();
						stowingTimer.reset();
					}
					if(stowingTimer.get()<Constants.UNSTOWINGTIME)
					{
						stowingMotor.set(-1.0);
					}
					else
					{
						stowingMotor.set(0.0);
						currState = systemStates.Neutral;
					}
					if(wantedState != currState)
					{
						currState = wantedState;
						stowingMotor.set(0.0);
					}
					break;
				default:
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
