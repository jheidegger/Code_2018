package Util;

import org.usfirst.frc.team6713.robot.*;
import edu.wpi.first.wpilibj.Timer;

/**
 * Custom PID Loop Class
 * @author Harrison McCarty
 */

public class PIDLoop {
	private double kP;
	private double kI;
	private double kD;
	private double kF;
	private double error, previous_error, integral, derivative, output, integralMax = 0;
	private double max_speed = 1.0;
	private double currTime;
	private double lastTime = Timer.getFPGATimestamp();
	private double deltaTime;

	public PIDLoop(double pG, double iG, double dG){
		kP = pG;
		kI = iG;
		kD = dG;
	}

	public PIDLoop(double pG, double iG, double dG, double mS){
		kP = pG;
		kI = iG;
		kD = dG;
		max_speed = mS;
	}

	public PIDLoop(double pG, double iG, double dG, double mS, double f){
		kP = pG;
		kI = iG;
		kD = dG;
		max_speed = mS;
		kF = f;
	}

	public PIDLoop(double pG, double iG, double dG, double mS, double f, double iMax){
		kP = pG;
		kI = iG;
		kD = dG;
		max_speed = mS;
		integralMax = iMax;
	}

	public double returnOutput(double current, double setpoint) {
		error = setpoint - current;
		return returnOutput(error);
	}

	public double returnOutput(double error) {
		deltaTime = .02;
		if(integral < integralMax || integralMax == 0) {
			integral += (error*deltaTime);
		}
		else
		{
			if(integral>integralMax)
			{
				integral = integralMax;
			}
			else if(integral<-integralMax)
			{
				integral = -integralMax;
			}
		}
		derivative = (error - previous_error)/deltaTime;
		previous_error = error;

		output = (kP*error) + (kI*integral) + (kD*derivative);

		if(output>max_speed) {
			output = max_speed;
		}
		else if(output<-max_speed) {
			output = -max_speed;
		}

		return output;
	}

	//getters and setters for everything
	public double getkP() {return kP;}
	public void setkP(double kP) {this.kP = kP;}

	public double getkI() {return kI;}
	public void setkI(double kI) {this.kI = kI;}

	public double getkD() {return kD;}
	public void setkD(double kD) {this.kD = kD;}

	public double getkF() {return kF;}
	public void setkF(double kF) {this.kF = kF;}

	public double getIntegralMax() {return integralMax;}
	public void setIntegralMax(double integralMax) {this.integralMax = integralMax;}

	public double getMax_speed() {return max_speed;}
	public void setMax_speed(double max_speed) {this.max_speed = max_speed;}
}
