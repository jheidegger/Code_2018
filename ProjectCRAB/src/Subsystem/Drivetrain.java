package Subsystem;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.ArrayList;
import org.usfirst.frc.team6713.robot.Constants;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import Util.PIDLoop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/** 
 * Handles crab drive states and manages individual swervepods, see {@link Swervepod}
 * @author Harrison McCarty, Jonathan Heidegger, and Matt Halsmer
 */

public class Drivetrain extends Subsystem {

	private static Drivetrain instance = new Drivetrain();

	private Loop_Manager loopMan = Loop_Manager.getInstance();
	
	private Controller controller = Controller.getInstance(); 
	private PixyCam cam = PixyCam.getInstance();
	
	private AHRS gyro;
	
	private ArrayList<Swervepod> Pods;
	private ArrayList<Double> recordedValuesY = new ArrayList<Double>();
	private ArrayList<Double> recordedValuesX = new ArrayList<Double>();
	private ArrayList<Double> recordedValuesOmega = new ArrayList<Double>();
	private ArrayList<Double> recordedValuesGyro = new ArrayList<Double>();
	private Swervepod upperRight;
	private Swervepod upperLeft;
	private Swervepod lowerLeft;
	private Swervepod lowerRight;
	double lastAngle = 0;
	
	private driveCoords Coords;
	private driveType commandType;
	
	private double currTime; 
	private double lastTime = Timer.getFPGATimestamp();
	
	public TalonSRX[] driveTalon = {new TalonSRX(1), new TalonSRX(2), new TalonSRX(3), new TalonSRX(4)}; 
	public TalonSRX[] gearTalon = {new TalonSRX(11), new TalonSRX(22), new TalonSRX(33), new TalonSRX(44)};
	
	private double kLength;
	private double kWidth;
	private double kRadius;
	
	private int idCount = 0; 
	
	private PIDLoop pidLoop;
	private PIDLoop pidForward;
	private PIDLoop autoHeadingControl;
	private PIDLoop antiTip; 
	
	private double kMaxSpeed;
	private double kMaxRotation;
	
	private double rel_max_speed = 0;
	private double angle;
	private double forwardCommand;
	private double strafeCommand;
	private double spinCommand;
	
	public enum systemStates{
		NEUTRAL,
		HOMING,
		DRIVE,
		PARK,
		VISION, 
		AUTON
	}
	
	public enum driveCoords{
		ROBOTCENTRIC,
		FIELDCENTRIC
	}
	
	public enum driveType{
		PERCENTPOWER,
		VELOCITY
	}
	
	private systemStates currentState;
	private systemStates requestedState;
	private systemStates lastState;
	
	private Drivetrain(){
		//instantiate the pods
		upperRight = new Swervepod(0,driveTalon[0], gearTalon[0]);
		upperLeft = new Swervepod(1,driveTalon[1], gearTalon[1]);
		lowerLeft = new Swervepod(2,driveTalon[2], gearTalon[2]);
		lowerRight = new Swervepod(3,driveTalon[3], gearTalon[3]);
		
		//Instantiate array list
		Pods = new ArrayList<Swervepod>();
		
		pidLoop = new PIDLoop(0.0007,0,0);
		pidForward = new PIDLoop(0.001,0,0);
		autoHeadingControl = new PIDLoop(.6,.1,.01);
		antiTip = new PIDLoop(.05,0,0);
				
		//Add instantiated Pods to the array list
		Pods.add(upperRight);
		Pods.add(upperLeft);
		Pods.add(lowerLeft);
		Pods.add(lowerRight);
		
		//setting constants
		kLength = Constants.DRIVETRAINLENGTH;
		kWidth = Constants.DRIVETRAINWIDTH;
		kRadius = Math.sqrt(Math.pow(kLength,2)+Math.pow(kWidth,2));
		kMaxSpeed = Constants.DRIVETRAINMAXWHEELSPEED;
		kMaxRotation = Constants.DRIVETRAINMAXROTATIONSPEED;
		
		//instantiate the gyro
		gyro = new AHRS(SPI.Port.kMXP);
		updateAngle();

		//initialize the commands
		forwardCommand = 0.000000000000000000000001;
		strafeCommand = 0.0;
		spinCommand = 0.0;
	}
	
	public static Drivetrain getInstance(){
		return instance;
	}

