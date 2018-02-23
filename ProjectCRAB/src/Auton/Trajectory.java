package Auton;

import edu.wpi.first.wpilibj.Timer;

public class Trajectory {
	private double kMaxVelocity;
	private double kMaxAcceleration; 
	private double currentX;
	private double currentY;
	private double currentAngle;
	private double currentTime;
	private double wantedX;
	private double wantedY;
	private double wantedAngle;
	private double currentVelocity;
	private double wantedVelocity;
	
	public Trjectory() {
		
	}
	public void setWantedPos() {
		wantedX = path.getWantedX(Timer.getFPGATimestamp()+.5);
		wantedY = path.getWantedY(Timer.getFPGATimestamp()+.5);
		wantedAngle = path.getWantedAngle(Timer.getFPGATimestamp()+.5);
	}
	
}

}
