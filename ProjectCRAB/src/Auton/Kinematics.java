package Auton;

import java.util.ArrayList;

import Subsystem.Drivetrain;
import Subsystem.Swervepod;

public class Kinematics {
	private ArrayList<Swervepod> podList;
	private double xSpeed;
	private double ySpeed;
	private double[] position = {0.0, 0.0, 0.0}; //X (FT.), Y (FT.), Theta (-PI to PI) 
	
	public Kinematics(ArrayList<Swervepod> Pods) {
		podList = Pods;
	}
	
	public void update()
	{
		double A[] = {podList.get(2).getWheelSpeed() * Math.cos(podList.get(2).getPosition()),
						podList.get(3).getWheelSpeed() * Math.cos(podList.get(3).getPosition())};
		
		double B[] = {podList.get(0).getWheelSpeed() * Math.sin(podList.get(0).getPosition()),
						podList.get(1).getWheelSpeed() * Math.sin(podList.get(1).getPosition())};
		
		double C[] = {podList.get(0).getWheelSpeed() * Math.sin(podList.get(0).getPosition()),
						podList.get(3).getWheelSpeed() * Math.sin(podList.get(3).getPosition())};
		
		double D[] = {podList.get(1).getWheelSpeed() * Math.cos(podList.get(1).getPosition()),
						podList.get(2).getWheelSpeed() * Math.cos(podList.get(2).getPosition())};
		double averageA = (A[0] + A[1])/2.0;
		double averageB = (B[0] + B[1])/2.0;
		double averageC = (C[0] + C[1])/2.0;
		double averageD = (D[0] + D[1])/2.0;
		xSpeed = averageA + (averageB - averageA)/2.0;
		ySpeed = averageC + (averageD-averageC)/2.0;
		
		// Integrate to position
		position[0] += xSpeed * .02;
		position[1] += ySpeed * .02;
		position[2] = Drivetrain.getInstance().getAngle();
		
	}
	
	private double getPosition() {
		return 0.0;
	}
}
