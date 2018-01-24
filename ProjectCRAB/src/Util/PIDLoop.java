package Util;

import Robot.*;

public class PIDLoop {
	
	private double proportionalGain; 
	private double integralGain;
	private double derivativeGain;
	private double error, previous_error, integral, derivative, output, integralMax = 0;
	private double max_speed = 0.8; 
	
	
	public PIDLoop(double pG, double iG, double dG){
		proportionalGain = pG; 
		integralGain = iG;
		derivativeGain = dG;
	}
	
	public PIDLoop(double pG, double iG, double dG, double mS){
		proportionalGain = pG; 
		integralGain = iG;
		derivativeGain = dG;
		max_speed = mS; 
	}

	public PIDLoop(double pG, double iG, double dG, double mS, double iMax){
		proportionalGain = pG; 
		integralGain = iG;
		derivativeGain = dG;
		max_speed = mS; 
		integralMax = iMax;
	}
	
	public double returnOutput(double current, double setpoint) {
		error = setpoint - current;
		if(integral < integralMax || integralMax == 0) {
			integral += (error*Constants.DELTATIME);
		}
		derivative = (error - previous_error)/Constants.DELTATIME; 
		previous_error = error;
		
		output = (proportionalGain*error) + (integralGain*integral) + (derivativeGain*derivative);
		
		if(output>max_speed) {
			output = max_speed; 
		}
		else if(output<-max_speed) {
			output = -max_speed;
		}
		
		return output;
	}
}
