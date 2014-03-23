package se.daniel.robot.lcd;

import se.daniel.robot.lcd.simulator.Window;

public class LCDFactory {

	public static AbstractLcd createLcd() {
		return new Window();
	}

}
