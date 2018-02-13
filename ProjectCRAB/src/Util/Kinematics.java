package Util;

import java.util.ArrayList;

import Subsystem.Swervepod;

public class Kinematics {
	private ArrayList<Swervepod> podList;
	private double[] position = {0.0, 0.0, 0.0}; //X (FT.), Y (FT.), Theta (-PI to PI) 
	
	public Kinematics(ArrayList<Swervepod> Pods) {
		podList = Pods;
	}
	private double getPosition() {
		return 0.0;
	}
}
