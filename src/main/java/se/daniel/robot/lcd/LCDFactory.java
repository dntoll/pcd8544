package se.daniel.robot.lcd;

import se.daniel.robot.lcd.rpi.RPILcd;
import se.daniel.robot.lcd.simulator.Window;

public class LCDFactory {

	public static AbstractLcd createLcd()  {
		
		
		try {
			return new RPILcd();
		} catch (java.lang.UnsatisfiedLinkError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Window();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Window();
		}
		
	}

}
