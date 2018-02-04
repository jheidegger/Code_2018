/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

/*
 * ----------------------------------------------------------------------------
 * Hey there my name is Jonathan Heidegger and I was one of the main authors
 * of this code. It was the code that Team 3176 used on Chimera in the
 * 2017 FRC game. It is all in one class and is a little under 2000 lines long.
 * This is not an ideal way to code. This is a road map to the interesting parts of swerve
 *
 * Email questions to jth3141@gmail.com or team3176@brownsburg.k12.in.us
 * <ROAD - MAP>
 * 	4 victors for steering
 * 	4 victors for wheel power
 * 	4 quadrature encoders on a PG71 motor
 * 	4 Hall effect sensors for homing
 *
 *	Swerve main function - line 1247
 *	Joystick and swerve modes - line 1218
 *	Pod homing function - line 910
 *	Wheel position PID - line 1481
 *	Wheel power current ramping- line 1446
 *	homing routine - line 727
 *	Teleop periodic - line 683
 *
 *
 *
 *
 * </Road - MAP>
 */

#include "WPILib.h"
#include "math.h"
#include "tgmath.h"
#include <thread>
#include <CameraServer.h>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/core/types.hpp>
#include <I2C.h>
#define HOLDOPENPOWER  -0.02
#define OPENMOTORPOWER -0.35
#define CLOSEMOTORPOWER  0.25
#define SLOWOPENINGPOWER  -0.1
#define HOLDCLOSEDPOWER 0.1
#define POWERLIMIT 6.5
#define PIXY_I2C_DEFAULT_ADDR           0x2

// Communication/misc parameters
#define PIXY_INITIAL_ARRAYSIZE      30
#define PIXY_MAXIMUM_ARRAYSIZE      130
#define PIXY_START_WORD             0xaa55 //for regular color recognition
#define PIXY_START_WORD_CC          0xaa56 //for color code - angle rotation recognition
#define PIXY_START_WORDX            0x55aa //regular color another way around
#define PIXY_MAX_SIGNATURE          7
#define PIXY_DEFAULT_ARGVAL         0xffff

// Pixy x-y position values
#define PIXY_MIN_X                  0L	//x: 0~319 pixels, y:0~199 pixels. (0,0) starts at bottom left
#define PIXY_MAX_X                  319L
#define PIXY_MIN_Y                  0L
#define PIXY_MAX_Y                  199L

enum BlockType
{
	NORMAL_BLOCK, //normal color recognition
	CC_BLOCK	  //color-code(change in angle) recognition
};

struct Block
{
  // print block structure!
  void print()
  {
    int i, j;
    char buf[128], sig[6], d;
	bool flag;
    if (signature>PIXY_MAX_SIGNATURE) // color code! (CC)
	{
      // convert signature number to an octal string
      for (i=12, j=0, flag=false; i>=0; i-=3) //assigns value to signature, x, y, width, height, and anlge
      {
        d = (signature>>i)&0x07;
        if (d>0 && !flag)
          flag = true;
        if (flag)
          sig[j++] = d + '0';
      }
      sig[j] = '\0';
      printf("CC block! sig: %s (%d decimal) x: %d y: %d width: %d height: %d angle %d\n", sig, signature, x, y, width, height, angle);
    }
	else // regular block.  Note, angle is always zero, so no need to print
      printf("sig: %d x: %d y: %d width: %d height: %d\n", signature, x, y, width, height); //prints out data to console instead of smartDashboard -> check on the side of the driver station, check +print and click view console
    //Serial.print(buf);
  }
  uint16_t signature; //Identification number for your object - you could set it in the pixymon
  uint16_t x; //0 - 320
  uint16_t y; //0 - 200
  uint16_t width;
  uint16_t height;
  uint16_t angle;
  //uint16_t area;
};

class Robot: public IterativeRobot
{

	//camera stuff
	I2C* i2c;
	BlockType blockType;// it is the enum on the top
	bool  skipStart;	//skips to check 0xaa55, which is byte that tells pixy it is start of new frame
	uint16_t blockCount; //How many signatured objects are there?
	uint16_t blockArraySize; //not used in the code
	Block blocks[100]; //array that stores blockCount array
	//One time flag used for homing
	bool homeReady[4] = {false,false,false,false};
	bool getframe = false;
	int lostTargets = 0;
	Joystick vStick;
	Joystick rStick;
	Joystick BM;
	AnalogGyro gyro;
	//These are the steer encoders
	Encoder enc1;
	Encoder enc2;
	Encoder enc3;
	Encoder enc4;
	Encoder shooterEnc;
	//Gyro used for field centric driving
	ADXRS450_Gyro Gyro1;
	//power victors for the wheels
	Victor w1;
	Victor w2;
	Victor w3;
	Victor w4;
	//hall effect sensors for homing
	DigitalInput hall1;
	DigitalInput hall2;
	DigitalInput hall3;
	DigitalInput hall4;
	//steering victors
	Victor aw1;
	Victor aw2;
	Victor aw3;
	Victor aw4;

	Victor winch;
	Victor shooter;
	Victor kicker;
	Victor gear;
	Victor gear2;
	//PID loop for the wheel position
	float throttle=0.0;
	float pOut=0.0;
	float dOut=0.0;
	float error1=0.0;
	float error2=0.0;
	float error3=0.0;
	float error4=0.0;
	float ShotlastError =0.0;
	float Shoterror = 0.0;
	float lastError1 =0.0;
	float lastError2=0.0;
	float lastError3=0.0;
	float lastError4=0.0;
	float Accum1 =0.0;
	float Accum2 =0.0;
	float Accum3 =0.0;
	float Accum4 =0.0;
	//homing global variables
	float offset[4]= {0.0,0.0,0.0,0.0};
	//variable used to reverse the wheels output
	float reverse[4]={1.0,1.0,1.0,1.0};

	float errorlong=0.0;
	//used to stop the wheels from reverting back to the 0 position when joystick was 0
	float lastThetalist[4] = {0.0,0.0,0.0,0.0};
	//current ramping to prevent brownouts
	float CurrentWheelPower[4] = {0.0,0.0,0.0,0.0};
	int joystickmode=0;
	float pi = 3.1415926535989793238464332847;
	//converting gyro from 0-360 to 0 - 2 pi
	float gyroAngle = fmod(((Gyro1.GetAngle()*pi)/180),2*pi);
	bool homestarted = false;
	int firstgo=0;
	float U=0;
	float setpoint;

