package Subsystem;

import java.util.ArrayList;

import Vision.PixyException;
import Vision.PixyPacket;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

public class PixyCam2 {
	I2C i2c;
	public PixyCam2()
	{
		i2c = new I2C(Port.kOnboard, 0x54);
	}
	static int g_skipStart = 0;
	static ArrayList<PixyPacket> blocks = new ArrayList<PixyPacket>();
	enum BlockType
	{
	  NORMAL_BLOCK,
	  CC_BLOCK // color code block
	} ;
	
	
	static BlockType g_blockType; // use this to remember the next object block type between function calls
	private short PIXY_START_WORD =  (short) 0xaa55;
	private BlockType NORMAL_BLOCK;
	private short PIXY_START_WORD_CC = (short) 0xaa56;
	private BlockType CC_BLOCK;
	private short PIXY_ARRAYSIZE = 100;
	private short PIXY_START_WORDX =  0x55aa;

	private int getStart()
	{
	  int w, lastw;

	  lastw = (short) 0xffff; // some inconsequential initial value

	  while(true)
	  {
	    w = getWord();
	    if (w==0 && lastw==0)
	      return 0; // in I2C and SPI modes this means no data, so return immediately
	    else if (w==PIXY_START_WORD && lastw==PIXY_START_WORD)
	    {
	      g_blockType = NORMAL_BLOCK; // remember block type
	      return 1; // code found!
	    }
	    else if (w==PIXY_START_WORD_CC && lastw==PIXY_START_WORD)
	    {
	      g_blockType = CC_BLOCK; // found color code block
	      return 1;
	    }    
	    else if (w==PIXY_START_WORDX ) // this is important, we might be juxtaposed 
	      getByte(); // we're out of sync! (backwards)
	    lastw = w; // save
	  }
	}
	int getBlocks(int maxBlocks)
	{
	  int i;
	  int w, blockCount, checksum, sum;
	  PixyPacket block;
	  blocks.clear();
	  if (g_skipStart != 0)
	  {
	    if (getStart()==0)
	      return 0;
	  }
	  else
	    g_skipStart = 0;

	  for(blockCount=0; blockCount<maxBlocks && blockCount<PIXY_ARRAYSIZE ;)
	  {
	    checksum = getWord();
	    if (checksum==PIXY_START_WORD) // we've reached the beginning of the next frame
	    {
	      g_skipStart = 1;
	      g_blockType = NORMAL_BLOCK;
	      return blockCount;
	    }
	    else if (checksum==PIXY_START_WORD_CC)
	    {
	      g_skipStart = 1;
	      g_blockType = CC_BLOCK;
	      return blockCount;
	    }
	    else if (checksum==0)
	      return blockCount;

	    block = new PixyPacket();

	    for (i=0, sum=0; i<6; i++)
	    {
	      if (g_blockType==NORMAL_BLOCK && i>=5) // no angle for normal block
	      {
	        block.angle = 0;
	        break;
	      }
	      w = getWord();
	      sum += w;
	      if(i == 0)
	      {
	    	  block.Sig  = w; 
	      }
	      else if(i == 1)
	      {
	    	  block.X = w;
	      }
	      else if(i ==2)
	      {
	    	  block.Y = w;
	      }
	      else if(i == 3)
	      {
	    	  block.Width = w;
	      }
	      else if(i == 4)
	      {
	    	  block.Height = w;
	      }
	      blocks.add(block);
	    }

	    // check checksum
	    if (checksum==sum)
	      blockCount++;
	    else
	      System.out.println("checksum error!\n");

	    w = getWord();
	    if (w==PIXY_START_WORD)
	      g_blockType = NORMAL_BLOCK;
	    else if (w==PIXY_START_WORD_CC)
	      g_blockType = CC_BLOCK;
	    else
	      return blockCount;
	  }
	  return blockCount;
	}
	byte getByte()
	{
		byte buffer[] = {0};

		i2c.readOnly(buffer, 1);
		return buffer[0];
	}
	int getWord()
	{
		byte buffer[] = {0, 0};
		i2c.readOnly(buffer, 2);
		//printf("\n c: %d ",buffer[0]);
		//printf("\n w: %d",buffer[1]<<8|buffer[0]);
		return (buffer[1] << 8) | buffer[0];
	}
}
