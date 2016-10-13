package mp3player.test;

import com.mp3player.vdp.VDP;

public class TestClass {

	public static void main(String[] args) {
		VDP plattform = new VDP();
		plattform.connectToMulticastAddress("224.0.0.0");
	}

}