	float TestAvgX;
	float AreaRatio = 1.0;
	bool twocontours = false;
	float RPMSize = 10.0;
	float RPMTable[10];
	int bufferIndex=0;
	int Cam =1;
	float speedfeed=0.0;
	int state = 0;
	//bool gear = false;
	Timer gearTimer;
	Timer Autotimer;
	Timer tankTimer;
	int tankState =0;
	int tankWheelState[4] = {0,0,0,0};
	float tankTimerRef[4]={0.0,0.0,0.0,0.0};
	float tankPower[4]={0.0,0.0,0.0,0.0};
	int TPState = 0;
	float autoGyroOffset =0;
	float TotalPower;
	float PowerScaler;
	bool softStart = true;
	SendableChooser<std::string> chooser;
	const std::string autoNameDefault = "Default";
	const std::string autoName1 = "Drive Forward";
	const std::string autoName2 = "Deliver Gear Center";
	const std::string autoName3 = "shoot turn and drive RED";
	const std::string autoName4 = "shoot turn and drive BLUE";
	const std::string autoName5 = "shoot and drive RED";
	const std::string autoName6 = "shoot and drive BLUE";
	const std::string autoName7 = "shoot and drive BLUE SHORT";
	const std::string autoName8 = "shoot and drive RED SHORT";

public:
	Robot():
		//myRobot(18,19),// uncommented today
		vStick(1),
		rStick(0),
		BM(2),
		gyro(0),
		enc1(0,1,false, Encoder::EncodingType::k4X),
		enc2(2,3,false, Encoder::EncodingType::k4X),
		enc3(4,5,false, Encoder::EncodingType::k4X),
		enc4(6,7,false, Encoder::EncodingType::k4X),
		shooterEnc(8,9,false, Encoder::EncodingType::k4X),
		w1(0),
		w2(2),
		w3(4),
		w4(6),
		hall1(21),
		hall2(22),
		hall3(23),
		hall4(24),
		aw1(1),
		aw2(3),
		aw3(5),
		aw4(7),
		winch(8),
		shooter(11),
		kicker(9),
		gear(12),
		gear2(13)
	{
		i2c = new I2C(I2C::Port::kOnboard, 0x54);
		chooser.AddDefault(autoNameDefault,autoNameDefault);
		chooser.AddObject(autoName1,autoName1);
		chooser.AddObject(autoName2,autoName2);
		chooser.AddObject(autoName3,autoName3);
		chooser.AddObject(autoName4,autoName4);
		chooser.AddObject(autoName5,autoName6);
		chooser.AddObject(autoName6,autoName6);
		chooser.AddObject(autoName7,autoName7);
		chooser.AddObject(autoName8,autoName8);
		SmartDashboard::PutData("chooser",&chooser);

	}




	bool homeDone = false;
	float timerRef;
	int autonState = 0;
	void AutonomousInit()
	{
		Gyro1.Reset();
		Autotimer.Start();
		Autotimer.Reset();
		//autonState=2;
		homeReady[0]=false;
		homeReady[1]=false;
		homeReady[2]=false;
		homeReady[3]=false;
		homeDone = false;
		autonState = 0;
		SmartDashboard::PutNumber("timer",Autotimer.Get());
		//autoLoopCounter = 0;// added line today

		//launchVisionThread();

	}
	void autonMode1()
	{
		autoGyroOffset =0;
		if(homeReady[0]!=true or homeReady[1]!=true or homeReady[2]!=true or homeReady[3]!=true)
		{
			homeOnePod(enc1.Get(),hall1.Get(),0);
			homeOnePod(enc2.Get(),hall2.Get(),1);
			homeOnePod(enc3.Get(),hall3.Get(),2);
			homeOnePod(enc4.Get(),hall4.Get(),3);
			Autotimer.Reset();
		}
		else if(Autotimer.Get()<3.5)
		{
			float error=Gyro1.GetAngle();
			float Out = error*.03;
			swerve(0.4,0.035,Out);
		}
		else
		{
			swerve(0.0,0.0,0.0);
		}
	}
	// State    Meaning
	// 0		Homing
	// 1        Pause To Settle
	// 2		Drive Forward (6 seconds)
	// 3		Stop
	void autonMode2()
	{
		gearDoorPeriodic();
		autoGyroOffset =0;
		if(homeReady[0]!=true or homeReady[1]!=true or homeReady[2]!=true or homeReady[3]!=true)
		{
			homeOnePod(enc1.Get(),hall1.Get(),0);
			homeOnePod(enc2.Get(),hall2.Get(),1);
			homeOnePod(enc3.Get(),hall3.Get(),2);
			homeOnePod(enc4.Get(),hall4.Get(),3);
			Autotimer.Reset();
		}
		else if(Autotimer.Get()<4)
		{
			float error=Gyro1.GetAngle();
			float Out = error*.03;
			swerve(0.3,-0.04,Out);
		}
		else if(Autotimer.Get()<4+.2)
		{
			swerve(-.1,0.0,0.0);
			openDoors();

		}
		else if(Autotimer.Get()<4+.2+1.5)
		{
			swerve(-0.35,0.02,0.0);
			openDoors();
		}
		else
		{
			swerve(0.0,0.0,0.0);
			closeDoor();
		}
	}
	void AutonMode3(float RedSide, bool Turn)//redside long
	{
		double rpmSet = 2070;

		autoGyroOffset =90 * RedSide;
		if(homeReady[0]!=true or homeReady[1]!=true or homeReady[2]!=true or homeReady[3]!=true)
		{
			homeOnePod(enc1.Get(),hall1.Get(),0);
			homeOnePod(enc2.Get(),hall2.Get(),1);
			homeOnePod(enc3.Get(),hall3.Get(),2);
			homeOnePod(enc4.Get(),hall4.Get(),3);
			AutonShooter(rpmSet);
			Autotimer.Reset();
		}
		else if(Autotimer.Get()<1.5)
		{
			swerve(0.0,-.35,0.0);
			AutonShooter(rpmSet);

		}
		else if(Autotimer.Get()<2.5)
		{
			float error=Gyro1.GetAngle()-22;
			float Out = error*.02;
			swerve(0.0,0.0,Out);
			AutonShooter(rpmSet);
			SmartDashboard::PutNumber("gyro auto", Gyro1.GetAngle());
		}
		else if(Autotimer.Get()<3+8)
		{
			AutonShooter(rpmSet);
			kicker.Set(1);
			swerve(0.0,0.0,0.0);
		}
		else if(Autotimer.Get()<3+8+1.3)
		{
			AutonShooter(0.0);
			kicker.Set(0.0);
			float error=Gyro1.GetAngle();
			float Out = error*.03;
			swerve(0.0,(-0.4*RedSide),Out);

		}
		else if(Autotimer.Get()<3+8.0+1.3+3.0 and Turn)
		{
			float error=Gyro1.GetAngle()+125.0*RedSide;
			float Out = error*.015;
			swerve(0.0,0.0,Out);

		}
		else
		{
			swerve(0.0,0.0,0.0);
		}
	}
	void AutonMode8(float RedSide, bool Turn) //Shoot and Drive, RED Short
		{
			double rpmSet = 2200;

			autoGyroOffset =90 * RedSide;
			if(homeReady[0]!=true or homeReady[1]!=true or homeReady[2]!=true or homeReady[3]!=true)
			{
				homeOnePod(enc1.Get(),hall1.Get(),0);
				homeOnePod(enc2.Get(),hall2.Get(),1);
				homeOnePod(enc3.Get(),hall3.Get(),2);
				homeOnePod(enc4.Get(),hall4.Get(),3);
				AutonShooter(rpmSet);
				Autotimer.Reset();
			}
			else if(Autotimer.Get()<1.2)
			{
				swerve(0.1,-.35,0.0);
				AutonShooter(rpmSet);

			}
			else if(Autotimer.Get()<1.2+1)
			{
				float error=Gyro1.GetAngle()-35;
				float Out = error*.02;
				swerve(0.0,0.0,Out);
				AutonShooter(rpmSet);
				SmartDashboard::PutNumber("gyro auto", Gyro1.GetAngle());
			}
			else if(Autotimer.Get()<1.2+1+8)
			{
				AutonShooter(rpmSet);
				kicker.Set(1);
				swerve(0.0,0.0,0.0);
			}
			else if(Autotimer.Get()<1.2+1+8+1.3)
			{

				AutonShooter(0.0);
				kicker.Set(0.0);
				float error=Gyro1.GetAngle()+100;
				float Out = error*.01;
				swerve(0.0,0.0,Out);


			}
			else if(Autotimer.Get()<1.2+1+8+1.3+.8)
			{
				AutonShooter(0.0);
				kicker.Set(0.0);
				swerve(.4,0.0,0.0);
			}
			else if(Autotimer.Get()<1.2+1+8+1.3+.8+1.8)
			{
				AutonShooter(0.0);
				kicker.Set(0.0);
				float error=Gyro1.GetAngle()+135;
				float Out = error*.015;
				swerve(0.0,0.0,Out);
			}
			else if(Autotimer.Get()<1.2+1+8+1.3+.8+1.8+1)
			{
				swerve(.3,0.0,0.0);
			}
			else
			{
				swerve(0.0,0.0,0.0);
			}
		}
	void AutonMode4( bool Turn)
		{
			double rpmSet = 2355;

			autoGyroOffset =-90;
			if(homeReady[0]!=true or homeReady[1]!=true or homeReady[2]!=true or homeReady[3]!=true)
			{
				homeOnePod(enc1.Get(),hall1.Get(),0);
				homeOnePod(enc2.Get(),hall2.Get(),1);
				homeOnePod(enc3.Get(),hall3.Get(),2);
				homeOnePod(enc4.Get(),hall4.Get(),3);
				AutonShooter(rpmSet);
				Autotimer.Reset();
			}
			else if(Autotimer.Get()<.5)
			{
				swerve(0.0,.35,0.0);
				AutonShooter(rpmSet);

			}
			else if(Autotimer.Get()<2)
			{
				float error=Gyro1.GetAngle()+15;
				float Out = error*.02;
				swerve(0.0,0.0,Out);
				AutonShooter(rpmSet);
			}
			else if(Autotimer.Get()<2+8)
			{
				AutonShooter(rpmSet);
				kicker.Set(1);
				swerve(0.0,0.0,0.0);
				SmartDashboard::PutNumber("gyro auto", Gyro1.GetAngle());
			}
			else if(Autotimer.Get()<2+8+1.7)
			{
				AutonShooter(0.0);
				kicker.Set(0.0);
				float error=Gyro1.GetAngle();
				float Out = error*.03;
				swerve(0.0,0.4,0.0);

			}
			else if(Autotimer.Get()<2+8.0+1.7+3.0 and Turn)
			{
				float error=Gyro1.GetAngle()-125.0;
				float Out = error*.015;
				swerve(0.0,0.0,Out);

			}
			else
			{
				swerve(0.0,0.0,0.0);
			}
		}
	void AutonMode7( bool Turn)
	{
		double rpmSet = 2200;

		autoGyroOffset =-90;
		if(homeReady[0]!=true or homeReady[1]!=true or homeReady[2]!=true or homeReady[3]!=true)
		{
			homeOnePod(enc1.Get(),hall1.Get(),0);
			homeOnePod(enc2.Get(),hall2.Get(),1);
			homeOnePod(enc3.Get(),hall3.Get(),2);
			homeOnePod(enc4.Get(),hall4.Get(),3);
			AutonShooter(rpmSet);
			Autotimer.Reset();
		}
		else if(Autotimer.Get()<.8)
		{
			swerve(0.0,.35,0.0);
			AutonShooter(rpmSet);

		}
		else if(Autotimer.Get()<.8+1.5)
		{
			float error=Gyro1.GetAngle()+44;
			float Out = error*.02;
			swerve(0.0,0.0,Out);
			AutonShooter(rpmSet);
		}
		else if(Autotimer.Get()<.8+1.5+6)
		{
			AutonShooter(rpmSet);
			kicker.Set(1);
			swerve(0.0,0.0,0.0);
			SmartDashboard::PutNumber("gyro auto", Gyro1.GetAngle());
		}
		else if(Autotimer.Get()<.8+1.5+6+2)
		{

			AutonShooter(0.0);
			kicker.Set(0.0);
			float error=Gyro1.GetAngle()-80;
			if(error>60)
			{
				error = 60;
			}
			if(error<-60)
			{
				error = -60;
			}
			float Out = error*.009;
			swerve(0.0,0.0,Out);


		}
		else if(Autotimer.Get()<.8+1.5+6+2+.8)
		{
			AutonShooter(0.0);
			kicker.Set(0.0);
			swerve(.4,0.0,0.0);
		}
		else if(Autotimer.Get()<.8+1.5+6+2+.8+1)
		{
			AutonShooter(0.0);
			kicker.Set(0.0);
			float error=Gyro1.GetAngle()-130;
			if(error>60)
			{
				error = 60;
			}
			if(error<-60)
			{
				error = -60;
			}
			float Out = error*.012;
			swerve(0.0,0.0,Out);
		}
		else if(Autotimer.Get()<.8+1.5+6+2+.8+1+1)
		{
			swerve(.3,0.0,0.0);
		}
		else
		{
			swerve(0.0,0.0,0.0);
		}
	}
	// State    Meaning
	// 0		Homing and spin up shooter
	// 1        Pause To Settle
	// 2		Shoot
	// 3		Drive
	// 4		turn 901

