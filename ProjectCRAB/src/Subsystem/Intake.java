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
 	private DigitalInput isCubeInLeft;
 	private DigitalInput isCubeInRight;
 	private DigitalInput isIntakeStowed;
 	
 	private systemStates currState;
 	private systemStates lastState;
 	private systemStates wantedState;
 	private boolean isOpenLoop;
 	private Encoder encoder;
 	private PIDLoop actuatorPID;
 	private double wantedPosition;
 	private double currPosition;
 	private double leftCurrent;
 	private double rightCurrent;
 	public final double neutralPosition = -500;
 	public final double downPosition = -17000;
 	// -1 full left - 1 full right
 	private double cubePosition = 0.0;
 	public enum systemStates{
 		Intaking,
 		Scoring,
 		UnJamming,
 		Homing,
 		Neutral
 	};
 	
 	public static Intake getInstance() {
 		return instance;
 	}
 	
 	private Intake()
 	{
 		actuatorPID = new PIDLoop(.00008,0,0.0,.55);
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
 	
 	public boolean isStowed()
 	{
 		return !isIntakeStowed.get();
 	}
 	
 	public void setPosition(double position) {wantedPosition = position;}//degreesToEncoder(position);}
 	public double getCurrPosition() {return currPosition;}
 	public boolean isCubeIn()
 	{
 		return (!isCubeInLeft.get() && !isCubeInRight.get());
 	}
 	/**
 	 * setting manual override for the intake
 	 * @param isOpenLoop a boolean to disable sensors
 	 */
 	public void setOpenLoopMode(boolean isOpenLoop)
 	{
 		this.isOpenLoop = isOpenLoop;
 	}
 	private double findCurrPosition() {return -encoder.getRaw();}
 	
 	private void checkState()
 	{
 		if(wantedState != currState)
		{
			currState = wantedState;
		}
 	}
 	
 	private void closedLoopControl()
 	{
		currPosition = findCurrPosition();
		if(wantedPosition < downPosition)
		{
			wantedPosition = downPosition;
		}
		if(isIntakeStowed.get()) 
		{
			stowingMotor.set(actuatorPID.returnOutput(currPosition, wantedPosition));	
		}
		else 
		{
			encoder.reset();
			if(actuatorPID.returnOutput(currPosition, wantedPosition)<0)
			{
				//allow the intake to deploy
				stowingMotor.set(actuatorPID.returnOutput(currPosition, wantedPosition));
			}
			else
			{
				//closed loop to hold the intake in place
				stowingMotor.set(actuatorPID.returnOutput(currPosition, 0.0));
			}
		}
 	}
 	private void openLoopControl()
 	{
 		stowingMotor.set(controller.getintakePositionJoystick() * .5);
 	}
 	private void intaking()
 	{
 		if(isCubeInLeft.get() || isCubeInRight.get())
			{
				rightCurrent = Drivetrain.getInstance().getPDP().getCurrent(10);
				leftCurrent = Drivetrain.getInstance().getPDP().getCurrent(11);
				double maxCurrent = 24;
				cubePosition = (rightCurrent-leftCurrent)/((rightCurrent+leftCurrent)/2.0);
				if(rightCurrent > maxCurrent || leftCurrent > maxCurrent) {
				wantedState = systemStates.UnJamming;
			}
				else
				{
					rightSideWheel.set(-((rightCurrent/maxCurrent)/2.0+.5));
					leftSideWheel.set(((leftCurrent/maxCurrent)/2.0+.5));
				}
				
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
 	}
 	
 	@Override
 	public void registerLoop() {
 		loopMan.addLoop(new Loop() {
			@Override
			public void onStart() {
				currState = systemStates.Homing;
				lastState = systemStates.Homing;
				wantedState = systemStates.Homing;
				isOpenLoop = false;
			}
 			@Override
 			public void onloop() {
 				if(isOpenLoop)
 				{
 					openLoopControl();
 					switch(currState)
 					{
 					case Neutral:
 						rightSideWheel.set(0.0);
 						leftSideWheel.set(0.0);
 						checkState();
 						lastState = systemStates.Neutral;
 						break;
					case Intaking:
						rightSideWheel.set(Constants.INTAKESPEED);
						leftSideWheel.set(-Constants.INTAKESPEED);
						checkState();
						lastState = systemStates.Intaking;
						break;
					case Scoring:
						rightSideWheel.set(Constants.INTAKESCORESPEED);
						leftSideWheel.set(-Constants.INTAKESCORESPEED);
						lastState = systemStates.Scoring;
						checkState();
						break;
					case UnJamming:
						if(lastState != systemStates.UnJamming)
	 					{
	 						unJamTimer.start();
	 						unJamTimer.reset();
	 					}
	 					if(unJamTimer.get()<.02)
	 					{
	 						rightSideWheel.set(0.0);
	 	 					leftSideWheel.set(0.0);
	 					}
	 					else if(unJamTimer.get() < .08)
	 					{
	 						rightSideWheel.set(Constants.INTAKESPEED);
	 	 					leftSideWheel.set(-Constants.INTAKESPEED);
	 					}
	 					else
	 					{
	 						currState = systemStates.Neutral;
	 					}
	 					lastState = systemStates.UnJamming;
	 					break;
					default:
						currState = systemStates.Neutral;
						break;
 					}
 				}
 				else
 				{
	 				switch(currState)
	 				{
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
	 				case Homing:
	 					if(isIntakeStowed.get())
	 					{
	 						stowingMotor.set(.25);
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
	 					if(!isOpenLoop && Elevator.getInstance().getState() != Elevator.systemStates.OPEN_LOOP)
	 					{
	 						if(Elevator.getInstance().getHeight() > 1000)
	 						{
	 							Elevator.getInstance().setWantedFloor(0.0);
	 							wantedPosition = neutralPosition;
	 							closedLoopControl();
	 						}
	 						else
	 						{
	 							intaking();
	 						}
	 					}
	 					else
	 					{
	 						intaking();
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
	 					if(unJamTimer.get()<.04 &&(isCubeInLeft.get() || isCubeInRight.get()))
	 					{
	 						rightSideWheel.set(Constants.INTAKESPEED*1.0);
	 	 					leftSideWheel.set(Constants.INTAKESPEED*1.0);
	 					}
	 					else if(unJamTimer.get() < .2 && !(isCubeInLeft.get() || isCubeInRight.get()) )
	 					{
	 						rightSideWheel.set(Constants.INTAKESPEED);
	 	 					leftSideWheel.set(-Constants.INTAKESPEED);
	 					}
	 					else
	 					{
	 						currState = systemStates.Intaking;
	 					}
	 					lastState = systemStates.UnJamming;
	 					break;
	 				default:
	 					currState = systemStates.Neutral;
						break;
	 				}
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
	@Override
	public void outputToSmartDashboard() {
		SmartDashboard.putBoolean("IR Left", isCubeInLeft.get());
		SmartDashboard.putBoolean("IR Right", isCubeInRight.get());
		SmartDashboard.putNumber("curr position", encoder.getRaw());
		SmartDashboard.putBoolean("Stowed",isIntakeStowed.get());
		SmartDashboard.putString("State", currState.toString());
		SmartDashboard.putBoolean("IsCubeINBoth", (!isCubeInLeft.get() && !isCubeInRight.get()));
		SmartDashboard.putNumber("wantedPosition", wantedPosition);
		SmartDashboard.putBoolean("CubeIn", isCubeInLeft.get());
		SmartDashboard.putBoolean("CubeIn", isCubeInRight.get());
		SmartDashboard.putNumber("Right Current", rightCurrent);
		SmartDashboard.putNumber("Left Current", leftCurrent);
		SmartDashboard.putNumber("cubePostion", cubePosition);
	}
 	@Override 
 	public void zeroAllSensors() {
 		
 	}
 	@Override 
 	public boolean checkSystem() {
 		return false;
 	}
 }
