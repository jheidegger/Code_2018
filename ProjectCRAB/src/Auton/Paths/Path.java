package Auton.Paths;

import Auton.*;

public class Path {
	private Trajectory t;
	public Path()
	{
		
	}
	public void regesterTrajectory(Trajectory t)
	{
		this.t = t;
		this.t.calculateTrajectory();
	}
	public Trajectory get()
	{
		return t;
	}
}