	private void updateAngle(){
		//-pi to pi 0 = straight ahead
		angle = ((((gyro.getAngle()+90)* Math.PI/180.0)) % (2*Math.PI));
	
		SmartDashboard.putNumber("Angle", angle);
		SmartDashboard.putNumber("rawGyro", gyro.getAngle());
	}
	
	private void crabDrive() {
		//Create arrays with the speed and angle of each pod
		double[] podDrive = new double[4];
		double[] podGear = new double[4];
		
		//Calculating components
		double a = strafeCommand + spinCommand * kLength/2; 
		double b = strafeCommand - spinCommand * kLength/2; 
		double c = forwardCommand - spinCommand * kWidth/2; 
		double d = forwardCommand + spinCommand * kWidth/2; 
		
		//Calculating the speed and angle of each pod
		podDrive[0] = Math.sqrt(Math.pow(b, 2)+ Math.pow(c, 2));
		podGear[0] = Math.atan2(b,c);
		
		podDrive[1] = Math.sqrt(Math.pow(b, 2)+ Math.pow(d, 2));
		podGear[1] = Math.atan2(b,d);
		
		podDrive[2] = Math.sqrt(Math.pow(a, 2)+ Math.pow(d, 2));
		podGear[2] = Math.atan2(a,d);
		
		podDrive[3] = Math.sqrt(Math.pow(a, 2)+ Math.pow(c, 2));
		podGear[3] = Math.atan2(a,c);
		
		//Finding the highest commanded velocity between the pods
		rel_max_speed = Math.max(Math.max(podDrive[0],podDrive[1]),Math.max(podDrive[2], podDrive[3]));
		
		//Reducing pods by the relative max speed
		if(rel_max_speed > 1) {
			for(int idx = 0; idx < Pods.size(); idx++) {
				podDrive[idx] /= rel_max_speed;
			}
		}
		
		
		// Sending each pod their respective commands
		for(int idx = 0; idx < Pods.size(); idx++) {
			//sending power from 0 to 13.5 ft/s and position -pi to pi
			Pods.get(idx).setPod(podDrive[idx],podGear[idx]); 
		}
	}
	
	public void recordAuton (){
		//Captures the commands of a driver and puts them into an array for Auton usage
		recordedValuesY.add(-controller.getForward());
		recordedValuesX.add(-controller.getStrafe());
		recordedValuesOmega.add(controller.getRotation());
		recordedValuesGyro.add(getAngle());
	}
	public void clearAuton()
	{
		recordedValuesX.clear();
		recordedValuesY.clear();
		recordedValuesOmega.clear();
		recordedValuesGyro.clear();
	}
	
	private void resetGyro() {
		gyro.reset();
	}
	public double getAngle()
	{
		return angle;
	}
	
	/**
	 * Determines the settings of swerve drive, and the current commands
	 * @param forwardCommand magnitude on the Y-Axis 
	 * @param strafeCommand magnitude on the X-Axis 
	 * @param spinCommand magnitude on the Omega Axis 
	 * @param Coords determines whether swerve is in Robot-Centric or Field-Centric
	 * @param commandType determines whether commanding values are in percent power (-1 to 1) or their intended velocity values (in ft/s)
	 */
	public void swerve(double forwardCommand, double strafeCommand, double spinCommand) {
		if(Coords == driveCoords.ROBOTCENTRIC) {
			this.forwardCommand = forwardCommand;
			this.strafeCommand = strafeCommand;
			this.spinCommand = spinCommand/6.0;
			if(commandType == driveType.PERCENTPOWER) {
				this.forwardCommand *= kMaxSpeed;
				this.strafeCommand *= kMaxSpeed;
				this.spinCommand *= kMaxRotation;
			}
		}
		else {
			final double temp = forwardCommand * Math.sin(angle) + strafeCommand * Math.cos(angle);
		    this.strafeCommand = (-forwardCommand * Math.cos(angle) + strafeCommand * Math.sin(angle));
		    this.forwardCommand = temp;
		    this.spinCommand = spinCommand/6.0;
		    if(spinCommand == 0) {
		    //this.spinCommand = this.spinCommand +autoHeadingControl.returnOutput(angle, lastAngle);
		    }
		    lastAngle = angle;
		    if(commandType == driveType.PERCENTPOWER) {
				this.forwardCommand *= kMaxSpeed;
				this.strafeCommand *= kMaxSpeed;
				this.spinCommand *= kMaxRotation;
			}
		}
	}
	public void swerve(double forwardCommand, double strafeCommand, double spinCommand, driveCoords Coords, driveType commandType){
		this.Coords = Coords;
		this.commandType = commandType;
		
		if(Coords == driveCoords.ROBOTCENTRIC) {
			this.forwardCommand = forwardCommand;
			this.strafeCommand = strafeCommand;
			this.spinCommand = spinCommand/6.0;
			if(commandType == driveType.PERCENTPOWER) {
				this.forwardCommand *= kMaxSpeed;
				this.strafeCommand *= kMaxSpeed;
				this.spinCommand *= kMaxRotation;
			}
		}
		else {

			final double temp = forwardCommand * Math.sin(angle) + strafeCommand * Math.cos(angle);
		    this.strafeCommand = (-forwardCommand * Math.cos(angle) + strafeCommand * Math.sin(angle));
		    this.forwardCommand = temp;
		    this.spinCommand = spinCommand/6.0;
		    if(spinCommand == 0) {
			   // this.spinCommand = this.spinCommand + autoHeadingControl.returnOutput(angle, lastAngle);
			}
			    lastAngle = angle;
		    if(commandType == driveType.PERCENTPOWER) {
				this.forwardCommand *= kMaxSpeed;
				this.strafeCommand *= kMaxSpeed;
				this.spinCommand *= kMaxRotation;
			}
		}
	}

	
	@Override
	public void zeroAllSensors() {
		for(int idx = 0; idx < 4; idx++)
		{
			Pods.get(idx).zeroAllSensors();
		}
	}

