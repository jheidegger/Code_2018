
/**
 * @author 222002121
 *
 */
public class Main {
	public static void main(String arg[])
	{
		Trajectory1D t = new Trajectory1D(10000,5000);
		t.addWaypoint(new Waypoint(100000.0, 0.0, 0.0,0.0));
		t.addWaypoint(new Waypoint(66666.0,0.0,0.0,0.0));
		t.calculateTrajectory();
		//t.print();
//		for(double i = 0.0; i<5.0; i=i+.1)
//		{
//			System.out.println(t.getPosition(i));
//		}
		//t.print();
		
	}
}
