package se.daniel.robot.lcd;

import se.daniel.robot.lcd.rpi.pcd8544;
import se.daniel.robot.lcd.rpi.Ili9341;
import se.daniel.robot.lcd.simulator.Window;

public class LCDFactory {

	public static AbstractLcd createLcd()  {
		
		
		try {
			return new Ili9341();
		} catch (java.lang.UnsatisfiedLinkError e) {
			e.printStackTrace();
			return new Window();
		} catch (Exception e) {
			e.printStackTrace();
			return new Window();
		}
		
	}

}