	@Override
	public boolean checkSystem() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setSystemState(systemStates wanted) {
		requestedState = wanted;
	}
	
	public void checkState() {
		if(requestedState!=currentState)
		{
			currentState = requestedState;
		}
	}
	
	@Override
	public void registerLoop()
	{
		loopMan.addLoop(new Loop() {
		@Override
		public void onStart() {
			currentState = systemStates.NEUTRAL;
			requestedState = systemStates.NEUTRAL;		
		}
		@Override
		public void onloop() {
			if(controller.getGyroResetButton()) {
				resetGyro();
			}
			updateAngle();
			switch(currentState) {
				case NEUTRAL:
					checkState();
					lastState = systemStates.NEUTRAL;
				case DRIVE:
					crabDrive();
					recordAuton();
					lastState = systemStates.DRIVE;
					checkState();
					break;
				case AUTON:
					
					if(lastState != systemStates.AUTON) {
						idCount = 0;
						resetGyro();
					}
					double error;
					if(Math.abs(recordedValuesGyro.get(idCount)- angle) > Math.PI)
					{
						error = ((recordedValuesGyro.get(idCount) - angle) + Math.PI*2.0) % (2*Math.PI);
					}
					else
					{
						error = recordedValuesGyro.get(idCount) - angle;
					}
					SmartDashboard.putNumber("error", error);
					SmartDashboard.putNumber("recordedValue for Gyro", recordedValuesGyro.get(idCount));
					SmartDashboard.putNumber("actual", angle);
					forwardCommand = recordedValuesY.get(idCount);
					strafeCommand = recordedValuesX.get(idCount);
					spinCommand = autoHeadingControl.returnOutput(error);
					SmartDashboard.putNumber("spinCommand", spinCommand);
					System.out.println(forwardCommand);
					if(idCount < recordedValuesX.size()-1)
					{
						idCount++;
					}
					swerve(-forwardCommand, -strafeCommand, -spinCommand, Drivetrain.driveCoords.FIELDCENTRIC, Drivetrain.driveType.VELOCITY);
//					swerve(0.0, 0.0, -spinCommand, Drivetrain.driveCoords.FIELDCENTRIC, Drivetrain.driveType.VELOCITY);
					crabDrive();
					lastState = systemStates.AUTON;
					checkState();
					break;
				case VISION:
					spinCommand = -pidLoop.returnOutput(cam.getAvgX(), 160);
					forwardCommand = pidForward.returnOutput(cam.getAvgArea(), 5000);
					if(forwardCommand < -.2) {
						forwardCommand = 0; 
					}
					crabDrive();
					lastState = systemStates.VISION;
					checkState();
					break;
				default:
					break;			
				}
		}	
		@Override
		public void stop() {				
		}
	});
	}
}
	

