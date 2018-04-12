package Auton.Paths;

import Auton.Trajectory;
import Auton.Waypoint;

public class leftToLeftScale extends Path {
	public static leftToLeftScale main = new leftToLeftScale();
	private Trajectory t = new Trajectory();
	private leftToLeftScale() {
		t.addWaypoint(new Waypoint(0.0, 0.0, 0.0));
		t.addWaypoint(new Waypoint(0.0,-18.0,Math.PI/4.0));
		super.regesterTrajectory(t);
	}
}
