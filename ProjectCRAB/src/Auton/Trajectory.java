package Auton;

import java.util.ArrayList;

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
	private ArrayList<Waypoint>  points;
	public Trajectory() {
		points = new ArrayList<Waypoint>();
	}
	public void addWaypoint(Waypoint w)
	{
		points.add(w);
	}
	public void calculateTrajectory()
	{
		
	}
	public void setWantedPos() {
		
	}
	
}


