/*
 * 02/13/99 : Java Conversion by E.B , ebsp@iname.com
 *
 *---------------------------------------------------------------------------
 * Declarations for MPEG header class
 * A few layer III, MPEG-2 LSF, and seeking modifications made by Jeff Tsay.
 * Last modified : 04/19/97 
 *
 *  @(#) header.h 1.7, last edit: 6/15/94 16:55:33
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
 *--------------------------------------------------------------------------
 */
package javazoom.jl.decoder;

/**
 * Class for extracting information from a frame header.
 * 
 *  
 */
// TODO: move strings into resources

public final class Header
{
	public  static final int[][]	frequencies = 
						{{22050, 24000, 16000, 1},
						{44100, 48000, 32000, 1}};
		  
	/**
	 * Constant for MPEG-2 LSF version 
	 */
	public static final int		MPEG2_LSF = 0;
		
	/**
	 * Constant for MPEG-1 version
	 */
	public static final int		MPEG1 = 1;
		
	public static final int		STEREO = 0;
	public static final int		JOINT_STEREO = 1;
	public static final int		DUAL_CHANNEL = 2;
	public static final int		SINGLE_CHANNEL = 3;
	public static final int		FOURTYFOUR_POINT_ONE = 0;
	public static final int		FOURTYEIGHT=1;
	public static final int		THIRTYTWO=2;

	private int				h_layer, h_protection_bit, h_bitrate_index,
	  						h_padding_bit, h_mode_extension;
	private int				h_version;
	private int				h_mode;
	private int				h_sample_frequency;
	private int				h_number_of_subbands, h_intensity_stereo_bound;
	private boolean			h_copyright, h_original;
	private byte			syncmode = Bitstream.INITIAL_SYNC;
	private Crc16			crc;
		  		
	public short			checksum;
	public int				framesize;
	public int				nSlots;

		  
	Header()
	{ 	
	}
	public String toString()
	{
		StringBuffer buffer = new StringBuffer(200);
		buffer.append("Layer ");
		buffer.append(layer_string());
		buffer.append(" frame ");
		buffer.append(mode_string());
		buffer.append(' ');
		buffer.append(version_string());
		if (!checksums())
			buffer.append(" no");
		buffer.append(" checksums");
		buffer.append(' ');
		buffer.append(sample_frequency_string());
		buffer.append(',');
		buffer.append(' ');
		buffer.append(bitrate_string());		
		
		String s =  buffer.toString();
		return s;
	}
	
	/**
	 * Read a 32-bit header from the bitstream.
	 */
	void read_header(Bitstream stream, Crc16[] crcp) 
		throws BitstreamException
	{
	  int headerstring;
	  int channel_bitrate;
		  
	  headerstring = stream.syncHeader(syncmode);
		  
	  if (syncmode==Bitstream.INITIAL_SYNC)
	  {				
			h_version = ((headerstring >>> 19) & 1);

			if ((h_sample_frequency = ((headerstring >>> 10) & 3)) == 3)
			{
				throw stream.newBitstreamException(Bitstream.UNKNOWN_ERROR);					
	 		}
			syncmode = Bitstream.STRICT_SYNC;
			stream.set_syncword(headerstring & 0xFFF80CC0);				
	  } 

		h_layer   = 4 - (headerstring >>> 17) & 3;
	  h_protection_bit = (headerstring >>> 16) & 1;
		h_bitrate_index  = (headerstring >>> 12) & 0xF;
	  h_padding_bit = (headerstring >>> 9) & 1;
		h_mode = ((headerstring >>> 6) & 3);
	  h_mode_extension = (headerstring >>> 4) & 3;
	  if (h_mode == JOINT_STEREO) h_intensity_stereo_bound = (h_mode_extension << 2) + 4;
	  else h_intensity_stereo_bound = 0;		// should never be used
	  if (((headerstring >>> 3) & 1) == 1) h_copyright = true;
	  if (((headerstring >>> 2) & 1) == 1) h_original = true;
			

	  // calculate number of subbands:
	  if (h_layer == 1) h_number_of_subbands = 32;
	  else
	  {
	    channel_bitrate = h_bitrate_index;
	    // calculate bitrate per channel:
	    if (h_mode != SINGLE_CHANNEL)
	  	if (channel_bitrate == 4) channel_bitrate = 1;
	      else channel_bitrate -= 4;

	    if ((channel_bitrate == 1) || (channel_bitrate == 2))
	  	if (h_sample_frequency == THIRTYTWO) h_number_of_subbands = 12;
	  	else h_number_of_subbands = 8;
	    else
	  	if ((h_sample_frequency == FOURTYEIGHT) || ((channel_bitrate >= 3) &&
	    													  	  (channel_bitrate <= 5)))
	  		h_number_of_subbands = 27;
	  	else
	  		h_number_of_subbands = 30;
	   }
		 if (h_intensity_stereo_bound > h_number_of_subbands) h_intensity_stereo_bound = h_number_of_subbands;
	   // calculate framesize and nSlots
	   calculate_framesize();

	  // read framedata:
	  stream.read_frame(framesize);
		  
	  if (h_protection_bit == 0)
	  {
	   // frame contains a crc checksum
	   checksum = (short) stream.get_bits(16);
	   if (crc == null)
	     crc = new Crc16();
	   crc.add_bits(headerstring, 16);
	   crcp[0] = crc;
	  }
	  else crcp[0] = null;
	  if (h_sample_frequency == FOURTYFOUR_POINT_ONE) 
	  {
	   /*
	  	if (offset == null) 
	     {
	  	  int max = max_number_of_frames(stream);
	  	  offset = new int[max];
	        for(int i=0; i<max; i++) offset[i] = 0;
	     }
	     // Bizarre, y avait ici une acollade ouvrante
	     int cf = stream.current_frame();
	     int lf = stream.last_frame();
	     if ((cf > 0) && (cf == lf))
	     {
	  	   offset[cf] = offset[cf-1] + h_padding_bit;
	     }
	     else
	     {
		       offset[0] = h_padding_bit;
	     }
	  */
	  }		
	}
		  
