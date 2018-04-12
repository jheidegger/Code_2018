package Auton.Paths;

import Auton.Trajectory;
import Auton.Waypoint;

public class LeftSwitchToCenterCube extends Path {
	public static LeftSwitchToCenterCube main = new LeftSwitchToCenterCube();
	private Trajectory t = new Trajectory();
	private LeftSwitchToCenterCube() {
		t.addWaypoint(new Waypoint(0.0, 0.0, 0.0));
		t.addWaypoint(new Waypoint(0.0,3.0,Math.PI/4.0+.2));
		t.addWaypoint(new Waypoint(4.0,3.0,Math.PI/4.0+.2));
		t.addWaypoint(new Waypoint(4.7,2.7,Math.PI/4.0+.2));
		t.addWaypoint(new Waypoint(5.0,2.7,0.0));
		//t.addWaypoint(new Waypoint(4.0, 2.0, Math.PI/4.0));
		super.regesterTrajectory(t);
	}
}