	int autonMode = 1;  //This is the variable that we use to select different auton modes

	void AutonomousPeriodic()
	{
		if(chooser.GetSelected()==autoName1)
		{
			autonMode1();
		}
		if(chooser.GetSelected()==autoName2)
		{
			autonMode2();
		}
		if(chooser.GetSelected()==autoName3)
		{
			AutonMode3(1.0,true);
		}
		if(chooser.GetSelected()==autoName4)
		{
			AutonMode4(true);
		}
		if(chooser.GetSelected()==autoName5)
		{
			AutonMode3(1.0,false);
		}
		if(chooser.GetSelected()==autoName6)
		{
			AutonMode4(false);
		}
		if(chooser.GetSelected()==autoName7)
		{
			AutonMode7(true);
		}
		if(chooser.GetSelected()==autoName8)
		{
			AutonMode8(1,true);
		}
		SmartDashboard::PutNumber("autonState", autonState);
		SmartDashboard::PutString("auto choice",chooser.GetSelected());
		SmartDashboard::PutNumber("timer",Autotimer.Get());
	}
	enum TELEOPSTATES {Start=0, trackGearTarget=1, trackBoilerTarget=2, homing=3, driving=4, tankMode = 5};
	TELEOPSTATES teleopState = Start;
	bool cancelHoming = false;
	bool homingTimedOut = false;
	bool homingCompleted = false;
	bool resetGyro = false;
	TELEOPSTATES lastTeleopState = Start;
	Timer teleopHomingTimer;
	float offset2[4] = {0.0,0.0,0.0,0.0};
	void TeleopInit()
	{
		for(int i=0;i<RPMSize;i++)
		{
			RPMTable[i]=0.0;
		}
		teleopHomingTimer.Start();

	}
	void TeleopPeriodic()
	{
		gearDoorPeriodic();
		visionLoop();
		if(BM.GetRawButton(1)== true){
			//Open the doors
			if( BM.GetRawButton(8) == true){
				slowOpen();
			}else{
				openDoors();
			}
		}else{
			//Close the doors
			closeDoor();
		}

		switch(teleopState)
		{
		case Start:
			//*** Do Stuff ***
			//*** Evaluate Transitions ***
			teleopState = driving;
			//***Set lastTeleopState***f
			lastTeleopState = Start;
			break;
		case trackGearTarget:
			//*** Do Stuff ***
			getframe=true;
			//*** Evaluate Transitions ***
			if(vStick.GetRawButton(5)==false)
			{
				teleopState = driving;
				getframe=false;
			}
			//***Set lastTeleopState***
			lastTeleopState = trackGearTarget;
			break;
		case trackBoilerTarget:
			//*** Do Stuff ***
			//*** Evaluate Transitions ***
			getframe=true;
			//***Set lastTeleopState***
			lastTeleopState = trackBoilerTarget;
			break;
		case homing:
			if(lastTeleopState != homing)
			{
				cancelHoming = false;
				homingTimedOut = false;
				homingCompleted = false;
				teleopHomingTimer.Reset();
				homeReady[0]= false;
				homeReady[1]= false;
				homeReady[2]= false;
				homeReady[3]= false;
			}
			//*** Do Stuff ***
			homeOnePod(enc1.Get(),hall1.Get(),0);
			homeOnePod(enc2.Get(),hall2.Get(),1);
			homeOnePod(enc3.Get(),hall3.Get(),2);
			homeOnePod(enc4.Get(),hall4.Get(),3);

			if(teleopHomingTimer.Get()>5)
			{
				homingTimedOut = true;
			}
			//*** Evaluate Transitions ***
			if(cancelHoming==true)
			{
				teleopState = driving;
				for(int i = 0; i<4;i++)
				{
					offset[i]=0.0;
				}
			}
			if(homingTimedOut==true)
			{
				teleopState = driving;
				for(int i = 0; i<4;i++)
				{
					offset[i]=0.0;
				}
			}
			if(homeReady[0]==true && homeReady[1]==true && homeReady[2]==true && homeReady[3]==true)
			{
				homingCompleted = true;
			}
			if(homingCompleted == true)
			{
				teleopState = driving;
			}
			//SmartDashboard::PutNumber("Homing State Pod1", homingstate[0]);
			//***Set lastTeleopState***
			lastTeleopState = homing;
			break;
		case driving:
			joystick();
			winchFunc();
			Shooter();
			if(vStick.GetRawButton(8)==true)
			{
				Gyro1.Reset();
			}
			//*** Evaluate Transitions ***
			if(vStick.GetRawButton(3) == true)
			{
				teleopState = homing;
			}
			if(vStick.GetRawButton(5)==true)
			{
				teleopState = trackGearTarget;
			}
			if(vStick.GetRawButton(11)==true)
			{
				teleopState = tankMode;
			}
			//***Set lastTeleopState***
			lastTeleopState = driving;
			//pdp.getCurrent(1);
			break;
		case tankMode:
			TankDrive();
			if(vStick.GetRawButton(12)==true)
			{
				teleopState = driving;
			}
			lastTeleopState = tankMode;
			break;
		default:
			break;
		}
		gyroAngle = fmod((((Gyro1.GetAngle()+autoGyroOffset)*pi)/180),2*pi);
		SmartDashboard::PutBoolean("Track Button", vStick.GetRawButton(5));
		SmartDashboard::PutNumber("state",teleopState);
		Output();

	}
	void Output()
	{
		SmartDashboard::PutNumber("offset", offset[0]);
		SmartDashboard::PutBoolean("HallEffect",hall1.Get());
		SmartDashboard::PutNumber("Gyro",gyroAngle);
		SmartDashboard::PutNumber("throttle",throttle);
		SmartDashboard::PutBoolean("Pod 1 Alignment", homeReady[0]);
		SmartDashboard::PutBoolean("Pod 2 Alignment", homeReady[1]);
		SmartDashboard::PutBoolean("Pod 3 Alignment", homeReady[2]);
		SmartDashboard::PutBoolean("Pod 4 Alignment", homeReady[3]);
		SmartDashboard::PutNumber("U",U);
		SmartDashboard::PutNumber("teleopState",teleopState);
		SmartDashboard::PutNumber("setpoint",setpoint);
		SmartDashboard::PutNumber("enc1",fmod(((enc1.Get()/497.0) *(2*pi)) * (32.0/36.0),(pi*2)));
		SmartDashboard::PutNumber("enc2",fmod(((enc2.Get()/497.0) *(2*pi)) * (32.0/36.0),(pi*2)));
		SmartDashboard::PutNumber("enc3",fmod(((enc3.Get()/497.0) *(2*pi)) * (32.0/36.0),(pi*2)));
		SmartDashboard::PutNumber("enc4",fmod(((enc4.Get()/497.0) *(2*pi)) * (32.0/36.0),(pi*2)));
		SmartDashboard::PutBoolean("hall1",hall1.Get());
		SmartDashboard::PutBoolean("hall2",hall2.Get());
		SmartDashboard::PutBoolean("hall3",hall3.Get());
		SmartDashboard::PutBoolean("hall4",hall4.Get());
		SmartDashboard::PutBoolean("homeDone",homeDone);


	}
	void TankDrive()
	{
		switch(tankState)
		{
		case 0:
			tankTimer.Start();
			tankTimer.Reset();
			tankWheelState[0]=0;
			tankWheelState[1]=0;
			tankWheelState[2]=0;
			tankWheelState[3]=0;
			tankState = 1;
			break;
		case 1:
			setTankWheelPosition(hall1.Get(),0);
			setTankWheelPosition(hall2.Get(),1);
			setTankWheelPosition(hall3.Get(),2);
			setTankWheelPosition(hall4.Get(),3);
			if(tankWheelState[0]==2 and tankWheelState[1]==2 and tankWheelState[2]==2  and tankWheelState[3]==2)
			{
				tankState = 2;
			}
			break;
		case 2:
			w1.Set(vStick.GetY());
			w4.Set(vStick.GetY());
			w2.Set(rStick.GetY());
			w3.Set(rStick.GetY());
		}
		SmartDashboard::PutNumber("tank state", tankState);
		SmartDashboard::PutNumber("tank wheel state 1", tankWheelState[0]);
	}
	void setTankWheelPosition(bool hall,int idx)
	{
		switch(tankWheelState[idx])
		{
		case 0:
			tankPower[idx]=3;
			if(hall == false)
			{
				tankWheelState[idx]=1;
				tankPower[idx]=0.0;
				tankTimerRef[idx]=tankTimer.Get();
			}
			break;
		case 1:
			tankPower[idx]=-.4;
			if(tankTimer.Get()>tankTimerRef[idx]+.2)
			{
				tankPower[idx]=0.0;
				tankWheelState[idx]=2;
			}
			break;
		case 2:
			tankPower[idx]=0.0;
			tankWheelState[idx]=2;
			break;
		}
		aw1.Set(tankPower[0]);
		aw2.Set(tankPower[1]);
		aw3.Set(tankPower[2]);
		aw4.Set(tankPower[3]);
	}
	//funtion is passed a encoder value a hall effect value and a id number for the pod
	//function is called in a loop until homeReady for that id is true or it times out
	void homeOnePod(float enc,bool hall, int idx)
	{

		float homeSpeed = 0.4;


	if(hall == false){
			if(idx==0)
			{
				aw1.Set(0);
				homeReady[0] = true;
			}
			else if(idx == 1)
			{
				aw2.Set(0);
				homeReady[1] = true;
			}
			else if(idx == 2)
			{
				aw3.Set(0);
				homeReady[2] = true;
			}
			else if(idx == 3)
			{
				aw4.Set(0);
				homeReady[3] = true;
			}
			//added an offset for the encoder to make sure all encoders were 0 after home.
			if(idx==0 || idx == 3)
			{
				offset[idx]=enc+55+280;
			}else if(idx == 1 ){
				offset[1] = enc+55; //For only pod one add
			}
			else if(idx == 2)
			{
				offset[idx]=enc+55;
			}

			}
			else{
				if(homeReady[idx]==false)
				{
				if(idx==0)
				{
					aw1.Set(homeSpeed);
					homeReady[0] = false;
				}
				else if(idx == 1)
				{
					aw2.Set(homeSpeed);
					homeReady[1] = false;
				}
				else if(idx == 2)
				{
					aw3.Set(homeSpeed);
					homeReady[2] = false;
				}
				else if(idx == 3)
				{
					aw4.Set(homeSpeed);
					homeReady[3] = false;
				}
					}
				}
	}
	void winchFunc()
	{
		if(BM.GetRawButton(1)==true)
		{
			gyro.Reset();
		}
		if(BM.GetRawButton(4) == true && BM.GetRawButton(6)==false){
			winch.Set(1.0);
		}
		else if(BM.GetRawButton(4) == true && BM.GetRawButton(6)==true)
		{
			winch.Set(0.5);
		}
		else if(BM.GetRawButton(2)==true){
			winch.Set(-.75);
		}
		else{
			winch.Set(0);
		}
	}
	void openloopShooter()
	{
		if(BM.GetRawButton(5)==true)
		{
			throttle=throttle+.007;
		}
		if(BM.GetRawButton(7)==true)
		{
			throttle=throttle-.007;
		}
		float rpmSetPoint = 2400 + (throttle * 600);
		double uThrottle = rpmSetPoint*(1.7596*.0001)+0.0502;
		if(BM.GetRawButton(8)==true)
		{
			shooter.Set(uThrottle);
		}
		else
		{
			shooter.Set(0.0);
		}
		if(BM.GetRawButton(3) == true){
			kicker.Set(.75);
		}
		else{
			kicker.Set(0.0);
		}
		SmartDashboard::PutNumber("rpm",rpmSetPoint);
		SmartDashboard::PutNumber("ShooterEnc", shooterEnc.Get());


	}
	void Shooter()
	{
		if(BM.GetRawButton(8)== true){
			//float throttle = vStick.GetThrottle();
			float rpm = ((shooterEnc.GetRate() * 60)/48);
			float AverageRPM=0.0;

			bufferIndex = fmod(bufferIndex+1,RPMSize);
			SmartDashboard::PutNumber("buffer",bufferIndex);
			RPMTable[bufferIndex]= rpm;
			for (int i = 0; i<RPMSize ; i++)
			{
				AverageRPM = AverageRPM + RPMTable[i];
				SmartDashboard::PutNumber("Rpmtable",RPMTable[i]);
			}
			AverageRPM = AverageRPM/RPMSize;
			// This makes range from 2400 - 3000
			if(BM.GetRawButton(5)==true)
			{
				throttle=throttle+.005;
			}
			if(BM.GetRawButton(7)==true)
			{
				throttle=throttle-.005;
			}
			SmartDashboard::PutNumber("THrottle",throttle);
			float rpmSetPoint = throttle*1000+1500;
			 //+ ((throttle/2)*600);//+ (1+throttle)/2 * 600;
			float kP = .00045;
			float kI = .00012;
			float kD = .0000002;

			//All Forms of Error
			ShotlastError = Shoterror;
			Shoterror = rpmSetPoint-AverageRPM;
			float iError = iError + Shoterror/1000;
			float dError = (Shoterror - ShotlastError);
			SmartDashboard::PutNumber("error",Shoterror);
			float iErrorLim =1000;

			// Cap iError
			if(iError > iErrorLim){
				iError = iErrorLim;
			}
			else if(iError < -iErrorLim){
				iError = -iErrorLim;
			}

			// Scale Controller Errors
			double uThrottle = rpmSetPoint*(1.724*.0001)+0.0321;
			double uI = iError * kI;
			double uP = Shoterror * kP;
			double uD = dError * kD;
			double uAll = uP + uI + uD + uThrottle;

			if(uAll > 1){
				uAll = 1;
			}
			else if(uAll < 0){
				uAll = 0;
			}

			float outcome = uAll;
			//float  = 0.0;
//			if(BM.GetRawButton(5)==true)
//			{
//				speedfeed = speedfeed +.1;
//			}
//			else if(BM.GetRawButton(7)==true)
//			{
//				speedfeed = speedfeed -.1;
//			}
			shooter.Set(uAll);
			SmartDashboard::PutNumber("shooter power", uAll);
			SmartDashboard::PutNumber("Set Point", rpmSetPoint);
			SmartDashboard::PutNumber("ShooterEnc", AverageRPM);


		}
		else{
			shooter.Set(0);
		}
		if(BM.GetRawButton(3) == true){
				kicker.Set(.75);
		}
		else{
			kicker.Set(0.0);
		}

				SmartDashboard::PutNumber("RAW ENC Rate",shooterEnc.GetRate());
				SmartDashboard::PutNumber("RAW ENC",shooterEnc.Get());
	}
	void AutonShooter(double rpmSetPoint){

		float rpm = ((shooterEnc.GetRate() * 60)/48);
					float AverageRPM=0.0;

					bufferIndex = fmod(bufferIndex+1,RPMSize);
					SmartDashboard::PutNumber("buffer",bufferIndex);
					RPMTable[bufferIndex]= rpm;
					for (int i = 0; i<RPMSize ; i++)
					{
						AverageRPM = AverageRPM + RPMTable[i];
						SmartDashboard::PutNumber("Rpmtable",RPMTable[i]);
					}
					AverageRPM = AverageRPM/RPMSize;
					// This makes range from 2400 - 3000
					if(BM.GetRawButton(5)==true)
					{
						throttle=throttle+.005;
					}
					if(BM.GetRawButton(7)==true)
					{
						throttle=throttle-.005;
					}
					SmartDashboard::PutNumber("THrottle",throttle);
					 //+ ((throttle/2)*600);//+ (1+throttle)/2 * 600;
					float kP = .00045;
					float kI = .00012;
					float kD = .0000002;

					//All Forms of Error
					ShotlastError = Shoterror;
					Shoterror = rpmSetPoint-AverageRPM;
					float iError = iError + Shoterror/1000;
					float dError = (Shoterror - ShotlastError);
					SmartDashboard::PutNumber("error",Shoterror);
					float iErrorLim =1000;

					// Cap iError
					if(iError > iErrorLim){
						iError = iErrorLim;
					}
					else if(iError < -iErrorLim){
						iError = -iErrorLim;
					}

					// Scale Controller Errors
					double uThrottle = rpmSetPoint*(1.724*.0001)+0.0321;
					double uI = iError * kI;
					double uP = Shoterror * kP;
					double uD = dError * kD;
					double uAll = uP + uI + uD + uThrottle;

					if(uAll > 1){
						uAll = 1;
					}
					else if(uAll < 0){
						uAll = 0;
					}

					float outcome = uAll;
					//float  = 0.0;
		//			if(BM.GetRawButton(5)==true)
		//			{
		//				speedfeed = speedfeed +.1;
		//			}
		//			else if(BM.GetRawButton(7)==true)
		//			{
		//				speedfeed = speedfeed -.1;
		//			}
					shooter.Set(uAll);
					SmartDashboard::PutNumber("shooter power", uAll);
					SmartDashboard::PutNumber("Set Point", rpmSetPoint);
					SmartDashboard::PutNumber("ShooterEnc", AverageRPM);
				SmartDashboard::PutNumber("RAW ENC Rate",shooterEnc.GetRate());
				SmartDashboard::PutNumber("RAW ENC",shooterEnc.Get());

	}
	void gearFunc(){
		if(BM.GetRawButton(1) == true){
			gear.Set(.2);
		}
		else if (BM.GetRawButton(3)==true){
			gear.Set(-.2);
		}
		else{
			gear.Set(0);
		}

	if(BM.GetRawButton(1) == true){
			gear2.Set(.2);
		}
		else if (BM.GetRawButton(3)==true){
			gear2.Set(-.2);
		}
		else{
			gear2.Set(0);
		}
	}
	//called in teleop during driver control that calls the different operator control modes
	void joystick()
	{
		float x = rStick.GetX();
		float y = -rStick.GetY();
		float w  = -vStick.GetX()/1.2;
		//slow mode
		if(rStick.GetRawButton(1)==true)
		{
			x = rStick.GetX()/2;
			y = -rStick.GetY()/2;
			w = -vStick.GetX()/4;
		}
		if(vStick.GetRawButton(1)==true)
		{
			x = rStick.GetX()/2;
			y = -rStick.GetY()/2;
			w = -vStick.GetX()/4;
		}
		//dead banding the joystick
		if(x<.05 && x > -.05)
		{
			x=0;
		}
		if(y<.05 && y> -.05)
		{
			y=0;
		}
		if(w<.07 && w>-.07)
		{
			w=0.0;
		}
		//converting x,y into a polar coordinate (M,Theta)
		float Theta;
		float M = sqrt(x*x+y*y);
		//correcting for atan to be from 0 - 2*pi
		if(x < 0)
		{
			Theta = atan2(x,y) + 2*3.14159;
		}
		else
		{
			Theta = atan2(x,y);
		}
		//modify the theta commanded by the gyro value to get field centric
		//if button 1 is pressed do slow robot centric
		if(rStick.GetRawButton(1)==false)
		{
			Theta = fmod(Theta - gyroAngle,2*pi);
		}
		//convert back to x, y from polar
		y=sin(Theta)*M;
		x=cos(Theta)*M;
		//call swerve drive
		swerve(x,y,w);
	}
	//main call for robot centric driving giving a percent from 0-1 for x, y
	void swerve(float x,float y,float w)
	{
		SmartDashboard::PutNumber("swerve x",x);
		SmartDashboard::PutNumber("swerve y",y);
		SmartDashboard::PutNumber("swerve w",w);

		w=w/17;
		if(w <= .003 && w >= -.003 ){
			w = 0;
		}
		//Sets distance in inches between center of robot and pod
		float x1 = 23.5/2.0;
		float y2 =26.0/2.0;
		//Sets x and y components of wheels
		float V1x = x + w * x1;
		float V1y = y - w * y2;
		float V2x = x  - w * x1;
		float V2y = y - w * y2;
		float V3x = x - w * x1;
		float V3y = y + w * y2;
		float V4x = x + w * x1;
		float V4y = y + w * y2;
		//converts to polar coords
		float Theta1 = getTheta(x,y,w,V1x,V1y,0);
		float Theta2 = getTheta(x,y,w,V2x,V2y,1);
		float Theta3 = getTheta(x,y,w,V3x,V3y,2);
		float Theta4 = getTheta(x,y,w,V4x,V4y,3);
		float V1 = sqrt(V1x*V1x+V1y*V1y);
		float V2 = sqrt(V2x*V2x+V2y*V2y);
		float V3 = sqrt(V3x*V3x+V3y*V3y);
		float V4 = sqrt(V4x*V4x+V4y*V4y);
		//makes sure all values are between 0 and 1 and that they are scaled back by the highest number
		float max12 = fmax(V1,V2);
		float max34 = fmax(V3,V4);
		float TrueMax = fmax(max12,max34);
		float CompensationTerm = 1.0;
		if(TrueMax>1)
		{
			CompensationTerm = 1.0/TrueMax;
		}
		V1=V1*reverse[0]*CompensationTerm;
		V2=V2*reverse[1]*CompensationTerm;
		V3=V3*reverse[2]*CompensationTerm;
		V4=V4*reverse[3]*CompensationTerm;
		//send commands to the wheels
		//!!!!NOTE: this is not a pid loop it is just current ramping!!!
		pidWheelPower(V1,0);
		pidWheelPower(V2,1);
		pidWheelPower(V3,2);
		pidWheelPower(V4,3);
		//Get encoder from 0 to 2 pi with the offsets from homing and the gear ratio of the motor
		float encAngle1 = fmod((((enc1.Get()	-offset[0])/497.0) *(2*3.141596)) * (32.0/36.0),	(3.14159265*2));
		float encAngle2 = fmod((((enc2.Get()	-offset[1])/497.0) *(2*3.141596)) * (32.0/36.0),	(3.14159265*2));
		float encAngle3 = fmod((((enc3.Get() 	-offset[2])/497.0) *(2*3.141596)) * (32.0/36.0),	(3.14159265*2));
		float encAngle4 = fmod((((enc4.Get()	-offset[3])/497.0) *(2*3.141596)) * (32.0/36.0),	(3.14159265*2));
		//when controller is 0 keep last angle
		//setting variables for pid on the position
		lastError1=error1;
		lastError2=error2;
		lastError3=error3;
		lastError4=error4;
		error1=getError(Theta1,encAngle1,0);
		error2=getError(Theta2,encAngle2,1);
		error3=getError(Theta3,encAngle3,2);
		error4=getError(Theta4,encAngle4,3);
		Accum1=Accum1+error1;
		Accum2=Accum2+error2;
		Accum3=Accum3+error3;
		Accum4=Accum4+error4;
		//checks to see if there is two much total current draw
		TotalPower = fabs(pidAngle(error1,Accum1,lastError1))+ fabs(pidAngle(error2,Accum2,lastError2)) + fabs(pidAngle(error3,Accum3,lastError3))+ fabs(pidAngle(error4,Accum4,lastError4))+ fabs(CurrentWheelPower[0]) + fabs(CurrentWheelPower[1]) + fabs(CurrentWheelPower[2]) +fabs(CurrentWheelPower[3]);
		PowerScaler=1;
		if(TotalPower>POWERLIMIT)
		{
			PowerScaler = POWERLIMIT/TotalPower;
		}
		//sets position of the pods
		aw1.Set(pidAngle(error1,Accum1,lastError1)*PowerScaler);
		aw2.Set(pidAngle(error2,Accum2,lastError2)*PowerScaler);
		aw3.Set(pidAngle(error3,Accum3,lastError3)*PowerScaler);
		aw4.Set(pidAngle(error4,Accum4,lastError4)*PowerScaler);
		//sets the speed of the wheels
		w1.Set(CurrentWheelPower[0] *PowerScaler);
		w2.Set(CurrentWheelPower[1] *PowerScaler);
		w3.Set(CurrentWheelPower[2] *PowerScaler);
		w4.Set(CurrentWheelPower[3] *PowerScaler);

		//out put to the dashboard
		SmartDashboard::PutNumber("PowerScaler", PowerScaler);
		SmartDashboard::PutNumber("PowerTotal", TotalPower);
		SmartDashboard::PutNumber("w",w);
		SmartDashboard::PutNumber("error1",error1);
		SmartDashboard::PutNumber("EncAngle", encAngle1);
		SmartDashboard::PutNumber("CurrentWheelPower 1", CurrentWheelPower[0]);
//		SmartDashboard::PutNumber("Accum1",Accum1);
//		SmartDashboard::PutNumber("Error Long", errorlong);
//		SmartDashboard::PutNumber("outcome",pidAngle(error1,Accum1,lastError1) );
//		SmartDashboard::PutNumber("joystickmode",joystickmode);
//
//		SmartDashboard::PutNumber("encraw",enc1.Get());
//		SmartDashboard::PutNumber("V3x",V3x);
//		SmartDashboard::PutNumber("V3y",V3y);
//		SmartDashboard::PutNumber("V3",V3);
//		SmartDashboard::PutNumber("Teta3",Theta3);
//		SmartDashboard::PutNumber("x",x);
//		SmartDashboard::PutNumber("y",y);
//		SmartDashboard::PutNumber("w",w);
		SmartDashboard::PutNumber("reverse1",reverse[0]);
//		SmartDashboard::PutNumber("reverse2",reverse[1]);
//		SmartDashboard::PutNumber("reverse3",reverse[2]);
//		SmartDashboard::PutNumber("reverse4",reverse[3]);
	}





//function returns the shortest route to the point and if the pod must invert speed to achieve it
// example: if the robot drive full forward then full back the wheels just invert speeds
float getError(float Theta,float encAngle, int idx)
{
	float error = Theta - encAngle;
	//finds the closest route to the setpoint including reverse values
	//will only travel 1/2 pi
	if(error < (-pi/2))
	{
		errorlong = error+pi;
		reverse[idx] = -1.0;
	}
	else if(error > pi/2)
	{
		errorlong = error - pi;
		reverse[idx] = -1.0;
	}
	else{
		errorlong = error;
		reverse[idx] = 1.0;
	}
	return errorlong;

}
float getTheta(float x,float y,float w,float Vx,float Vy,int idx)
{
	//takes the x and y to give the theta from 0 to 2 pi
	float Theta;
	if(rStick.GetRawButton(8)==true)
	{
		Theta=0.0;
	}
	else if(rStick.GetRawButton(10)==true)
	{
		Theta = 3.141592*.5;
	}
	else if(x==0 && y==0 && w==0)
	{
		Theta = lastThetalist[idx];
	}
	else
	{
		if(Vx < 0)
		{
			Theta = atan2(Vx,Vy) + 2*3.14159;
		}
		else
		{
			Theta = atan2(Vx,Vy);
		}
		lastThetalist[idx]=Theta;
	}
	return Theta;
}
//current ramping for the wheels
void pidWheelPower(float SetPoint, int idx)
{
	float stepValue = .06;
	float AcceptedError = .13;

	float PowerError = CurrentWheelPower[idx]-SetPoint;
	stepValue = PowerError * .13;
	if(stepValue<.06)
	{
		stepValue = .06;
	}
	if(CurrentWheelPower[idx]>SetPoint)
	{
		if(fabs(PowerError)<AcceptedError)
		{
			CurrentWheelPower[idx]=SetPoint;
		}
		else
		{
			CurrentWheelPower[idx]= CurrentWheelPower[idx] - stepValue;
		}
	}
	else if (CurrentWheelPower[idx]<SetPoint)
	{
		if(fabs(PowerError)< AcceptedError)
		{
			CurrentWheelPower[idx]= SetPoint;
		}
		else
		{
			CurrentWheelPower[idx]= CurrentWheelPower[idx] + stepValue;
		}
	}
}
// a PID loop for the position of the wheel returns the motor power to be provided
float pidAngle(float error,float Accum, float lastError){
	//really only uses P control
	float kP = 2.5;
	float kI = .000;
	float kD = .0;
	float dError = error - lastError;
	pOut = error * kP;
	if(Accum>10)
	{
		Accum=10;
	}
	float iOut = Accum * kI;
	dOut = dError * kD;
	float outcome = pOut + dOut + iOut;
	//bounds the values
	if(outcome > 1)
	{
		outcome = 1;
	}
	else if(outcome < -1)
	{
		outcome = -1;
	}
	return outcome;
	}



void TestPeriodic()
	{

	}




