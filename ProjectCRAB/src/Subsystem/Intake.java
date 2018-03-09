package Subsystem;
 
import org.usfirst.frc.team6713.robot.Constants;
import Util.PIDLoop;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
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
 	private Controller controller = Controller.getInstance();
 	
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
 	private Encoder encoder;
 	private PIDLoop actuatorPID;
 	
 	private double kStowingTime = Constants.STOWINGTIME;
 	private double kUnStowingTime = Constants.UNSTOWINGTIME;
 	
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
 		actuatorPID = new PIDLoop(.2,0,0);
 		rightSideWheel = new Victor(Constants.INTAKERIGHTSIDE);
 		leftSideWheel = new Victor(Constants.INTAKELEFTSIDE);
 		stowingMotor = new Victor(Constants.INTAKESTOWINGMOTOR);
 		encoder = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
 		//isCubeIn = new DigitalInput(0);
 		//isIntakeStowed = new DigitalInput(1);
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
 				switch(currState)
 				{
 				//idle and wait for commands
 				case Neutral:
 					rightSideWheel.set(0.0);
 					leftSideWheel.set(0.0);
 					stowingMotor.set(controller.actuatorOpenLoop());
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
					/*if(lastState != systemStates.Stowing)
					{
						stowingTimer.start();
						stowingTimer.reset();
					}
					if(stowingTimer.get()<kStowingTime){
						stowingMotor.set(1.0);
					}
					else
					{
						stowingMotor.set(0.0);
						currState = systemStates.Stowed;
					}*/
					stowingMotor.set(actuatorPID.returnOutput(encoder.getRaw(), 0));
					if(encoder.getRaw() > -10 && encoder.getRaw() < 10) {
						wantedState = systemStates.Stowed;
					}
					if(wantedState != currState)
					{
						currState = wantedState;
						stowingMotor.set(0.0);
					}
					break;
				case unStowing:
					/*if(lastState != systemStates.unStowing)
					{
						stowingTimer.start();
						stowingTimer.reset();
					}
					if(stowingTimer.get()<kUnStowingTime)
					{
						stowingMotor.set(-1.0);
					}
					else
					{
						stowingMotor.set(0.0);
						currState = systemStates.Neutral;
					}*/
					stowingMotor.set(actuatorPID.returnOutput(encoder.getRaw(), 409.0));
					if(encoder.getRaw() > -399 && encoder.getRaw() < 419) {
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
 				rightSideWheel.set(0.0);
				leftSideWheel.set(0.0);
 			}
 		});
 	}
 }
