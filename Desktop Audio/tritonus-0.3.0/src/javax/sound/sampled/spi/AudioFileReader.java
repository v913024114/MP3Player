/*
 *	AudioFileReader.java
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


package	javax.sound.sampled.spi;


import	java.io.File;
import	java.io.InputStream;
import	java.io.IOException;

import	java.net.URL;

import	javax.sound.sampled.AudioFileFormat;
import	javax.sound.sampled.AudioInputStream;
import	javax.sound.sampled.UnsupportedAudioFileException;



public abstract class AudioFileReader
{
	public abstract AudioFileFormat getAudioFileFormat(InputStream inputStream)
		throws	UnsupportedAudioFileException, IOException;



	public abstract AudioFileFormat getAudioFileFormat(URL url)
		throws	UnsupportedAudioFileException, IOException;



	public abstract AudioFileFormat getAudioFileFormat(File file)
		throws	UnsupportedAudioFileException, IOException;



	public abstract AudioInputStream getAudioInputStream(InputStream inputStream)
		throws	UnsupportedAudioFileException, IOException;



	public abstract AudioInputStream getAudioInputStream(URL url)
		throws	UnsupportedAudioFileException, IOException;



	public abstract AudioInputStream getAudioInputStream(File file)
		throws	UnsupportedAudioFileException, IOException;
}



/*** AudioFileReader.java ***/
