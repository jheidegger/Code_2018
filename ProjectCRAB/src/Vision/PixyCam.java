package Vision;

import java.util.ArrayList;

import Robot.Constants;
import Subsystem.Loop;
import Subsystem.Loop_Manager;
import Subsystem.Subsystem;
import Subsystem.Drivetrain.systemStates;
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
	
	private double Xtest = 0;
	private double Ytest = 0;
	private double widthTest = 0;
	private double heightTest = 0;
	
	private int SIZE = Constants.AVG_LIST_SIZE;
	
	ArrayList<Double> xValues = new ArrayList<Double>();
	private double xSum = 0;
	private double averageX = 0;
	
	ArrayList<Double> areaValues = new ArrayList<Double>();
	private double areaSum = 0;
	private double averageArea = 0;

	
	private PixyCam() {
		pixy = new I2C(Port.kOnboard, 0x54);
		packets = new PixyPacket[7];
		pExc = new PixyException(print);
	}
	
	public static PixyCam getInstance()
	{
		return instance;
	}
	
	public enum systemStates{
		NEUTRAL, 
		TRACKING_CUBE
	}
	
	private systemStates currentState;
	
	//This method parses raw data from the pixy into readable integers
	public int convertToInt(byte upper, byte lower) {
		return (((int)upper & 0xff) << 8) | ((int)lower & 0xff);
	}
	
	//This method gathers data, then parses that data, and sets it to private variables that can be grabbed by the functions below
	public PixyPacket readPacket(int Signature) throws PixyException { //The signature should be which number object in 
		int Checksum;												   //pixymon you are trying to get data for
		int Sig;
		byte[] rawData = new byte[32];
		
		try{
			pixy.readOnly(rawData, 32);
		} catch (RuntimeException e){}
		
		if(rawData.length < 32){
			System.out.println("Byte array length is broken");
			return null;
		}
		
		for (int i = 0; i <= 16; i++) {
			int syncWord = convertToInt(rawData[i+1], rawData[i+0]); //Parse first 2 bytes
			if (syncWord == 0xaa55) { //Check is first 2 bytes equal a "sync word", which indicates the start of a packet of valid data
				syncWord = convertToInt(rawData[i+3], rawData[i+2]); //Parse the next 2 bytes
				if (syncWord != 0xaa55){ //Shifts everything in the case that one syncword is sent
					i -= 2;
				}
				//This next block parses the rest of the data
				Checksum = convertToInt(rawData[i+5], rawData[i+4]);
				Sig = convertToInt(rawData[i+7], rawData[i+6]);
				if(Sig <= 0 || Sig > packets.length){
					break;
				}
				packets[Sig - 1] = new PixyPacket();
				packets[Sig - 1].X = convertToInt(rawData[i+9], rawData[i+8]);
				Xtest = (double)packets[Sig-1].X;
				
				packets[Sig - 1].Y = convertToInt(rawData[i+11], rawData[i+10]);
				Ytest = (double)packets[Sig-1].Y;
				
				packets[Sig - 1].Width = convertToInt(rawData[i+13], rawData[i+12]);
				widthTest = (double)packets[Sig-1].Width;
				
				packets[Sig - 1].Height = convertToInt(rawData[i+15], rawData[i+14]);
				heightTest = (double)packets[Sig-1].Height;
				
				//Checks whether the data is valid using the checksum
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
	
	public void setSystemState(systemStates wanted) {
		currentState = wanted;
	}
}