package Auton;

import java.awt.geom.Arc2D;
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
	private double timeStep = .02;
	private double simTime = 0.0;
	private ArrayList<Waypoint>  points;
	private ArrayList<Double> speed;
	private ArrayList<Double> angle;
	public Trajectory() {
		points = new ArrayList<Waypoint>();
		speed = new ArrayList<Double>();
		angle = new ArrayList<Double>();
	}
	public void addWaypoint(Waypoint w)
	{
		points.add(w);
	}
	public void calculateTrajectory()
	{
		double currSpeed = 0.0;
		double currX = points.get(0).getX();
		double currY = points.get(0).getY();
		int waypointIdx = 1;
		while(simTime < 15.0)
		{
			if(currSpeed < kMaxVelocity)
			{
				
			}
			angle.add(Math.atan2(points.get(waypointIdx).getY()-currY, points.get(waypointIdx).getX()-currX));
			simTime+= timeStep;
		}
	}
	public void setWantedPos() {
		
	}
	
}


