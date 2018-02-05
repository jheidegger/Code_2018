package Subsystem;
 
 import Robot.Constants;
 import edu.wpi.first.wpilibj.Solenoid;
 import edu.wpi.first.wpilibj.Victor;
 
 public class Intake extends Subsystem {
 
 	public static Intake instance = new Intake();
 	private Victor rightSideWheel;
 	private Victor leftSideWheel;
 
 	private systemStates currState;
 	enum systemStates{
 		Intaking,
 		Scoring,
 		UnJamming,
 		Stowed,
 		Handoff
 	};
 	
 	private Intake()
 	{
 		rightSideWheel = new Victor(Constants.INTAKERIGHTSIDE);
 		leftSideWheel = new Victor(Constants.INTAKELEFTSIDE);
 		
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
 		Loop_Manager.getInstance().addLoop(new Loop() {

			@Override
			public void onStart() {
				currState = systemStates.Stowed;
			}

 			@Override
 			public void onloop() {
 				switch(currState)
 				{
 			case Intaking:
 					rightSideWheel.set(Constants.INTAKESPEED);
 					leftSideWheel.set(Constants.INTAKESPEED);
 					break;
 				case Scoring:
 					rightSideWheel.set(Constants.INTAKESCORESPEED);
 					leftSideWheel.set(Constants.INTAKESCORESPEED);
 					break;
 				case UnJamming:
 					break;
 				case Stowed:
 					rightSideWheel.set(0.0);
 					leftSideWheel.set(0.0);
 					break;
 				case Handoff:
 					rightSideWheel.set(Constants.INTAKEHANDOFFSPEED);
 					leftSideWheel.set(Constants.INTAKEHANDOFFSPEED);
 					break;
 				}
 				
 			}
 
 			@Override
 			public void stop() {
 				// TODO Auto-generated method stub
 				
 			}
 			
 		});
 
 	}
 
 }
