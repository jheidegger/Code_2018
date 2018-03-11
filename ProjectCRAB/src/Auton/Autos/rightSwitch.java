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
	import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class rightSwitch {
		public static rightSwitch main = new rightSwitch();
		public static String gameData;
		private static double driveTime = 2.0;
		private static double driveTime2 = 1.0;
		private static double scoringTime = 2.0;
		private static boolean firstTime = true;
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
							Drivetrain.getInstance().swerve(-.3, 0.0,0.0, driveCoords.FIELDCENTRIC, driveType.PERCENTPOWER);
							
						}
						else if(gameData.substring(0,1).equals("R") && Timer.getFPGATimestamp()-startTime < driveTime +2.0)
						{
							Intake.getInstance().setWantedState(Subsystem.Intake.systemStates.Scoring);
							Drivetrain.getInstance().swerve(0.0, 0.0, driveCoords.FIELDCENTRIC, driveType.PERCENTPOWER, 0.0);
						}
						else
						{
							Drivetrain.getInstance().swerve(0.0, 0.0, driveCoords.FIELDCENTRIC, driveType.PERCENTPOWER, 0.0);
							Intake.getInstance().setWantedState(Subsystem.Intake.systemStates.Neutral);
						}
						
					}

					@Override
					public void stop() {
						// TODO Auto-generated method stub
						
					}
			
				};
		public rightSwitch() {
		
		}
		public static void setGameData(String game)
		{
			gameData = game;
		}
		
		public static void run()
		{
			if(firstTime)
			{
				loop.onStart();
				firstTime = false;
			}
			else
			{
				loop.onloop();
			}
		}	

}