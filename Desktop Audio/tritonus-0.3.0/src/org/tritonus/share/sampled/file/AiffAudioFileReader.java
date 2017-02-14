/*
 *	AiffAudioFileReader.java
 */

/*
 *  Copyright (c) 2000 by Florian Bomers <florian@bome.com>
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


package	org.tritonus.sampled.file;


import	java.io.DataInputStream;
import	java.io.File;
import	java.io.InputStream;
import	java.io.IOException;

import	javax.sound.sampled.AudioFormat;
import	javax.sound.sampled.AudioFileFormat;
import	javax.sound.sampled.AudioInputStream;
import	javax.sound.sampled.UnsupportedAudioFileException;
import	javax.sound.sampled.spi.AudioFileReader;

import	org.tritonus.sampled.file.TAudioFileFormat;
import	org.tritonus.sampled.Encodings;
import	org.tritonus.TDebug;


/**
 * Class for reading AIFF and AIFF-C files.
 *
 * @author Florian Bomers
 * @author Matthias Pfisterer
 */

public class AiffAudioFileReader extends TAudioFileReader {

	private void skipChunk(DataInputStream dataInputStream, int chunkLength, int chunkRead)
	throws IOException {
		chunkLength-=chunkRead;
		if (chunkLength>0) {
			dataInputStream.skip(chunkLength + (chunkLength % 2));
		}
	}

	private AudioFormat readCommChunk(DataInputStream dataInputStream, int chunkLength)
	throws IOException, UnsupportedAudioFileException {

		int		nNumChannels = dataInputStream.readShort();
		if (nNumChannels <= 0) {
			throw new UnsupportedAudioFileException("not an AIFF file: number of channels must be positive");
		}
		if (TDebug.TraceAudioFileReader) {
			TDebug.out("Found "+nNumChannels+" channels.");
		}
		// ignored: frame count
		dataInputStream.readInt();
		int nSampleSize = dataInputStream.readShort();
		float fSampleRate = (float) readIeeeExtended(dataInputStream);
		if (fSampleRate <= 0.0) {
			throw new UnsupportedAudioFileException("not an AIFF file: sample rate must be positive");
		}
		if (TDebug.TraceAudioFileReader) {
			TDebug.out("Found framerate "+fSampleRate);
		}
		AudioFormat.Encoding encoding = AiffTool.PCM;
		int nRead=18;
		if (chunkLength>nRead) {
			int nEncoding=dataInputStream.readInt();
			nRead+=4;
			if (nEncoding==AiffTool.AIFF_COMM_PCM) {
				// PCM - nothing to do
			}
			else
				if (nEncoding==AiffTool.AIFF_COMM_ULAW) {
					// ULAW
					encoding=AiffTool.ULAW;
					nSampleSize=8;
				} else {
					throw new UnsupportedAudioFileException(
					    "Encoding 0x"+Integer.toHexString(nEncoding)+" of AIFF file not supported");
				}
		}
		skipChunk(dataInputStream, chunkLength, nRead);
		AudioFormat format = new AudioFormat(encoding,
		                                     fSampleRate,
		                                     nSampleSize,
		                                     nNumChannels,
		                                     (nSampleSize * nNumChannels) / 8,
		                                     fSampleRate,
		                                     true);
		return format;
	}

	private void readVerChunk(DataInputStream dataInputStream, int chunkLength)
	throws IOException, UnsupportedAudioFileException {
		if (chunkLength<4) {
			throw new UnsupportedAudioFileException("Corrput AIFF file: FVER chunk too small.");
		}
		int nVer=dataInputStream.readInt();
		if (nVer!=AiffTool.AIFF_FVER_TIME_STAMP) {
			throw new UnsupportedAudioFileException("Unsupported AIFF file: version not known.");
		}
		skipChunk(dataInputStream, chunkLength, 4);
	}


	public AudioFileFormat getAudioFileFormat(InputStream inputStream)
	throws	UnsupportedAudioFileException, IOException {
		DataInputStream	dataInputStream = new DataInputStream(inputStream);
		int	nMagic = dataInputStream.readInt();
		if (nMagic != AiffTool.AIFF_FORM_MAGIC) {
			throw new UnsupportedAudioFileException("not an AIFF file: header magic is not FORM");
		}
		int nTotalLength = dataInputStream.readInt();
		nMagic = dataInputStream.readInt();
		boolean	bIsAifc;
		if (nMagic == AiffTool.AIFF_AIFF_MAGIC) {
			bIsAifc = false;
		} else if (nMagic == AiffTool.AIFF_AIFC_MAGIC) {
			bIsAifc = true;
		} else {
			throw new UnsupportedAudioFileException("unsupported IFF file: header magic neither AIFF nor AIFC");
		}
		boolean bFVerFound=!bIsAifc;
		boolean bCommFound=false;
		boolean bSSndFound=false;
		AudioFormat format=null;
		int nDataChunkLength=0;

		// walk through the chunks
		// chunks may be in any order. However, in this implementation, SSND must be last
		while (!bFVerFound || !bCommFound || !bSSndFound) {
			nMagic = dataInputStream.readInt();
			int nChunkLength = dataInputStream.readInt();
			switch (nMagic) {
			case AiffTool.AIFF_COMM_MAGIC:
				format=readCommChunk(dataInputStream, nChunkLength);
				if (TDebug.TraceAudioFileReader) {
					TDebug.out("Read COMM chunk with length "+nChunkLength);
				}
				bCommFound=true;
				break;
			case AiffTool.AIFF_FVER_MAGIC:
				if (!bFVerFound) {
					readVerChunk(dataInputStream, nChunkLength);
					if (TDebug.TraceAudioFileReader) {
						TDebug.out("Read FVER chunk with length "+nChunkLength);
					}
					bFVerFound=true;
				} else {
					skipChunk(dataInputStream, nChunkLength, 0);
				}
				break;
			case AiffTool.AIFF_SSND_MAGIC:
				if (!bCommFound || !bFVerFound) {
					throw new UnsupportedAudioFileException(
					    "cannot handle AIFF file: SSND not last chunk");
				}
				bSSndFound=true;
				nDataChunkLength=nChunkLength-8;
				// 8 information bytes of no interest
				dataInputStream.skip(8);
				if (TDebug.TraceAudioFileReader) {
					TDebug.out("Found SSND chunk with length "+nChunkLength);
				}
				break;
			default:
				if (TDebug.TraceAudioFileReader) {
					TDebug.out("Skipping unknown chunk: "+Integer.toHexString(nMagic));
				}
				skipChunk(dataInputStream, nChunkLength, 0);
				break;
			}
		}

		// TODO: length argument has to be in frames
		return new TAudioFileFormat(bIsAifc ? AiffTool.AIFC : AiffTool.AIFF,
		                            format,
		                            nDataChunkLength / format.getFrameSize(), nTotalLength + 8);
	}

}

/*** AiffAudioFileReader.java ***/
