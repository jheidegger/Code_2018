package Auton.Paths;

import Auton.Trajectory;
import Auton.Waypoint;

public class CenterCubeToRightSwitch extends Path {
	public static CenterCubeToRightSwitch main = new CenterCubeToRightSwitch();
	private Trajectory t = new Trajectory();
	private CenterCubeToRightSwitch() {
		t.addWaypoint(new Waypoint(0.0, 0.0, 0.0));
		t.addWaypoint(new Waypoint(0.0, 1.0, 0.0));
		t.addWaypoint(new Waypoint(4.0,1.0,0.0));
		t.addWaypoint(new Waypoint(4.0,-1.0, 0.0));
		super.regesterTrajectory(t);
	}
}
