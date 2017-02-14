/*
 *	TCircularBuffer.java
 */

/*
 *  Copyright (c) 1999 by Matthias Pfisterer <Matthias.Pfisterer@gmx.de>
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


package	org.tritonus.util;


import	org.tritonus.TDebug;



public class TCircularBuffer
{
	private boolean		m_bBlockingRead;
	private boolean		m_bBlockingWrite;
	private byte[]		m_abData;
	private int		m_nSize;
	private int		m_nReadPos;
	private int		m_nWritePos;
	private Trigger		m_trigger;
	private boolean		m_bOpen;



	public TCircularBuffer(int nSize, boolean bBlockingRead, boolean bBlockingWrite, Trigger trigger)
	{
		m_bBlockingRead = bBlockingRead;
		m_bBlockingWrite = bBlockingWrite;
		m_nSize = nSize;
		m_abData = new byte[m_nSize];
		m_nReadPos = 0;
		m_nWritePos = 0;
		m_trigger = trigger;
		m_bOpen = true;
	}



	public void close()
	{
		m_bOpen = false;
	}



	public int availableRead()
	{
		// return (m_nWritePos - m_nReadPos) % m_nSize;
		return m_nWritePos - m_nReadPos;
	}



	public int availableWrite()
	{
		return m_nSize - availableRead();
	}



	private int getReadPos()
	{
		return m_nReadPos % m_nSize;
	}



	private int getWritePos()
	{
		return m_nWritePos % m_nSize;
	}



	public int read(byte[] abData)
	{
		return read(abData, 0, abData.length);
	}



	public int read(byte[] abData, int nOffset, int nLength)
	{
		if (TDebug.TraceCircularBuffer)
		{
			TDebug.out("TCircularBuffer.read(): called.");
		}
		// TODO: shoudl be inside synchronized block?
		if (!m_bOpen)
		{
			return -1;
		}
		synchronized (this)
		{
			if (!m_bBlockingRead)
			{
				if (m_trigger != null && availableRead() < nLength)
				{
					if (TDebug.TraceCircularBuffer)
					{
						TDebug.out("TCircularBuffer.read(): executing trigger.");
					}
					m_trigger.execute();
				}
				nLength = Math.min(availableRead(), nLength);
			}
			int nRemainingBytes = nLength;
			while (nRemainingBytes > 0)
			{
				while (availableRead() == 0)
				{
					try
					{
						wait();
					}
					catch (InterruptedException e)
					{
					}
				}
				int	nAvailable = Math.min(availableRead(), nRemainingBytes);
				while (nAvailable > 0)
				{
					int	nToRead = Math.min(nAvailable, m_nSize - getReadPos());
					System.arraycopy(m_abData, getReadPos(), abData, nOffset, nToRead);
					m_nReadPos += nToRead;
					nOffset += nToRead;
					nAvailable -= nToRead;
					nRemainingBytes -= nToRead;
				}
				notifyAll();
			}
			if (TDebug.TraceCircularBuffer)
			{
				TDebug.out("TCircularBuffer.read(): completed.");
			}
			return nLength;
		}
	}


	public int write(byte[] abData)
	{
		return write(abData, 0, abData.length);
	}



	public int write(byte[] abData, int nOffset, int nLength)
	{
		if (TDebug.TraceCircularBuffer)
		{
			TDebug.out("TCircularBuffer.write(): called; nLength: " + nLength);
		}
		synchronized (this)
		{
			if (TDebug.TraceCircularBuffer)
			{
				TDebug.out("TCircularBuffer.write(): entered synchronized block.");
			}
			if (!m_bBlockingWrite)
			{
				nLength = Math.min(availableWrite(), nLength);
			}
			int nRemainingBytes = nLength;
			while (nRemainingBytes > 0)
			{
				while (availableWrite() == 0)
				{
					try
					{
						wait();
					}
					catch (InterruptedException e)
					{
					}
				}
				int	nAvailable = Math.min(availableWrite(), nRemainingBytes);
				while (nAvailable > 0)
				{
					int	nToWrite = Math.min(nAvailable, m_nSize - getWritePos());
				//TDebug.out("src buf size= " + abData.length + ", offset = " + nOffset + ", dst buf size=" + m_abData.length + " write pos=" + getWritePos() + " len=" + nToWrite);
					System.arraycopy(abData, nOffset, m_abData, getWritePos(), nToWrite);
					m_nWritePos += nToWrite;
					nOffset += nToWrite;
					nAvailable -= nToWrite;
					nRemainingBytes -= nToWrite;
				}
				notifyAll();
			}
			return nLength;
		}
	}



	public static interface Trigger
	{
		public void execute();
	}


}



/*** TCircularBuffer.java ***/

