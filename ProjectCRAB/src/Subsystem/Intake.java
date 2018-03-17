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
 	public final double downPosition = -17000;
 	
 	public enum systemStates{
 		Intaking,
 		Scoring,
 		UnJamming,
 		Stowing,
 		unStowing,
 		unStowed,
 		Stowed,
 		Homing,
 		OpenLoop,
 		Neutral, openNeutral
 	};
 	
 	public static Intake getInstance() {
 		return instance;
 	}
 	
 	private Intake()
 	{
 		actuatorPID = new PIDLoop(.0006,0,0.00002,.35);
 		rightSideWheel = new Victor(Constants.INTAKERIGHTSIDE);
 		leftSideWheel = new Victor(Constants.INTAKELEFTSIDE);
 		stowingMotor = new Victor(Constants.INTAKESTOWINGMOTOR);
 		encoder = new Encoder(2, 3, false, Encoder.EncodingType.k4X);
 		encoder.reset();
 		//isCubeIn = new DigitalInput(0);
 		isIntakeStowed = new DigitalInput(5);
 		unJamTimer = new Timer();
 		currState = systemStates.Homing;
 	}
 	/**
 	 * Main control of the intake through the state based logic
 	 * @param wantedState requested state for the system to switch into
 	 */
 	public void setWantedState(systemStates wantedState) {this.wantedState = wantedState;}
 	@Override public void zeroAllSensors() {}
 	@Override public boolean checkSystem() {return false;}
 	
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
 	
 	public void setPosition(double position) {wantedPosition = position;}//degreesToEncoder(position);}
 	public double getCurrPosition() {return currPosition;}
 	private void findCurrPosition() {currPosition = encoder.getRaw();}
 	private double encoderToDegrees(double encoderTicks) {return (encoderTicks/2048.0*360.0);}
 	private double degreesToEncoder(double degrees) {return (degrees/360.0*2048);}
 	
 	private void checkState()
 	{
 		if(wantedState != currState)
		{
			currState = wantedState;
		}
 	}
 	
 	private void closedLoopControl()
 	{
 		
 		
		currPosition = encoder.getRaw();
		
		if(wantedPosition < downPosition)
		{
			wantedPosition = downPosition;
		}
		if(wantedPosition > 0.0)
		{
			wantedPosition = 0.0;
		}
		if(isIntakeStowed.get()) {
			stowingMotor.set(actuatorPID.returnOutput(currPosition, wantedPosition));
			
		}
		else {
			encoder.reset();
			if(actuatorPID.returnOutput(currPosition, wantedPosition)<0)
			{
				stowingMotor.set(actuatorPID.returnOutput(currPosition, wantedPosition));
			}
			else
			{
				stowingMotor.set(actuatorPID.returnOutput(currPosition, 0.0));
			}
		}
 	}
 	
 	@Override
 	public void registerLoop() {
 		loopMan.addLoop(new Loop() {
			@Override
			public void onStart() {
				currState = systemStates.Homing;
				lastState = systemStates.Homing;
				wantedState = systemStates.Homing;
			}
 			@Override
 			public void onloop() {
 				SmartDashboard.putNumber("curr position", encoder.getRaw());
 				SmartDashboard.putBoolean("Stowed",isIntakeStowed.get());
 				SmartDashboard.putString("State", currState.toString());
 				SmartDashboard.putNumber("wantedPosition", wantedPosition);
 				switch(currState)
 				{
 				case openNeutral:
 					rightSideWheel.set(0.0);
 					leftSideWheel.set(0.0);
 					lastState = systemStates.Neutral;
 					checkState();
 					break;
 				//idle and wait for commands
 				case Neutral:
 					rightSideWheel.set(0.0);
 					leftSideWheel.set(0.0);
 					lastState = systemStates.Neutral;
 					closedLoopControl();
 					checkState();
 					break;
 				case Stowed:
 					wantedPosition = 0.0;
 					checkState();
 					break;
 				case unStowed:
 					wantedPosition = downPosition;
 					checkState();
 					break;
 				case Homing:
 					if(isIntakeStowed.get())
 					{
 						stowingMotor.set(.2);
 						rightSideWheel.set(0.0);
	 					leftSideWheel.set(0.0);
 					}
 					else
 					{
 						encoder.reset();
 						stowingMotor.set(0.0);
 						rightSideWheel.set(0.0);
	 					leftSideWheel.set(0.0);
 						
 						currState = systemStates.Neutral;
 					}
 					lastState = systemStates.Homing;
 					break;
 					//spins wheels in to intake the Power Cube
 				case Intaking:
 					//if(!isCubeIn.get())
 					//{
	 					rightSideWheel.set(Constants.INTAKESPEED);
	 					leftSideWheel.set(-Constants.INTAKESPEED);
	 					lastState = systemStates.Intaking;
	 					wantedPosition = downPosition;
	 					//closedLoopControl();
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
 					//closedLoopControl();
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
 						rightSideWheel.set(Constants.INTAKESPEED*.5);
 	 					leftSideWheel.set(-Constants.INTAKESPEED*.5);
 					}
 					else if(unJamTimer.get() < .1)
 					{
 						rightSideWheel.set(-Constants.INTAKESPEED);
 	 					leftSideWheel.set(Constants.INTAKESPEED);
 					}
 					else
 					{
 						checkState();
 					}
 					lastState = systemStates.UnJamming;
 					break;
 				case OpenLoop:
 					rightSideWheel.set(0.0);
 					leftSideWheel.set(0.0);
 					if(!isIntakeStowed.get())
 					{
 						if(controller.actuatorOpenLoop()<0) {
 							stowingMotor.set(controller.actuatorOpenLoop()*.2);
 						}
 						else
 						{
 							stowingMotor.set(0.0);
 						}
 					}
 					else
 					{
 						stowingMotor.set(controller.actuatorOpenLoop()*.2);
 					}
 					checkState();
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
