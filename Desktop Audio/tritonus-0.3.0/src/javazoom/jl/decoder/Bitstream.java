/*
 * 12/12/99	 Based on Ibitstream. Exceptions thrown on errors, 
 *			 Tempoarily removed seek functionality. mdm@techie.com
 *
 * 02/12/99 : Java Conversion by E.B , ebsp@iname.com , JavaLayer
 *
 *----------------------------------------------------------------------
 *  @(#) ibitstream.h 1.5, last edit: 6/15/94 16:55:34
 *  @(#) Copyright (C) 1993, 1994 Tobias Bading (bading@cs.tu-berlin.de)
 *  @(#) Berlin University of Technology
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  Changes made by Jeff Tsay :
 *  04/14/97 : Added function prototypes for new syncing and seeking
 *  mechanisms. Also made this file portable.
 *-----------------------------------------------------------------------
 */
 
package javazoom.jl.decoder;

import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;

/** 
 * The <code>Bistream</code> class is responsible for parsing
 * an MPEG audio bitstream. 
 * 
 * <b>REVIEW:</b> much of the parsing currently occurs in the 
 * various decoders. This should be moved into this class and associated
 * inner classes.
 */
public final class Bitstream implements BitstreamErrors
{	
	
	/**
	 * Syncrhronization control constant for the initial
	 * synchronization to the start of a frame.
	 */
	static byte		INITIAL_SYNC = 0;

	/**
	 * Syncrhronization control constant for non-iniital frame
	 * synchronizations. 
	 */
	static byte		STRICT_SYNC = 1;
  
	// max. 1730 bytes per frame: 144 * 384kbit/s / 32000 Hz + 2 Bytes CRC  
	/**
	 * Maximum size of the frame buffer.
	 */
	private static final int	BUFFER_INT_SIZE = 433;
  
		
	/**
	 * The frame buffer that holds the data for the current frame.
	 */
	private final int[]		framebuffer = new int[BUFFER_INT_SIZE];
	
	/**
	 * Number of valid bytes in the frame buffer.
	 */
	private int				framesize;		
	
	/**
	 * Index into <code>framebuffer</code> where the next bits are
	 * retrieved. 
	 */
	private int				wordpointer;	
		
	/**
	 * Number (0-31, from MSB to LSB) of next bit for get_bits()
	 */
	private int				bitindex;		
	
	/**
	 * The current specified syncword
	 */
	private int				syncword;
	
	/**
	 * 
	 */
	private boolean			single_ch_mode;
  //private int 			current_frame_number;
  //private int				last_frame_number;
  	
	private final int		bitmask[] = {0,	// dummy
	 0x00000001, 0x00000003, 0x00000007, 0x0000000F,
	 0x0000001F, 0x0000003F, 0x0000007F, 0x000000FF,
	 0x000001FF, 0x000003FF, 0x000007FF, 0x00000FFF,
	 0x00001FFF, 0x00003FFF, 0x00007FFF, 0x0000FFFF,
     0x0001FFFF };	
	
	private final InputStream		source;		
	
	private final Header			header = new Header();
	
	private final byte				syncbuf[] = new byte[4];
	
	private Crc16[]					crc = new Crc16[1];
	
	/**
	 * Construct a IBitstream that reads data from a
	 * given InputStream.
	 * 
	 * @param in	The InputStream to read from.
	 */
	public Bitstream(InputStream in)
	{		
		if (in==null)
			throw new NullPointerException("in");
		
		source = in;	
		
		closeFrame();
		//current_frame_number = -1;
		//last_frame_number = -1;	    
	}  
  	
	/**
	 * Reads and parses the next frame from the input source.
	 * @return the Header describing details of the frame read,
	 *	or null if the end of the stream has been reached.
	 */
	public Header readFrame() throws BitstreamException
	{
		Header result = null;
		try
		{
			result = readNextFrame();
		}
		catch (BitstreamException ex)
		{			
			if (ex.getErrorCode()!=STREAM_EOF)
			{
				// wrap original exception so stack trace is maintained.
				throw newBitstreamException(ex.getErrorCode(), ex);				
			}
		}
		return result;
	}
  
	private Header readNextFrame() throws BitstreamException		
	{
		if (framesize == -1)
		{			
			nextFrame();
		}
		
		return header;
	}
	
	
	/**
	 * 
	 */
	private void nextFrame() throws BitstreamException
	{
		// entire frame is read by the header class. 
		header.read_header(this, crc);						   		
	}
	
	public void closeFrame()
	{
		framesize = -1;
		wordpointer = -1;
		bitindex = -1;		
	}

	
	// REVIEW: this class should provide inner classes to
	// parse the frame contents. Eventually, readBits will
	// be removed. 
	public int readBits(int n)
	{
		return get_bits(n);
	}
	
	public int readCheckedBits(int n)
	{
		// REVIEW: implement CRC check.
		return get_bits(n);
	}

	protected BitstreamException newBitstreamException(int errorcode)
	{
		return new BitstreamException(errorcode, null);
	}
	protected BitstreamException newBitstreamException(int errorcode, Throwable throwable)
	{
		return new BitstreamException(errorcode, throwable);
	}

	
  /**
   * Get next 32 bits from bitstream.
   * They are stored in the headerstring.
   * syncmod allows Synchro flag ID
   * The returned value is False at the end of stream.
   */
  		
