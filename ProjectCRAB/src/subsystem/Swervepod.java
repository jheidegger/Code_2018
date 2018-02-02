package subsystem;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Swervepod extends Subsystem {
	
	private TalonSRX driveMotor;
	private TalonSRX steerMotor;
	private int id;
	private boolean isReversed;
	private double currAngle;
	
	Swervepod(int id,TalonSRX driveMotor,TalonSRX steerMotor) {
		this.id = id;
		this.driveMotor = driveMotor;
		this.steerMotor = steerMotor;
		isReversed = false;
	}
	
	public void setPod(double Speed, double Angle)
	{
		//convert Angle from -pi to pi into 0 to 2pi
		if(Angle<0)
		{
			Angle = Angle + 2 * Math.PI;
		}
		
		double steerposition = Angle / (Math.PI*2) * 4096;
		
		steerMotor.set(ControlMode.Position, steerposition);
		driveMotor.set(ControlMode.Velocity, Speed);	
	}
	
	@Override
	public void zeroAllSensors() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean checkSystem() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void registerLoop() {
		// N/A
		
	}

}
