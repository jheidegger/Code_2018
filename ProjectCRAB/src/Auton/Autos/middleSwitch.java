package Auton.Autos;

import Subsystem.Drivetrain;
import Subsystem.Drivetrain.driveCoords;
import Subsystem.Drivetrain.driveType;
import Subsystem.Elevator.systemStates;
import Subsystem.Intake;
import Subsystem.Loop;
import Subsystem.Swervepod;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class middleSwitch extends Auto {
	public static middleSwitch main = new middleSwitch();
	private static double driveTime = 1.5;
	private static double driveTime2 = 1.0;
	private static double scoringTime = 2.0;
	private static double startTime;
	private static Loop loop = new Loop()
			{
				@Override
				public void onStart() {
					startTime = Timer.getFPGATimestamp();
				}

				@Override
				public void onloop() {
					SmartDashboard.putNumber("timer", Timer.getFPGATimestamp());
					if(Timer.getFPGATimestamp()-startTime<driveTime)
					{
						SmartDashboard.putBoolean("in drive", true);
						if(gameData.substring(0,1).equals("R"))
						{
							Drivetrain.getInstance().swerve(-.3, 0.27,((Drivetrain.getInstance().getAngle()-Math.PI/2)*.1), driveCoords.FIELDCENTRIC, driveType.PERCENTPOWER);
						}
						else
						{
							Drivetrain.getInstance().swerve(-.3, -0.3,((Drivetrain.getInstance().getAngle()-Math.PI/2)*.1), driveCoords.FIELDCENTRIC, driveType.PERCENTPOWER);
						}
					}
					else if(Timer.getFPGATimestamp()-startTime<driveTime+driveTime2)
					{
						Drivetrain.getInstance().swerve(-.3, 0.0,0.0, driveCoords.FIELDCENTRIC, driveType.PERCENTPOWER);
					}
					else if(Timer.getFPGATimestamp()-startTime<driveTime+scoringTime+driveTime2)
					{
						SmartDashboard.putBoolean("in drive", false);
						Intake.getInstance().setWantedState(Subsystem.Intake.systemStates.Scoring);
						Drivetrain.getInstance().swerve(0.0, 0.0, 0.0, driveCoords.FIELDCENTRIC, driveType.PERCENTPOWER);
					}
					else
					{
						Intake.getInstance().setWantedState(Subsystem.Intake.systemStates.Neutral);
					}
					
				}

				@Override
				public void stop() {
					// TODO Auto-generated method stub
					
				}
		
			};
	public middleSwitch() {
		super(loop);
	}
	

}
