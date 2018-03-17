package Auton.Autos;

import Auton.PathFollower;
import Auton.Trajectory;
import Auton.Waypoint;
import Subsystem.Drivetrain;
import Subsystem.Loop;
import Subsystem.Drivetrain.driveCoords;
import Subsystem.Drivetrain.driveType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TrajectoryTest {
		
		private static Trajectory t;
		private static PathFollower p;
		private boolean firstTime = true;
		public TrajectoryTest() {
			t = new Trajectory();
			t.addWaypoint(new Waypoint(0.0, 0.0, 0.0));
			t.addWaypoint(new Waypoint(0.0, 2.0, 0.0));
			t.calculateTrajectory();
			p = new PathFollower(t);
		}

		public void run()
		{
			if(firstTime)
			{
				p.init();
				p.run();
				firstTime = false;
			}
			else
			{
				p.run();
			}
		}	

}

