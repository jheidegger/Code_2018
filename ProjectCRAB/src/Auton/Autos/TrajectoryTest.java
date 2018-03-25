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

public class TrajectoryTest extends Auto {
		public static TrajectoryTest main = new TrajectoryTest();
		private static Trajectory t;
		private static PathFollower p;
		public TrajectoryTest() {
			super.registerLoop(new Loop()
			{

				@Override
				public void onStart() {
					t = new Trajectory();
					t.addWaypoint(new Waypoint(0.0, 0.0, 0.0));
					t.addWaypoint(new Waypoint(0.0, -5.0, 0.0));
					//t.addWaypoint(new Waypoint(4.0,-2.0,0.0));
					//t.addWaypoint(new Waypoint(0.0, -2.0, 0.0));
					t.calculateTrajectory();
					p = new PathFollower(t);
					p.init();
					p.run();
				}

				@Override
				public void onloop() {
					p.run();
				}

				@Override
				public void stop() {
					// TODO Auto-generated method stub
					
				}
		
			});
		}
			

}
