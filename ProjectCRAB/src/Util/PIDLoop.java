package Util;

import Robot.*;
import edu.wpi.first.wpilibj.Timer;

public class PIDLoop {
	
	private double proportionalGain; 
	private double integralGain;
	private double derivativeGain;
	private double Kf;
	private double error, previous_error, integral, derivative, output, integralMax = 0;
	private double max_speed = 0.8; 
	private double currTime; 
	private double lastTime = 0.0;
	private double deltaTime;
	private Timer timer;
	
	
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
	public PIDLoop(double pG, double iG, double dG, double mS, double iMax, double f){
		proportionalGain = pG; 
		integralGain = iG;
		derivativeGain = dG;
		max_speed = mS; 
		integralMax = iMax;
		Kf = f;
	}
	public double returnOutput(double current, double setpoint) {
		currTime = timer.get();
		deltaTime = currTime-lastTime;
		lastTime = currTime;
		error = setpoint - current;
		if(integral < integralMax || integralMax == 0) {
			integral += (error*deltaTime);
		}
		derivative = (error - previous_error)/deltaTime; 
		previous_error = error;
		
		output = (proportionalGain*error) + (integralGain*integral) + (derivativeGain*derivative) + (Kf*setpoint);
		
		if(output>max_speed) {
			output = max_speed; 
		}
		else if(output<-max_speed) {
			output = -max_speed;
		}
		
		return output;
	}
}
