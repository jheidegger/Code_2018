package org.usfirst.frc.team6713.robot;

import edu.wpi.first.wpilibj.I2C;

public class PixyTest 
{
	private static I2C port;
	private static int PIXY_ADDRESS = 0x54;
	private static final int BLOCK_SIZE=50;
	byte[] buffer = new byte[BLOCK_SIZE];
	
	public PixyTest() {
		port = new I2C(I2C.Port.kOnboard, PIXY_ADDRESS);
	}
	
	public String test() 
	{
		if(!(port.read(PIXY_ADDRESS, BLOCK_SIZE, buffer)))
		{
			return "read successfully";
		}
		else
			return "didnt read successfully";
	}
	public byte[] getBytes() {
		return buffer;
	}
	//Start reading and printing buffer on Monday
}