	// Functions to query header contents:
	/**
	 * Returns version.
	 */
	public int version() { return h_version; }

	/**
	 * Returns Layer ID.
	 */
	public int layer() { return h_layer; }
		  
	/**
	 * Returns bitrate index.
	 */
	public int bitrate_index() { return h_bitrate_index; }

	/**
	 * Returns Sample Frequency.
	 */
	public int sample_frequency() { return h_sample_frequency; }

	/**
	 * Returns Frequency.
	 */
	public int frequency() {return frequencies[h_version][h_sample_frequency];}

	/**
	 * Returns Mode.
	 */
	public int mode() { return h_mode; }

	/**
	 * Returns Protection bit.
	 */
	public boolean checksums()
	{
		if (h_protection_bit == 0) return true;
	  else return false;
	}

	/**
	 * Returns Copyright.
	 */
	public boolean copyright() { return h_copyright; }

	/**
	 * Returns Original.
	 */
	public boolean original() { return h_original; }

	/**
	 * Returns Checksum flag.
	 * Compares computed checksum with stream checksum.
	 */
	public boolean checksum_ok () { return (checksum == crc.checksum()); }

	// Seeking and layer III stuff
	/**
	 * Returns Layer III Padding bit.
	 */
	public boolean padding() 
	{
		if (h_padding_bit == 0) return false;
	  else return true;
	}

	/**
	 * Returns Slots.
	 */
	public int slots() { return nSlots; }
		  
	/**
	 * Returns Mode Extension.
	 */
	public int mode_extension() { return h_mode_extension; }
	
	
	private static final int bitrates[][][] = {
		{{0 /*free format*/, 32000, 48000, 56000, 64000, 80000, 96000,
	  112000, 128000, 144000, 160000, 176000, 192000 ,224000, 256000, 0},
	 	{0 /*free format*/, 8000, 16000, 24000, 32000, 40000, 48000,
	  56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000, 0},
	 	{0 /*free format*/, 8000, 16000, 24000, 32000, 40000, 48000,
	  56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000, 0}},
		{{0 /*free format*/, 32000, 64000, 96000, 128000, 160000, 192000,
	   224000, 256000, 288000, 320000, 352000, 384000, 416000, 448000, 0},
	  {0 /*free format*/, 32000, 48000, 56000, 64000, 80000, 96000,
	   112000, 128000, 160000, 192000, 224000, 256000, 320000, 384000, 0},
	  {0 /*free format*/, 32000, 40000, 48000, 56000, 64000, 80000,
	   96000, 112000, 128000, 160000, 192000, 224000, 256000, 320000, 0}}
		};	
	/**
	 * Calculate Frame size.
	 * Calculates framesize in bytes excluding header size.
	 */
	private int calculate_framesize()
	{
	  
	 if (h_layer == 1)
	 {
	   framesize = (12 * bitrates[h_version][0][h_bitrate_index]) /
	               frequencies[h_version][h_sample_frequency];
	   if (h_padding_bit != 0 ) framesize++;
	   framesize <<= 2;		// one slot is 4 bytes long
	   nSlots = 0;
	 }
	 else
	 {
	   framesize = (144 * bitrates[h_version][h_layer - 1][h_bitrate_index]) /
	               frequencies[h_version][h_sample_frequency];
	   if (h_version == MPEG2_LSF) framesize >>>= 1;
	   if (h_padding_bit != 0) framesize++;
	   // Layer III slots
	   if (h_layer == 3)
	   {
	     if (h_version == MPEG1)
	     {
	  		 nSlots = framesize - ((h_mode == SINGLE_CHANNEL) ? 17 : 32) // side info size
	  								  -  ((h_protection_bit!=0) ? 0 : 2) 		       // CRC size
	  								  - 4; 								             // header size
	     }
	     else
	     {  // MPEG-2 LSF
	        nSlots = framesize - ((h_mode == SINGLE_CHANNEL) ?  9 : 17) // side info size
	  					   		  -  ((h_protection_bit!=0) ? 0 : 2) 		       // CRC size
	  								  - 4; 								             // header size
	     }
	   }
	   else
	   {
	  	 nSlots = 0;
	   }
	 }
	 framesize -= 4;             // subtract header size
	 return framesize;
	}
		  

