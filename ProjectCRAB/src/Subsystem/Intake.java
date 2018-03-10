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
 	private Loop_Manager loopMan = Loop_Manager.getInstance();
 	
 	private Victor rightSideWheel;
 	private Victor leftSideWheel;
 	private Victor stowingMotor;
 	
 	private Timer unJamTimer;
 	private DigitalInput isCubeIn;
 	private DigitalInput isIntakeStowed;
 	
 	private systemStates currState;
 	private systemStates lastState;
 	private systemStates wantedState;

 	private Encoder encoder;
 	private PIDLoop actuatorPID;
 	private double wantedPosition;
 	private double currPosition;
 	
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
 		actuatorPID = new PIDLoop(.2,0,0,.5);
 		rightSideWheel = new Victor(Constants.INTAKERIGHTSIDE);
 		leftSideWheel = new Victor(Constants.INTAKELEFTSIDE);
 		stowingMotor = new Victor(Constants.INTAKESTOWINGMOTOR);
 		encoder = new Encoder(2, 3, false, Encoder.EncodingType.k4X);
 		//isCubeIn = new DigitalInput(0);
 		isIntakeStowed = new DigitalInput(5);
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
 	}
 
 	@Override
 	public boolean checkSystem() {
 		return false;
 	}
 	
 	public void setPosition(double position) {wantedPosition = degreesToEncoder(position);}
 	public double getCurrPosition() {return currPosition;}
 	private void findCurrPosition() {currPosition = (encoderToDegrees(-encoder.getRaw()));}
 	private double encoderToDegrees(double encoderTicks) {return (encoderTicks/2048.0*360.0);}
 	private double degreesToEncoder(double degrees) {return (degrees/360.0*2048);}
 	
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
 				findCurrPosition();
 				if(isIntakeStowed.get()||(wantedPosition > 0 && wantedPosition < 90)) {
 					stowingMotor.set(actuatorPID.returnOutput(currPosition, wantedPosition));
 				}
 				else {
					stowingMotor.set(0);
				}
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
 					else if(unJamTimer.get() < .1)
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
