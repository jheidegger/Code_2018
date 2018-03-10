package Auton.Autos;

import Auton.Auto;
import Subsystem.Drivetrain;
import Subsystem.Drivetrain.driveCoords;
import Subsystem.Drivetrain.driveType;
import Subsystem.Elevator.systemStates;
import Subsystem.Intake;
import Subsystem.Loop;
import Subsystem.Swervepod;
import edu.wpi.first.wpilibj.Timer;

public class middleSwitch extends Auto {
	public static middleSwitch main = new middleSwitch();
	public static String gameData;
	private static double driveTime = 2.0;
	private static double scoringTime = 1.0;
	private static Loop loop = new Loop()
			{
				@Override
				public void onStart() {
										
				}

				@Override
				public void onloop() {
					if(Timer.getFPGATimestamp()<driveTime)
					{
						if(gameData.substring(0,1).equals("R"))
						{
							Drivetrain.getInstance().swerve(.3, Math.PI/4.0, driveCoords.FIELDCENTRIC, driveType.PERCENTPOWER, 0.0);
						}
						else
						{
							Drivetrain.getInstance().swerve(.3, 3.0*Math.PI/4.0, driveCoords.FIELDCENTRIC, driveType.PERCENTPOWER, 0.0);
						}
					}
					else if(Timer.getFPGATimestamp()<driveTime+scoringTime)
					{
						Intake.getInstance().setWantedState(Subsystem.Intake.systemStates.Scoring);
						Drivetrain.getInstance().swerve(0.0, 0.0, driveCoords.FIELDCENTRIC, driveType.PERCENTPOWER, 0.0);
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
		gameData = super.gameData;
	}

}