	//These methods implement the gear drop subsystem
	int gearDoorState = 0;
			// 0 - starting
			// 1 - open
			// 2 - closing
			// 3 - closed
			// 4 - opening
			// 5 - slowOpening
	int lastGearDoorState = 0;
	Timer gearDoorTimer;

	void gearDoorPeriodic(){
	// this function is called periodically to set motor stuff
		SmartDashboard::PutNumber("gearstate",gearDoorState);
		SmartDashboard::PutNumber("gearTimer",gearDoorTimer.Get());
		SmartDashboard::PutNumber("lastGearState",lastGearDoorState);

		switch( gearDoorState){
				case 0:
					//STARTING State
					gearDoorState = 3;
					gearDoorTimer.Start();
				break;
				case 1:
					//OPENED State
					gear.Set(HOLDOPENPOWER);
					gear2.Set(HOLDOPENPOWER);
				break;

				case 2:
					//CLOSING State
					gear.Set(CLOSEMOTORPOWER);
					gear2.Set(CLOSEMOTORPOWER);
				break;

				case 3:
					//CLOSED State
					//gear.Set(CLOSEMOTORPOWER);
					gear.Set(HOLDCLOSEDPOWER);
					gear2.Set(HOLDCLOSEDPOWER);
				break;
				case 4:
					//OPENING State
					gear.Set(OPENMOTORPOWER);
					gear2.Set(OPENMOTORPOWER);
				break;
				case 5:
					//SLOWOPENING State
					gear.Set (SLOWOPENINGPOWER);
					gear2.Set (SLOWOPENINGPOWER);
				break;
				default:
					gearDoorState = 2;
		}



	}



