/*
 *	AudioFormats.java
 */

/*
 *  Copyright (c) 1999,2000 by Matthias Pfisterer <Matthias.Pfisterer@gmx.de>
 *  Copyright (c) 1999 by Florian Bomers <florian@bome.com>
 *
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */


package	org.tritonus.sampled;

import	javax.sound.sampled.AudioFormat;
import	javax.sound.sampled.AudioSystem;



public class AudioFormats
{
	//$$fb 19 Dec 99: added
	//$$fb ohh, I just read the documentation - it must indeed match exactly.
	//I think it would be much better that a pair of values also matches
	//when at least one is NOT_SPECIFIED.
	// support for NOT_SPECIFIED should be consistent in JavaSound...
	// As a "workaround" I implemented it like this in TFormatConversionProvider
	private static boolean doMatch(int i1, int i2)
	{
		return i1 == AudioSystem.NOT_SPECIFIED
			|| i2 == AudioSystem.NOT_SPECIFIED
			|| i1 == i2;
	}



	private static boolean doMatch(float f1, float f2)
	{
		return f1 == AudioSystem.NOT_SPECIFIED
			|| f2 == AudioSystem.NOT_SPECIFIED
			|| Math.abs(f1 - f2) < 1.0e-9;
	}



	public static boolean matches(AudioFormat format1,
				      AudioFormat format2)
	{
		//$$fb 19 Dec 99: endian must be checked, too.
		//
		// we do have a problem with redundant elements:
		// e.g. 
		// encoding=ALAW || ULAW -> bigEndian and samplesizeinbits don't matter
		// sample size in bits == 8 -> bigEndian doesn't matter
		// sample size in bits > 8 -> PCM is always signed. 
		// This is an overall issue in JavaSound, I think.
		// At present, it is not consistently implemented to support these 
		// redundancies and implicit definitions
		//
		// As a workaround of this issue I return in the converters
		// all combinations, e.g. for ULAW I return bigEndian and !bigEndian formats.
/* old version
   return getEncoding().equals(format.getEncoding())
   && getChannels() == format.getChannels()
   && getSampleSizeInBits() == format.getSampleSizeInBits()
   && getFrameSize() == format.getFrameSize()
   && (Math.abs(getSampleRate() - format.getSampleRate()) < 1.0e-9 || format.getSampleRate() == AudioSystem.NOT_SPECIFIED)
   && (Math.abs(getFrameRate() - format.getFrameRate()) < 1.0e-9 || format.getFrameRate() == AudioSystem.NOT_SPECIFIED);
*/
		// as proposed by florian
		return format1.getEncoding().equals(format2.getEncoding())
			&& (format2.getSampleSizeInBits()<=8 || format2.getSampleSizeInBits()==AudioSystem.NOT_SPECIFIED || format1.isBigEndian()==format2.isBigEndian())
			&& doMatch(format1.getChannels(),format2.getChannels())
			&& doMatch(format1.getSampleSizeInBits(), format2.getSampleSizeInBits())
			&& doMatch(format1.getFrameSize(), format2.getFrameSize())
			&& doMatch(format1.getSampleRate(), format2.getSampleRate())
			&& doMatch(format1.getFrameRate(),format2.getFrameRate());
	}


}



/*** AudioFormats.java ***/
