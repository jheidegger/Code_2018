package Subsystem;
 
import org.usfirst.frc.team6713.robot.Constants;
import org.usfirst.frc.team6713.robot.Robot;

import Util.PIDLoop;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
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
 	private DigitalInput isCubeInLeft;
 	private DigitalInput isCubeInRight;
 	private DigitalInput isIntakeStowed;
 	
 	private systemStates currState;
 	private systemStates lastState;
 	private systemStates wantedState;

 	private Encoder encoder;
 	private PIDLoop actuatorPID;
 	private double wantedPosition;
 	private double currPosition;
 	public final double downPosition = -17000;
 	// -1 full left - 1 full right
 	private double cubePosition = 0.0;
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
 		actuatorPID = new PIDLoop(.00008,0,0.0,.4);
 		rightSideWheel = new Victor(Constants.INTAKERIGHTSIDE);
 		leftSideWheel = new Victor(Constants.INTAKELEFTSIDE);
 		stowingMotor = new Victor(Constants.INTAKESTOWINGMOTOR);
 		encoder = new Encoder(2, 3, false, Encoder.EncodingType.k4X);
 		encoder.reset();
 		isCubeInLeft = new DigitalInput(6);
 		isCubeInRight = new DigitalInput(7);
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
 				SmartDashboard.putBoolean("CubeIn", isCubeInLeft.get());
 				SmartDashboard.putBoolean("CubeIn", isCubeInRight.get());
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
 					if(lastState == systemStates.Scoring || lastState == systemStates.Intaking) {
 						wantedPosition = 0.0;
 						cubePosition = 0.0;
 					}
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
 						stowingMotor.set(.3);
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
 					if(isCubeInLeft.get() || isCubeInRight.get())
 					{
 						double rightCurrent = Drivetrain.getInstance().getPDP().getCurrent(10);
 						double leftCurrent = Drivetrain.getInstance().getPDP().getCurrent(11);
 						SmartDashboard.putNumber("Right Current", rightCurrent);
 						SmartDashboard.putNumber("Left Current", leftCurrent);
 						double maxCurrent = 24;
 						cubePosition = (rightCurrent-leftCurrent)/((rightCurrent+leftCurrent)/2.0);
// 						if(rightCurrent > 23 || leftCurrent > 23) {
//  							wantedState = systemStates.UnJamming;
//  						}
// 						else if(rightCurrent>6 || leftCurrent>6)
// 						{
// 							rightSideWheel.set(Constants.INTAKESPEED);
// 		 					leftSideWheel.set(-Constants.INTAKESPEED);
// 						}
// 						else
// 						{
// 							rightSideWheel.set(Constants.INTAKESPEED/1.5);
// 		 					leftSideWheel.set(-Constants.INTAKESPEED/1.5);
// 						}
 						if(rightCurrent > maxCurrent || leftCurrent > maxCurrent) {
							wantedState = systemStates.UnJamming;
						}
 						else
 						{
 							rightSideWheel.set(-((rightCurrent/maxCurrent)/2.0+.5));
 		 					leftSideWheel.set(((leftCurrent/maxCurrent)/2.0+.5));
 						}
	 					SmartDashboard.putNumber("cubePostion", cubePosition);
	 					lastState = systemStates.Intaking;
	 					wantedPosition = downPosition;
	 					closedLoopControl();
	 					if(wantedState == systemStates.UnJamming)
	 					{
	 						currState = systemStates.UnJamming;
	 					}
	 					else if(controller.Stow())
	 					{
	 						wantedPosition = 0.0;
	 						currState = systemStates.Neutral;
	 					}
 					}
 					else
 					{
 						currState = systemStates.Neutral;
 					}
 					lastState = systemStates.Intaking;
 					
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
 					if(unJamTimer.get()<.02)
 					{
 						rightSideWheel.set(0.0);//(Constants.INTAKESPEED*.5)*.5);
 	 					leftSideWheel.set(0.0);//(-Constants.INTAKESPEED*.5)*.5);
 					}
 					else if(unJamTimer.get() < .08)
 					{
 						rightSideWheel.set(-Constants.INTAKESPEED);
 	 					leftSideWheel.set(Constants.INTAKESPEED);
 					}
 					else
 					{
 						currState = systemStates.Intaking;
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
 				if(isCubeInLeft.get() || isCubeInRight.get()) {
 					LED.getInstance().setWantedState(LED.ledStates.LIGHTSHOW);
 				}
 				else {
 					LED.getInstance().setWantedState(LED.ledStates.CUBE_INTAKED);
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