	void slowOpen(){
		switch( gearDoorState){
				case 0:
					gearDoorState = 5;
				break;
				// opening to open
				case 1:
					gearDoorState = 5;
				break;
				//closing to opening
				case 2:
					gearDoorState = 5;
				break;
				//opeing
				case 3:
					gearDoorState = 5;
				break;
				case 4:
					gearDoorState = 5;
				break;

				case 5:
					gearDoorState = 5;
				break;
				default:
					gearDoorState = 5;
				}
		lastGearDoorState = gearDoorState;
	}


	// 0 - starting
	// 1 - open
	// 2 - closing
	// 3 - closed
	// 4 - opening
	// 5 - slowOpening

	void openDoors(){
		//This function opens the gear doors
		switch( gearDoorState){
			case 0:
				gearDoorState = 4;
			break;
			// opening to open
			case 1:
				gearDoorState = 1;
			break;
			//closing to opening
			case 2:
				gearDoorState = 4;
			break;
			//opening
			case 3:
				gearDoorState = 4;
			break;
			case 4:
				//OpeningState
				if(lastGearDoorState != 4){
					gearDoorTimer.Reset();
				}
				if(gearDoorTimer.Get()>1){
					gearDoorState = 4; //Go to open
				}else{
					gearDoorState = 4; //Stay in opening
				}
			break;
			case 5:
				gearDoorState = 4;
			break;

			default:
				gearDoorState = 4;
		}
		lastGearDoorState = gearDoorState;
		gearDoorTimer.Reset();

	}


