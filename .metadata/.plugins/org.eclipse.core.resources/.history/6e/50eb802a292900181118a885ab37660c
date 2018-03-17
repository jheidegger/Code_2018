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

public class driveStraight {
		public static driveStraight main = new driveStraight();
		public static String gameData;
		private static double driveTime = 2.7;
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
						else
						{
							Drivetrain.getInstance().swerve(0.0, 0.0, driveCoords.FIELDCENTRIC, driveType.PERCENTPOWER, 0.0);
						}
						
					}

					@Override
					public void stop() {
						// TODO Auto-generated method stub
						
					}
			
				};
		public driveStraight() {
		
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