	// functions which return header informations as strings:
	/**
	 * Return Layer version.
	 */
	public String layer_string()
	{
		switch (h_layer)
		{
	   case 1:
	  	return "I";
	   case 2:
	  	return "II";
	   case 3:
	  	return "III";
		}
	  return null;
	}

	
	static private final String bitrate_str[][][] = {
		{{"free format", "32 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s",
	  "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "144 kbit/s",
	  "160 kbit/s", "176 kbit/s", "192 kbit/s", "224 kbit/s", "256 kbit/s",
	  "forbidden"},
	  {"free format", "8 kbit/s", "16 kbit/s", "24 kbit/s", "32 kbit/s",
	  "40 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s",
	  "96 kbit/s", "112 kbit/s", "128 kbit/s", "144 kbit/s", "160 kbit/s",
	  "forbidden"},
	  {"free format", "8 kbit/s", "16 kbit/s", "24 kbit/s", "32 kbit/s",
	  "40 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s", "80 kbit/s",
	  "96 kbit/s", "112 kbit/s", "128 kbit/s", "144 kbit/s", "160 kbit/s",
	  "forbidden"}},
	  {{"free format", "32 kbit/s", "64 kbit/s", "96 kbit/s", "128 kbit/s",
	  "160 kbit/s", "192 kbit/s", "224 kbit/s", "256 kbit/s", "288 kbit/s",
	  "320 kbit/s", "352 kbit/s", "384 kbit/s", "416 kbit/s", "448 kbit/s",
	  "forbidden"},
	  {"free format", "32 kbit/s", "48 kbit/s", "56 kbit/s", "64 kbit/s",
	  "80 kbit/s", "96 kbit/s", "112 kbit/s", "128 kbit/s", "160 kbit/s",
	  "192 kbit/s", "224 kbit/s", "256 kbit/s", "320 kbit/s", "384 kbit/s",
	  "forbidden"},
	  {"free format", "32 kbit/s", "40 kbit/s", "48 kbit/s", "56 kbit/s",
	  "64 kbit/s", "80 kbit/s" , "96 kbit/s", "112 kbit/s", "128 kbit/s",
	  "160 kbit/s", "192 kbit/s", "224 kbit/s", "256 kbit/s", "320 kbit/s",
	  "forbidden"}}
	  };
	
	/**
	 * Returns Bitrate.
	 */
	public String bitrate_string()
	{
	  return bitrate_str[h_version][h_layer - 1][h_bitrate_index];
	}  

	/**
	 * Returns Frequency
	 */
	public String sample_frequency_string()
	{
		switch (h_sample_frequency)
		{
	    case THIRTYTWO:
	  	if (h_version == MPEG1)
	  		return "32 kHz";
	    else
	    	return "16 kHz";
	    case FOURTYFOUR_POINT_ONE:
	  	if (h_version == MPEG1)
	  		return "44.1 kHz";
	    else
	    	return "22.05 kHz";
	    case FOURTYEIGHT:
	  	if (h_version == MPEG1)
	  		return "48 kHz";
	    else
	    	return "24 kHz";
	  }
	  return(null);
	}
		  
	/**
	 * Returns Mode.
	 */
	public String mode_string()
	{
	   switch (h_mode)
	   { 
	     case STEREO:
	  	return "Stereo";
	     case JOINT_STEREO:
	  	return "Joint stereo";
	     case DUAL_CHANNEL:
	  	return "Dual channel";
	     case SINGLE_CHANNEL:
	  	return "Single channel";
	   }
	   return null;
	}
		  
	/**
	 * Returns Version.
	 */
	public String version_string()
	{
	  switch (h_version)
	  {
	    case MPEG1:
	      return "MPEG-1";
	    case MPEG2_LSF:
	      return "MPEG-2 LSF";
	  }
	  return(null);
	}
		  
	/**
	 * Returns the number of subbands in the current frame.
	 */
	public int number_of_subbands() {return h_number_of_subbands;}

	/**
	 * Returns Intensity Stereo.
	 * Layer II joint stereo only).
	 * Returns the number of subbands which are in stereo mode,
	 * subbands above that limit are in intensity stereo mode.
	 */
	public int intensity_stereo_bound() {return h_intensity_stereo_bound;}

		  
}
