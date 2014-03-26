package se.daniel.robot.lcd.rpi;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import se.daniel.robot.lcd.Font;
import se.daniel.robot.lcd.PixelBuffer;
import se.daniel.robot.lcd.AbstractLcd;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.Spi;

/**
 * This code is translated from 
 * 
 * https://github.com/XavierBerger/pcd8544/blob/master/src/lcd.py
 * 
 *  Original code:
 *  Code picked up Raspberry Pi forums  
 *  http://www.raspberrypi.org/phpBB3/viewtopic.php?p=301522#p301522
 */
public class Lcd extends AbstractLcd {
	

	//#gpio's :
	static final byte DC   = 4;// # gpio pin 16 = wiringpi no. 4 (BCM 23)
	static final byte RST  = 5;// # gpio pin 18 = wiringpi no. 5 (BCM 24)
	static byte LED  = 1;// # gpio pin 12 = wiringpi no. 1 (BCM 18)

	//# SPI connection, these are only for documentation...
	static final byte SCE  = 10;// # gpio pin 24 = wiringpi no. 10 (CE0 BCM 8) 
	static final byte SCLK = 14;// # gpio pin 23 = wiringpi no. 14 (SCLK BCM 11)
	static final byte DIN  = 12;// # gpio pin 19 = wiringpi no. 12 (MOSI BCM 10)
	
	int spiChannel;
	
	/**
	 * Default Constructor starting lcd on Spi channel 0 and with a speed of 4000000
	 * @throws Exception 
	 */
	public Lcd() throws Exception {
		this(Spi.CHANNEL_0, 4000000);
	}
	
	/**
	 * 
	 * @param spiChannel [Spi.CHANNEL_0 | Spi.CHANNEL_1]
	 * @param spiSpeed 4000000 Recommended
	 * @throws Exception 
	 */
	public Lcd(int spiChannel, int spiSpeed) throws Exception {
		this.spiChannel = spiChannel;
		
		int fd = Spi.wiringPiSPISetup(spiChannel, spiSpeed);
        if (fd <= -1) {
            throw new Exception("SPI setup failed");
        }
        
        
        //Set pin directions.
        Gpio.wiringPiSetup();
        Gpio.pinMode(RST, Gpio.OUTPUT);
        Gpio.pinMode(DC, Gpio.OUTPUT);
        if (LED == 1) {
        	Gpio.pinMode(LED, Gpio.PWM_OUTPUT);
        } else {
        	Gpio.pinMode(LED, Gpio.OUTPUT);
        }
        
        // Toggle RST low to reset.
        Gpio.digitalWrite(RST, OFF);
        Thread.sleep(100);
        Gpio.digitalWrite(RST, ON);

        
        byte command[] = {0x21, 0x14, 0x20, 0x20, 0x0c};
    	lcd_cmd(command);
        set_brightness(0);
	}
	
	
	private void lcd_cmd(byte[] command)  {
		Gpio.digitalWrite(DC, OFF);
		if (Spi.wiringPiSPIDataRW(spiChannel, command, command.length) == -1) {
			System.err.println("spi failed lcd_cmd");
		}
	}
	
	private void lcd_data(byte[] data) {
		Gpio.digitalWrite(DC, ON);
		if (Spi.wiringPiSPIDataRW(spiChannel, data, data.length) == -1) {
			System.err.println("spi failed lcd_data");
		}
	}
	
	public void cls() {
	    gotoxy(0, 0);
	    lcd_data(clsBuffer.getData());
	}
	
	
	public void backlight(int value) {
	    set_brightness(256 * value);
	}

	
	public void set_brightness(int led_value) {
		if ( LED == 1) {
	        if (0 <= led_value && led_value < 1023) {
	        	Gpio.pwmWrite(LED, led_value);
	        }
		} else {
	        if (led_value == 0)
	        	Gpio.digitalWrite(LED, OFF);
	        else
	        	Gpio.digitalWrite(LED, ON);
		}
	}
	
	public void set_contrast(byte contrast) {
	    if ( 0x80 <= contrast && contrast < 0xFF) {
	    	byte command[] = {0x21, 0x14, contrast, 0x20, 0x0c};
	    	lcd_cmd(command);
	    }
	}
	
	private void gotoxy(int x, int y) {
		if ( (0 <= x && x < COLUMNS) && (0 <= y && y < ROWS)) {
			
			byte command[] = {(byte) (x+128), (byte) (y + 64) };
			lcd_cmd(command);
		}
	}
	
	public void gotorc(int r, int c) {
		gotoxy(c*ROWS, r);
	}
	
	public void text(String text) {
	    for (int i =0; i< text.length(); i++) {
	        display_char(text.charAt(i));
	    }
	}
	
	private void display_char(char b) {
        byte[] character = Font.getFontChar(b);
		lcd_data(character);//+[0]
	}
	
	public void show_image(BufferedImage im) {
		
		
		super.show_image(im);
		
		gotoxy(0, 0);
	    lcd_data(buffer.getData());
	}

}
