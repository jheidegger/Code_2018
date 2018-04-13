package Auton.Paths;

import Auton.Trajectory;
import Auton.Waypoint;

public class LeftSwitchToCenterCube extends Path {
	public static LeftSwitchToCenterCube main = new LeftSwitchToCenterCube();
	private Trajectory t = new Trajectory();
	private LeftSwitchToCenterCube() {
		t.addWaypoint(new Waypoint(0.0, 0.0, 0.0));
		t.addWaypoint(new Waypoint(0.0,2.5,Math.PI/4.0+.2,1.0));
		t.addWaypoint(new Waypoint(5.0,2.5,Math.PI/4.0+.2,1.0));
		t.addWaypoint(new Waypoint(4.0,3.0,0.0));
//		t.addWaypoint(new Waypoint(2.0,4.0,0.0));
		//t.addWaypoint(new Waypoint(4.0, 2.0, Math.PI/4.0));
		super.regesterTrajectory(t);
	}
}
