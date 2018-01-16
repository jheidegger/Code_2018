package org.usfirst.frc.team6713.subsystem;

import edu.wpi.first.wpilibj.I2C;

class Block
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
}

public class PixyCam extends Subsystem {

	enum BlockType
	{
		NORMAL_BLOCK, //normal color recognition
		CC_BLOCK	  //color-code(chnage in angle) recognition
	};
	I2C i2c;
	void PixyCam(){
		BlockType blockType;// it is the enum on the top
		bool  skipStart;	//skips to check 0xaa55, which is byte that tells pixy it is start of new frame
		byte blockCount; //How many signatured objects are there?
		byte blockArraySize; //not used in the code
		Block blocks[100]; //array that stores blockCount array
		
	}
	bool getStart() //checks whether if it is start of the normal frame, CC frame, or the data is out of sync
	{
	  byte w, lastw;

	  lastw = (byte) 0xffff;

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

byte getWord() //Getting two Bytes from Pixy (The full information)
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
uint8_t getBlocks(byte maxBlocks)
{
  blocks[0] = {0}; //resets the array - clears out data from previous reading
  uint8_t i;
  byte w, checksum, sum;
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
    SmartDashboard.PutNumber("checksum",checksum);
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

    for (i=0, sum=0; i<sizeof(Block)/sizeof(byte); i++)
    {
      w = getWord();
      sum += w; //sum = w + sum
      *((byte *)block + i) = w; //converts block to interger value
    }
    if (checksum==sum)
    {
      blockCount++;
    	SmartDashboard.PutNumber("pixy error", 0.0);
    }
    else
      SmartDashboard.PutNumber("pixy error", 1.0);

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
	byte NumberOfTargets = (byte)getBlocks(20);
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
//		if(blocks[target1Index].x<blocks[target2Index].x)
//		{
//			target1=blocks[target1Index];
//			target2=blocks[target2Index];
//		}
//		if(blocks[target1Index].x<blocks[target2Index].x)
//		{
//			target1=blocks[target2Index];
//			target2=blocks[target1Index];
//		}
		target1=blocks[target1Index];
		target2=blocks[target2Index];
		SmartDashboard.PutNumber("target1",target1.x);
		SmartDashboard.PutNumber("target2",target2.x);
		SmartDashboard.PutNumber("target1Index",target1Index);
		SmartDashboard.PutNumber("target2Index",target2Index);
		deltaX = target2.x-target1.x;
	}

	printf("blocks: ");printf("%d", NumberOfTargets);printf("\n"); //prints number of block to the console

	SmartDashboard.PutNumber("xvalue1",blocks[0].x);
	SmartDashboard.PutNumber("xvalue1",blocks[1].x);
	SmartDashboard.PutNumber("deltaX",deltaX);
	SmartDashboard.PutNumber("TargetsLost",lostTargets);
	SmartDashboard.PutNumber("number of targets",NumberOfTargets);
	 // prints x, y, width, and etc. to the console (the vairables in the block object)
	printf("\n"); //new line(space)
	SmartDashboard.PutBoolean("started",getStart());
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

}
