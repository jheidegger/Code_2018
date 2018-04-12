package Auton.Paths;

import Auton.Trajectory;
import Auton.Waypoint;

public class rightToRightScale extends Path {
	public static rightToRightScale main = new rightToRightScale();
	private Trajectory t = new Trajectory();
	private rightToRightScale() {
		t.addWaypoint(new Waypoint(0.0, 0.0, 0.0));
		t.addWaypoint(new Waypoint(0.0,-18.0,-Math.PI/4.0));
		super.regesterTrajectory(t);
	}
}
