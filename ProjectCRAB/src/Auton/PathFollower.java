package Auton;

import javax.swing.text.StyleContext.SmallAttributeSet;

import Subsystem.Drivetrain;
import Subsystem.Loop;
import Util.PIDLoop;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PathFollower {
	private double currX;
	private double currY;
	private PIDLoop spinHandler;
	private Trajectory t;
	private double startTime;
	private double lastTime;
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
					lastTime = 0.0;
				}

				@Override
				public void onloop() {
					double Time = Timer.getFPGATimestamp()-startTime;
					double dt = Time-lastTime;
					double speed = t.getSpeed(Time);
					double wheelAngle = t.getWheelAngle(Time);
					double strafeCommand = speed*Math.cos(wheelAngle);
					double forwardCommand = speed*Math.sin(wheelAngle);
					double spinCommand = spinHandler.returnOutput(Drivetrain.getInstance().getAngle(), 0 );
					currY += forwardCommand * dt;
					SmartDashboard.putNumber("autoSpinCommand", spinCommand);
					SmartDashboard.putNumber("autoSpeed", forwardCommand);
					SmartDashboard.putNumber("calculatedY", currY);
					Drivetrain.getInstance().swerve(forwardCommand, strafeCommand, spinCommand,Drivetrain.driveCoords.FIELDCENTRIC, 
							Drivetrain.driveType.VELOCITY);
					lastTime = Time;
					
				}

				@Override
				public void stop() {
					//  N/A
					
				}
		
			};
	

}