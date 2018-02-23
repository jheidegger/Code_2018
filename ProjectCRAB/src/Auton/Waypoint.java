package Auton;

public class Waypoint {
	private double X;
	private double Y;
	private double Angle;
	public Waypoint(double X, double Y, double Angle)
	{
		this.X = X;
		this.Y = Y;
		this.Angle = Angle;
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
	
}
