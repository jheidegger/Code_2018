package Auton;

import Util.PIDLoop;
import edu.wpi.first.wpilibj.Timer;

public class PathFollower {
	private double wantedX;
	private double wantedY; 
	private double wantedAngle;
	private double currentX;
	private double currentY;
	private double currentAngle;
	
	private double wantedStrafeVelocity;
	private double wantedForwardVelocity; 
	
	
	private double strafeError;
	private double forwardError;
	private double spinError;
	
	private PIDLoop strafeHandler;
	private PIDLoop forwardHandler;
	private PIDLoop spinHandler;
	
	public PathFollower() {
		strafeHandler = new PIDLoop(0.0,0.0,0.0);
		forwardHandler = new PIDLoop(0.0,0.0,0.0);
		spinHandler = new PIDLoop(0.0,0.0,0.0);
	}
	
	private void findTotalError() {
		strafeError = wantedX - currentX; 
		forwardError = wantedY - currentY;
		spinError = wantedAngle - currentAngle;
		
	}
	
	private void setWantedCoords() {
		/*
		wantedX = Trajectory.getWantedX();
		wantedY = Trajectory.getWantedY(); 
		wantedAngle = Trajectory.getWantedAngle();
		*/
	}
}