	// 0 - starting
	// 1 - open
	// 2 - closing
	// 3 - closed
	// 4 - opening
	// 5 - slowOpening

	void closeDoor(){
		//this function closes the gear doors
		//This function opens the gear doors
		switch( gearDoorState){
			case 0:
				gearDoorState = 2;
			break;
			// opening to open
			case 1:
				gearDoorState = 2;
			break;
			//closing to opening
			case 2:

				if(lastGearDoorState != 2){
					gearDoorTimer.Reset();
				}
				if(gearDoorTimer.Get()>1){
					gearDoorState = 3;
				}else{
					gearDoorState = 2;
				}
			break;
			//opening
			case 3:
				gearDoorState = 3;
			break;
			case 4:
				gearDoorState = 2;
			break;
			default:
				gearDoorState = 2;
		}
		lastGearDoorState = gearDoorState;
	}


	//************************** this is the vision code *************************************//
	//
	//
	//
	//************************* this is the vision code **************************************//
	bool getStart() //checks whether if it is start of the normal frame, CC frame, or the data is out of sync
			{
			  uint16_t w, lastw;

			  lastw = 0xffff;

			  while(true)
			  {
			    w = getWord(); //This it the function right underneath
			    if (w==0 && lastw==0)
				{
			      //delayMicroseconds(10);
				  return false;
				}
			    else if (w==PIXY_START_WORD && lastw==PIXY_START_WORD)
				{
			      blockType = NORMAL_BLOCK;
			      return true;
				}
			    else if (w==PIXY_START_WORD_CC && lastw==PIXY_START_WORD)
				{
			      blockType = CC_BLOCK;
			      return true;
				}
				else if (w==PIXY_START_WORDX) //when byte recieved was 0x55aa instead of otherway around, the code syncs the byte
				{
				  printf("Pixy: reorder");
				  getByte(); // resync
				}
				lastw = w;
			  }
			}

