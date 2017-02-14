/*
 *	AlsaSynthesizer.java
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


package	org.tritonus.midi.device.alsa;


import	java.util.ArrayList;
import	java.util.Iterator;
import	java.util.List;

import	javax.sound.midi.MidiChannel;
import	javax.sound.midi.MidiDevice;
import	javax.sound.midi.MidiEvent;
import	javax.sound.midi.MidiMessage;
import	javax.sound.midi.MidiUnavailableException;
import	javax.sound.midi.Receiver;
import	javax.sound.midi.Transmitter;
import	javax.sound.midi.Synthesizer;
import	javax.sound.midi.Instrument;
import	javax.sound.midi.Soundbank;
import	javax.sound.midi.VoiceStatus;
import	javax.sound.midi.Patch;

import	org.tritonus.TDebug;
import	org.tritonus.lowlevel.alsa.ASequencer;
import	org.tritonus.midi.device.TMidiDeviceInfo;
import	org.tritonus.util.GlobalInfo;




public class AlsaSynthesizer
	extends		AlsaMidiDevice
	implements	Synthesizer
{
	private static final MidiChannel[]	EMPTY_MIDICHANNEL_ARRAY = new MidiChannel[0];

	private List		m_channels;



	public AlsaSynthesizer(int nClient, int nPort)
	{
		super(
			new TMidiDeviceInfo(
				"ALSA Synthesizer (" + nClient + ":" + nPort + ")",
				GlobalInfo.getVendor(),
				"Synthesizer based on the ALSA sequencer",
				GlobalInfo.getVersion()),
			nClient, nPort, false, true);
		m_channels = new ArrayList();
	}



	protected void openImpl()
	{
		super.openImpl();
		// TDebug.out("AlsaSynthesizer.openImpl(): called");
		// necessary? thread-safe?
		m_channels.clear();
		Receiver	receiver = null;
		try
		{
			receiver = this.getReceiver();
		}
		catch (MidiUnavailableException e)
		{
		}
		for (int i = 0; i < 16; i++)
		{
			MidiChannel	channel = new AlsaMidiChannel(
				receiver, i);
			m_channels.add(channel);
		}
	}



	protected void closeImpl()
	{
		super.closeImpl();
	}



	public int getMaxPolyphony()
	{
		return 62;
	}



	public long getLatency()
	{
		return -1L;
	}



	public MidiChannel[] getChannels()
	{
		return (MidiChannel[]) m_channels.toArray(EMPTY_MIDICHANNEL_ARRAY);
	}




	public VoiceStatus[] getVoiceStatus()
	{
		return null;
	}



	public boolean isSoundbankSupported(Soundbank soundbank)
	{
		return false;
	}



	public boolean loadInstrument(Instrument instrument)
	{
		return false;
	}



	public void unloadInstrument(Instrument instrument)
	{
	}



	public boolean remapInstrument(Instrument from, Instrument to)
	{
		return false;
	}



	public Soundbank getDefaultSoundbank()
	{
		return null;
	}



	public Instrument[] getAvailableInstruments()
	{
		return null;
	}



	public Instrument[] getLoadedInstruments()
	{
		return null;
	}



	public boolean loadAllInstruments(Soundbank soundbank)
	{
		return false;
	}



	public void unloadAllInstruments(Soundbank soundbank)
	{
	}




	public boolean loadInstruments(Soundbank soundbank, Patch[] aPatches)
	{
		return false;
	}



	public void unloadInstruments(Soundbank soundbank, Patch[] aPatches)
	{
	}



}



/*** AlsaSynthesizer.java ***/

