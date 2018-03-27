package Auton;

import javax.swing.text.StyleContext.SmallAttributeSet;

import Subsystem.Drivetrain;
import Subsystem.Loop;
import Util.PIDLoop;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PathFollower {
	private double wantedX;
	private double wantedY; 
	private double wantedAngle;
	private double currentX;
	private double currentY;
	private double currentAngle;
	private double spinError;
	private PIDLoop spinHandler;
	private Trajectory t;
	private double startTime;
	public PathFollower(Trajectory t) {
		this.t = t;
		spinHandler = new PIDLoop(1.0,0.0,0.0);
	}
	public void init()
	{
		pathLoop.onStart();
	}
	public void run()
	{
		pathLoop.onloop();
	}
	Loop pathLoop = new Loop()
			{
				@Override
				public void onStart() {
					startTime = Timer.getFPGATimestamp();
				}

				@Override
				public void onloop() {
					double Time = Timer.getFPGATimestamp()-startTime;
					double speed = t.getSpeed(Time);
					//double speed = 0.0;
					double wheelAngle = t.getWheelAngle(Time);
					double strafeCommand = speed*Math.cos(wheelAngle);
					double forwardCommand = speed*Math.sin(wheelAngle);
					double spinCommand = spinHandler.returnOutput(Drivetrain.getInstance().getAngle(), 0 );
					SmartDashboard.putNumber("autoSpinCommand", spinCommand);
					//System.out.println("speed: "+ speed);
					Drivetrain.getInstance().swerve(forwardCommand, strafeCommand, spinCommand,Drivetrain.driveCoords.FIELDCENTRIC, 
							Drivetrain.driveType.VELOCITY);
				}

				@Override
				public void stop() {
					//  N/A
					
				}
		
			};
	

}