		uint16_t getWord() //Getting two Bytes from Pixy (The full information)
		{
			unsigned char buffer[2] = {0, 0};

			i2c->ReadOnly(2, buffer);
			//printf("\n c: %d ",buffer[0]);
			printf("\n w: %d",buffer[1]<<8|buffer[0]);
			return (buffer[1] << 8) | buffer[0]; //shift buffer[1] by 8 bits and add( | is bitwise or) buffer[0] to it
		}

		uint8_t getByte()//gets a byte
		{
			unsigned char buffer[1] = {0};

			i2c->ReadOnly(1, buffer);
			return buffer[0];
		}
		uint8_t getBlocks(uint16_t maxBlocks)
		{
		  blocks[0] = {0}; //resets the array - clears out data from previous reading
		  uint8_t i;
		  uint16_t w, checksum, sum;
		  Block *block;
		  if (!skipStart) //when computer has not seen 0xaa55 (starting frame)
		  {
			  if (getStart()==false)
					return 0;
		  }
		  else
			skipStart = false;

		  for(blockCount=0; blockCount<maxBlocks && blockCount<PIXY_MAXIMUM_ARRAYSIZE; blockCount++)
		  {
		    checksum = getWord();
		    SmartDashboard::PutNumber("checksum",checksum);
		    if (checksum==PIXY_START_WORD) // we've reached the beginning of the next frame - checking for 0xaa55
		    {
		      skipStart = true; //starts this function
			  blockType = NORMAL_BLOCK;
			  //Serial.println("skip");
		      return blockCount;
		    }
			else if (checksum==PIXY_START_WORD_CC) //we've reacehd the beginning of the next frame - checking for 0xaa56
			{
			  skipStart = true;
			  blockType = CC_BLOCK;
			  return blockCount;
			}
		    else if (checksum==0)
		      return blockCount;

			//if (blockCount>blockArraySize)
				//resize();

			block = blocks + blockCount;

		    for (i=0, sum=0; i<sizeof(Block)/sizeof(uint16_t); i++)
		    {
		      w = getWord();
		      sum += w; //sum = w + sum
		      *((uint16_t *)block + i) = w; //converts block to interger value
		    }
		    if (checksum==sum)
		    {
		      blockCount++;
		    	SmartDashboard::PutNumber("pixy error", 0.0);
		    }
		    else
		      SmartDashboard::PutNumber("pixy error", 1.0);

			w = getWord(); //when this is start of the frame
			if (w==PIXY_START_WORD)
			  blockType = NORMAL_BLOCK;
			else if (w==PIXY_START_WORD_CC)
			  blockType = CC_BLOCK;



		  }
		  return blockCount;
		}

