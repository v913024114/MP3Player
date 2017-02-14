package test.mediakey;

import java.io.File;

import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;

public class TestHotkey {

	public static void main(String[] args) {
		// Check if running on Windows
		String osName = System.getProperty("os.name");
		if(!osName.toLowerCase().contains("windows")){
			System.err.println("Only supported on windows");
			return;
		}
		
		// Check if 32 or 64 bit and load dll
		String bitnessJVM = System.getProperty("sun.arch.data.model");
		File dllFile;
		if(bitnessJVM.equals("64")) {
			dllFile = new File("JIntellitype64.dll");
		}else {
			dllFile = new File("JIntellitype.dll");
		}
		JIntellitype.setLibraryLocation(dllFile);
		
		JIntellitype.getInstance().addIntellitypeListener(new IntellitypeListener() {
			
			@Override
			public void onIntellitype(int aCommand) {
				if(aCommand == JIntellitype.APPCOMMAND_MEDIA_PLAY_PAUSE) {
			           System.out.println("Play/Pause");
				}
				else if(aCommand == JIntellitype.APPCOMMAND_MEDIA_STOP) {
					System.out.println("Stop");
				}
				else if(aCommand == JIntellitype.APPCOMMAND_MEDIA_NEXTTRACK) {
					System.out.println("Next");
				}
				else if(aCommand == JIntellitype.APPCOMMAND_MEDIA_PREVIOUSTRACK) {
					System.out.println("Previous");
				}
				else if(aCommand == JIntellitype.APPCOMMAND_VOLUME_DOWN) {
					System.out.println("Volume down");
				}
				else if(aCommand == JIntellitype.APPCOMMAND_VOLUME_UP) {
					System.out.println("Volume up");
				}
				else if(aCommand == JIntellitype.APPCOMMAND_VOLUME_MUTE) {
					System.out.println("Volume mute");
				}
				else {
					System.out.println("Other");
				}
			}
		});
		System.out.println("Listening...");
		
		
		
//		JIntellitype.getInstance().cleanUp();
//		System.exit(0);
	}
}
