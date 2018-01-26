package org.usfirst.frc.team6713.robot;


public class PIDLoop {
	
	private double proportionalGain; 
	private double integralGain;
	private double derivativeGain;
	private double error, previous_error, integral, derivative, output = 0;
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
	
	public double returnOutput(double current, double setpoint) {
		error = setpoint - current;
		integral += (error*.02);
		derivative = (error - previous_error)/.02; 
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