		void visionLoop()
		{

			Block target1;
			Block target2;
			int target1Index=0;
			int target2Index=0;
			float target1area=0;
			float target2area=0;
			float deltaX;
			uint16_t NumberOfTargets = getBlocks(20);
			//blocks[0].print();
			//blocks[1].print();

			if(NumberOfTargets==0)
			{
				lostTargets++;
			}
			else
			{
				lostTargets=0;
			}
			if(lostTargets>5 || NumberOfTargets !=0)
			{
				for(int i =0; i<NumberOfTargets; i++)
				{
					if(i == 0)
					{
						target1area = blocks[i].width*blocks[i].height;
						target1Index=0;
					}
					else
					{
						if(target1area<blocks[i].width*blocks[i].height)
						{
							target2area=target1area;
							target2Index=target1Index;
							target1Index=i;
							target1area=blocks[i].width*blocks[i].height;
						}
						else if(target2area<blocks[i].width*blocks[i].height)
						{
							target2area = blocks[i].width*blocks[i].height;
							target2Index=i;
						}
					}
				}
//				if(blocks[target1Index].x<blocks[target2Index].x)
//				{
//					target1=blocks[target1Index];
//					target2=blocks[target2Index];
//				}
//				if(blocks[target1Index].x<blocks[target2Index].x)
//				{
//					target1=blocks[target2Index];
//					target2=blocks[target1Index];
//				}
				target1=blocks[target1Index];
				target2=blocks[target2Index];
				SmartDashboard::PutNumber("target1",target1.x);
				SmartDashboard::PutNumber("target2",target2.x);
				SmartDashboard::PutNumber("target1Index",target1Index);
				SmartDashboard::PutNumber("target2Index",target2Index);
				deltaX = target2.x-target1.x;
			}

			printf("blocks: ");printf("%d", NumberOfTargets);printf("\n"); //prints number of block to the console

			SmartDashboard::PutNumber("xvalue1",blocks[0].x);
			SmartDashboard::PutNumber("xvalue1",blocks[1].x);
			SmartDashboard::PutNumber("deltaX",deltaX);
			SmartDashboard::PutNumber("TargetsLost",lostTargets);
			SmartDashboard::PutNumber("number of targets",NumberOfTargets);
			 // prints x, y, width, and etc. to the console (the vairables in the block object)
			printf("\n"); //new line(space)
			SmartDashboard::PutBoolean("started",getStart());
		}









	//************************** this is the end of the vision code *************************************//
	//
	//
	//
	//************************* this is the end of the vision code **************************************//




};

START_ROBOT_CLASS(Robot)
