

public class Waypoint {
	private double X;
	private double Y;
	private double Angle;
	private double speed;
	public Waypoint(double X, double Y, double Angle)
	{
		this.X = X;
		this.Y = Y;
		this.Angle = Angle;
		this.speed = 0.0;
	}
	public Waypoint(double X, double Y, double Angle, double speed)
	{
		this.X = X;
		this.Y = Y;
		this.Angle = Angle;
		this.speed = speed;
	}
	public double getX() {
		return X;
	}
	public void setX(double x) {
		X = x;
	}
	public double getY() {
		return Y;
	}
	public void setY(double y) {
		Y = y;
	}
	public double getAngle() {
		return Angle;
	}
	public void setAngle(double angle) {
		Angle = angle;
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
}