	int syncHeader(byte syncmode) throws BitstreamException
	{
		boolean sync;
		int headerstring;
				
		// test for EOF
		int read;
		try
		{
			read = source.read();
			if (read==-1)
			{
				// System.out.println("hallo1");
				throw newBitstreamException(STREAM_EOF, null);	
			}
		}
		catch (IOException ex)
		{
			// System.out.println("hallo2-");
			throw newBitstreamException(STREAM_ERROR, ex);
		}
		
		syncbuf[0] = (byte)read;
		
		// read additinal 2 bytes
		readFully(syncbuf, 1, 2);
		
		headerstring = ((syncbuf[0] << 16) & 0x00FF0000) | ((syncbuf[1] << 8) & 0x0000FF00) | ((syncbuf[2] << 0) & 0x000000FF);
		
		do
		{						
			headerstring <<= 8;
			
			readFully(syncbuf, 3, 1);
			headerstring |= (syncbuf[3] & 0x000000FF);
			
			if (syncmode == INITIAL_SYNC)
			{
				sync =  ((headerstring & 0xFFF00000) == 0xFFF00000);
			}
			else
			{
				sync =  ((headerstring & 0xFFF80C00) == syncword) &&
				    (((headerstring & 0x000000C0) == 0x000000C0) == single_ch_mode);
			}		   						
		} 
		// System.out.println("hallo3");
		while (!sync);
			
		//current_frame_number++;
		//if (last_frame_number < current_frame_number) last_frame_number = current_frame_number;		
		
		return headerstring;
	}

  /**
   * Fill buffer with data from bitstream.
   * Read bytesize bytes from the file.
   * The returned value is False at the end of stream.
   */
  void read_frame(int bytesize) throws BitstreamException 
  {
 	int		numread = 0;
    // read bytesize bytes from the file, placing the number of bytes
    // actually read in numread and setting result to true if
    // successful
  	boolean result = false;
	
	// REVIEW: optimize - reuse temporary buffer	
	byte[]	byteread = new byte[bytesize];
	readFully(byteread,0,bytesize);		
	
	// Convert Bytes read to int		
	int	b=0;
	
	for (int k=0;k<bytesize;k=k+4)
	{
		int convert = 0;
		byte b0 = 0;
		byte b1 = 0;
		byte b2 = 0;
		byte b3 = 0;
		b0 = byteread[k];
		if (k+1<bytesize) b1 = byteread[k+1];
		if (k+2<bytesize) b2 = byteread[k+2];
		if (k+3<bytesize) b3 = byteread[k+3];
		framebuffer[b++] = ((b0 << 24) &0xFF000000) | ((b1 << 16) & 0x00FF0000) | ((b2 << 8) & 0x0000FF00) | (b3 & 0x000000FF);
	}
	
	wordpointer = 0;
    bitindex = 0;
    framesize = bytesize;
	
  }
  
  /**
   * Read bits from buffer into the lower bits of an unsigned int. 
   * The LSB contains the latest read bit of the stream.
   * (1 <= number_of_bits <= 16)
   */  
  public int get_bits(int number_of_bits)
  {
  	
  	int				returnvalue = 0;
  	int 			sum = bitindex + number_of_bits;

  	if (sum <= 32)
  	{
	   // all bits contained in *wordpointer
	   returnvalue = (framebuffer[wordpointer] >>> (32 - sum)) & bitmask[number_of_bits];
	   // returnvalue = (wordpointer[0] >> (32 - sum)) & bitmask[number_of_bits];
	   if ((bitindex += number_of_bits) == 32)
	   {
		 bitindex = 0;
		 wordpointer++; // added by me!
	   }
	   return returnvalue;
    }

    // Magouille a Voir
    //((short[])&returnvalue)[0] = ((short[])wordpointer + 1)[0];
    //wordpointer++; // Added by me!
    //((short[])&returnvalue + 1)[0] = ((short[])wordpointer)[0];
	int Right = (framebuffer[wordpointer] & 0x0000FFFF);
	wordpointer++;
	int Left = (framebuffer[wordpointer] & 0xFFFF0000);
	returnvalue = ((Right << 16) & 0xFFFF0000) | ((Left >>> 16)& 0x0000FFFF);
	
    returnvalue >>>= 48 - sum;	// returnvalue >>= 16 - (number_of_bits - (32 - bitindex))
    returnvalue &= bitmask[number_of_bits];
    bitindex = sum - 32;
    return returnvalue;
}

	/**
	 * Set the word we want to sync the header to.
	 * In Big-Endian byte order
	 */
	void set_syncword(int syncword0)
	{
		syncword = syncword0 & 0xFFFFFF3F;
		single_ch_mode = ((syncword0 & 0x000000C0) == 0x000000C0);
	}
	/**
	 * Reads the exact number of bytes from the source
	 * input stream into a byte array.
	 * 
	 * @param b		The byte array to read the specified number
	 *				of bytes into.
	 * @param offs	The index in the array where the first byte
	 *				read should be stored.
	 * @param len	the number of bytes to read.
	 * 
	 * @exception BitstreamException is thrown if the specified
	 *		number of bytes could not be read from the stream.
	 */
	private void readFully(byte[] b, int offs, int len)
		throws BitstreamException
	{
		try
		{
			while (len > 0)
			{
				int bytesread = source.read(b, offs, len);
				if (bytesread == -1)
				{
					// System.out.println("hallo4");
					// inserted MP19991217
					throw newBitstreamException(STREAM_EOF, null);
					// end insertion
					// commented out MP19991217
					// break;
					// end commented out
					//throw newBitstreamException(UNEXPECTED_EOF, new EOFException());
				}
				
				offs += bytesread;
				len -= bytesread;
			}			
		}
		catch (IOException ex)
		{
			// System.out.println("hallo5");
			throw newBitstreamException(STREAM_ERROR, ex);
		}
	}
  
}
