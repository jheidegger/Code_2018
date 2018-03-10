package Auton;

import java.awt.geom.Arc2D;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.Timer;

public class Trajectory {
	private double kMaxVelocity = 1;
	private double kMaxAcceleration=.01; 
	
	private double currentX;
	private double currentY;
	private double currentAngle;
	private double currentTime;
	private double wantedX;
	private double wantedY;
	private double wantedAngle;
	private double currentVelocity;
	private double wantedVelocity;
	private double timeStep = .1;
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
		double currAngle = 0.0;
		int waypointIdx = 1;
		while(simTime < 1.0)
		{
			double stoppingDistance = Math.pow(currSpeed,2)/(2.0*kMaxAcceleration);
			double distanceToWaypoint = Math.sqrt(Math.pow(currX-points.get(waypointIdx).getX(), 2)+ Math.pow(currY - points.get(waypointIdx).getY(),2));
			if(Math.abs(currSpeed) < kMaxVelocity && distanceToWaypoint>stoppingDistance)
			{
				if((kMaxVelocity - Math.abs(currSpeed)) < kMaxAcceleration)
				{
					currSpeed = kMaxVelocity;
				}
				else
				{
					currSpeed = currSpeed + kMaxAcceleration;
				}
			}
			speed.add(currSpeed);
			currAngle = Math.atan2(points.get(waypointIdx).getY()-currY, points.get(waypointIdx).getX()-currX);
			angle.add(currAngle);
			currX =+ currSpeed * Math.cos(currAngle) * timeStep;
			currY =+ currSpeed * Math.sin(currAngle) * timeStep;
			simTime+= timeStep;
		}
	}
	public void print()
	{
	    for (double s:speed)
	    {
	        System.out.println(s);
	     }
	   }
	public void setWantedPos() {
		
	}
}


