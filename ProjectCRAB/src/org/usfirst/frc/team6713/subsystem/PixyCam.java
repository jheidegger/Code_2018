package org.usfirst.frc.team6713.subsystem;

import java.util.ArrayList;

import org.usfirst.frc.team6713.subsystem.Drivetrain.systemStates;

import Robot.Constants;
import Vision.*;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

public class PixyCam extends Subsystem{
	
	private static PixyCam instance;
	
	PixyPacket values;
	I2C pixy;
	PixyPacket[] packets;
	PixyException pExc;
	String print;
	
	double Xtest = 0;
	double Ytest = 0;
	double widthTest = 0;
	double heightTest = 0;
	
	int SIZE = Constants.AVG_LIST_SIZE;
	ArrayList<Double> xValues = new ArrayList<Double>();
	double xSum = 0;
	double averageX = 0;
	
	ArrayList<Double> areaValues = new ArrayList<Double>();
	double areaSum = 0;
	double averageArea = 0;

	
	public PixyCam() {
		pixy = new I2C(Port.kOnboard, 0x54);
		packets = new PixyPacket[7];
		pExc = new PixyException(print);
	}
	
	public enum systemStates{
		NEUTRAL, 
		TRACKING_CUBE
	}
	
	private systemStates currentState;
	
	public static PixyCam getInstance()
	{
		if(instance == null)
		{
			instance = new PixyCam();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	@Override
	public void registerLoop() {
		Loop_Manager.getInstance().addLoop(new Loop()
		{

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onloop() {
				// TODO Auto-generated method stub
				switch(currentState) {
				case NEUTRAL:
					//manualDrive();
				case TRACKING_CUBE:
					try {
						readPacket(1);
					} catch (PixyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			@Override
			public void stop() {
				// TODO Auto-generated method stub
				
			}
	
		});
	}
	
	//This method parses raw data from the pixy into readable integers
	public int cvt(byte upper, byte lower) {
		return (((int)upper & 0xff) << 8) | ((int)lower & 0xff);
	}
	
	//This method gathers data, then parses that data, and as	ns the ints to global variables
	public PixyPacket readPacket(int Signature) throws PixyException { //The signature should be which number object in 
		int Checksum;												   //pixymon you are trying to get data for
		int Sig;
		byte[] rawData = new byte[32];
		
		try{
			pixy.readOnly(rawData, 32);
		} catch (RuntimeException e){}
		
		if(rawData.length < 32){
			System.out.println("byte array length is broken");
			return null;
		}
		
		for (int i = 0; i <= 16; i++) {
			int syncWord = cvt(rawData[i+1], rawData[i+0]); //Parse first 2 bytes
			if (syncWord == 0xaa55) { //Check is first 2 bytes equal a "sync word", which indicates the start of a packet of valid data
				syncWord = cvt(rawData[i+3], rawData[i+2]); //Parse the next 2 bytes
				if (syncWord != 0xaa55){ //Shifts everything in the case that one syncword is sent
					i -= 2;
				}
				
				//This next block parses the rest of the data
				Checksum = cvt(rawData[i+5], rawData[i+4]);
				Sig = cvt(rawData[i+7], rawData[i+6]);
				if(Sig <= 0 || Sig > packets.length){
					break;
				}
				
				packets[Sig - 1] = new PixyPacket();
				packets[Sig - 1].X = cvt(rawData[i+9], rawData[i+8]);
				Xtest = (double)packets[Sig-1].X;
				
				//System.out.println(cvt(rawData[i+9], rawData[i+8]));
				packets[Sig - 1].Y = cvt(rawData[i+11], rawData[i+10]);
				Ytest = (double)packets[Sig-1].Y;
				
				//System.out.println(cvt(rawData[i+11], rawData[i+10]));
				packets[Sig - 1].Width = cvt(rawData[i+13], rawData[i+12]);
				widthTest = (double)packets[Sig-1].Width;
				
				//System.out.println(cvt(rawData[i+13], rawData[i+12]));
				packets[Sig - 1].Height = cvt(rawData[i+15], rawData[i+14]);
				heightTest = (double)packets[Sig-1].Height;
				
				//System.out.println(cvt(rawData[i+15], rawData[i+14]));
				//Checks whether the data is valid using the checksum *This if block should never be entered*
				if (Checksum != Sig + packets[Sig - 1].X + packets[Sig - 1].Y + packets[Sig - 1].Width + packets[Sig - 1].Height) {
					packets[Sig - 1] = null;
					throw pExc;
				}
				break;
			}
		}
		//Assigns our packet to a temp packet, then deletes data so that we dont return old data
		PixyPacket pkt = packets[Signature - 1];
		packets[Signature - 1] = null;
		return pkt;
	}
	
	public double getX() {
		return Xtest;
	}
	public double getY() {
		return Ytest;
	}
	public double getWidth() {
		return widthTest;
	}
	public double getHeight() {
		return heightTest;
	}
	public double getArea() {
		return (heightTest*widthTest);
	}
	public double getAvgX() {
		if (xValues.size() == SIZE) {
			for (double value : xValues) {
				xSum += value;
			}
			averageX = xSum / SIZE;
			xValues.clear();
			xSum = 0;
			return averageX; 
		}
		else {
			return 0;
		}
	}
	public double getAvgArea() {
		if (areaValues.size() == SIZE) {
			for (double value : areaValues) {
				areaSum += value;
			}
			averageArea = areaSum / SIZE;
			areaValues.clear();
			areaSum = 0;
			return averageArea; 
		}
		else {
			return 0;
		}
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
	public void setSystemState(systemStates wanted) {
		currentState = wanted;
	}
}