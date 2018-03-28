package Subsystem;

import java.util.ArrayList;

import org.usfirst.frc.team6713.robot.*;

import Vision.PixyException;
import Vision.PixyPacket;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

public class PixyCam {
private static PixyCam instance = new PixyCam();
	
	PixyPacket values;
	I2C pixy;
	PixyPacket[] packets;
	PixyException pExc;
	String print;
	
	double Xtest = 0;
	double Ytest = 0;
	double widthTest = 0;
	double heightTest = 0;
	
	int SIZE = 7;
	double[] xValues = {0,0,0,0,0,0,0};
	double averageX = 0;
	int idxX = 0;
	
	double[] areaValues = {0,0,0,0,0,0,0};
	double areaSum = 0;
	double averageArea = 0;
	int idxArea = 0;

	
	public PixyCam() {
		//pixy = new I2C(Port.kOnboard, 0x54);
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
			return instance;
		
	}

	public void track_cube() {
		try {
			readPacket(1);
		} catch (PixyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setAvgX();
		setAvgArea();
		
	}
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
	private void setAvgX() {
		xValues[idxX] = getX();
		averageX = (xValues[0] + xValues[1] + xValues[2] + xValues[3] +xValues[4] + xValues[5] +xValues[6]) / 7;
		idxX++; 
		if(idxX >6) {
			idxX = 0; 
		}
	}
	public double getAvgX() {
		return averageX;
	}
	private void setAvgArea() {
		areaValues[idxArea] = getArea();
		averageArea = (areaValues[0] + areaValues[1] + areaValues[2] + areaValues[3] +areaValues[4] + areaValues[5] +areaValues[6]) / 7;
		idxArea++; 
		if(idxArea >6) {
			idxArea = 0; 
		}
	}
	public double getAvgArea() {
		return averageArea;
	}
	
	public void setSystemState(systemStates wanted) {
		currentState = wanted;
	}
}