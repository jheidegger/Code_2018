package Auton.Paths;

import Auton.Trajectory;
import Auton.Waypoint;

public class LeftSwitchToCenterCube extends Path {
	public static LeftSwitchToCenterCube main = new LeftSwitchToCenterCube();
	private Trajectory t = new Trajectory();
	private LeftSwitchToCenterCube() {
		t.addWaypoint(new Waypoint(0.0, 0.0, 0.0));
		t.addWaypoint(new Waypoint(0.0,3.0,0.0));
		t.addWaypoint(new Waypoint(4.0,3.0, 0.0));
		t.addWaypoint(new Waypoint(4.0, 1.0, 0.0));
		super.regesterTrajectory(t);
	}
}
