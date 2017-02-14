/*
 *	TAsynchronousFilteredAudioInputStream.java
 */

/*
 *  Copyright (c) 1999, 2000 by Matthias Pfisterer <Matthias.Pfisterer@gmx.de>
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


package	org.tritonus.sampled.convert;


import	java.io.ByteArrayInputStream;
import	java.io.IOException;

import	javax.sound.sampled.AudioFormat;
import	javax.sound.sampled.AudioInputStream;

import	org.tritonus.TDebug;
import	org.tritonus.util.TCircularBuffer;


/**
 * Base class for asynchronus converters.
 *
 * @author Matthias Pfisterer
 */

public abstract class TAsynchronousFilteredAudioInputStream
	extends		AudioInputStream
	implements	TCircularBuffer.Trigger
{
	private static int		DEFAULT_BUFFER_SIZE = 327670;
	private static final byte[]	EMPTY_BYTE_ARRAY = new byte[0];

	// ausnahmsweise ;-)
	protected TCircularBuffer	m_circularBuffer;
	private byte[]			m_abSingleByte;



	public TAsynchronousFilteredAudioInputStream(AudioFormat outputFormat, long lLength)
	{
		this(outputFormat, lLength, DEFAULT_BUFFER_SIZE);
	}



	public TAsynchronousFilteredAudioInputStream(AudioFormat outputFormat, long lLength, int nBufferSize)
	{
		/*	The usage of a ByteArrayInputStream is a hack.
		 *	(the infamous "JavaOne hack", because I did it on june
		 *	6th 2000 in San Francisco, only hours before a
		 *	JavaOne session where I wanted to show mp3 playback
		 *	with Java Sound.) It is necessary because in the FCS
		 *	version of the Sun jdk1.3, the constructor of
		 *	AudioInputStream throws an exception if its first
		 *	argument is null. So we have to pass a dummy non-null
		 *	value.
		 */
		super(new ByteArrayInputStream(EMPTY_BYTE_ARRAY),
		      outputFormat,
		      lLength);
		m_circularBuffer = new TCircularBuffer(
			nBufferSize,
			false,	// blocking read
			true,	// blocking write
			this);	// trigger
	}



	public int read()
		throws	IOException
	{
		if (m_abSingleByte == null)
		{
			m_abSingleByte = new byte[1];
		}
		int	nReturn = read(m_abSingleByte);
		if (nReturn == -1)
		{
			return -1;
		}
		else
		{
			return m_abSingleByte[0];
		}
	}



	public int read(byte[] abData)
		throws	IOException
	{
		return m_circularBuffer.read(abData);
	}



	public int read(byte[] abData, int nOffset, int nLength)
		throws	IOException
	{
		return m_circularBuffer.read(abData, nOffset, nLength);
	}



	public long skip(long lSkip)
		throws	IOException
	{
		// TODO: this is quite inefficient
		for (long lSkipped = 0; lSkipped < lSkip; lSkipped++)
		{
			int	nReturn = read();
			if (nReturn == -1)
			{
				return lSkipped;
			}
		}
		return lSkip;
	}



	public int available()
		throws	IOException
	{
		return m_circularBuffer.availableRead();
	}



	public void close()
		throws	IOException
	{
		m_circularBuffer.close();
	}



	public boolean markSupported()
	{
		return false;
	}



	public void mark(int nReadLimit)
	{
	}



	public void reset()
		throws	IOException
	{
		throw new IOException("mark not supported");
	}
}



/*** TAsynchronousFilteredAudioInputStream.java ***/
