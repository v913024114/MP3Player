package mp3player.machine;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public abstract class LocalMachine {

	public static LocalMachine getLocalMachine() {
		String os = System.getProperty("os.name");
		if(os.toLowerCase().contains("windows")) {
			return new WindowsMachine();
		}
		return null;
	}
	
	
	public abstract boolean enterStandby();
	
	public abstract boolean turnOffMonitors();
	
	
	
	private static class WindowsMachine extends LocalMachine
	{
		@Override
		public boolean enterStandby()
		{
			String windir = System.getenv("windir");
			String command = windir+"/System32/rundll32.exe powrprof.dll,SetSuspendState";
			try {
				Runtime.getRuntime().exec(command);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public boolean turnOffMonitors() {
			File exe = new File("Turn Off Monitor.exe");
			if(!exe.exists()) {
				try {
					Files.copy(getClass().getResourceAsStream("Turn Off Monitor.exe"), exe.toPath());
				} catch (IOException e) {
					return false;
				}
			}
			try {
				Runtime.getRuntime().exec(exe.getAbsolutePath());
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	
}
