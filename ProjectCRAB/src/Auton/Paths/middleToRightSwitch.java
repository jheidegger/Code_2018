package Auton.Paths;

import Auton.Trajectory;
import Auton.Waypoint;

public class middleToRightSwitch extends Path {
	public static middleToRightSwitch main = new middleToRightSwitch();
	private Trajectory t = new Trajectory();
	private middleToRightSwitch() {
		t.addWaypoint(new Waypoint(0.0, 0.0, 0.0));
		t.addWaypoint(new Waypoint(4.85, -4.0, 0.0));
		t.addWaypoint(new Waypoint(4.85, -9.0, 0.0));
		super.regesterTrajectory(t);
	}
}
