package se.daniel.robot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import se.daniel.robot.lcd.AbstractLcd;
import se.daniel.robot.lcd.LCDFactory;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        //Multi platform LCD
    	//On PC its shown as a Window
    	
    	AbstractLcd lcd = LCDFactory.createLcd();
    	
		lcd.cls();
		lcd.gotorc(1,1);
		lcd.text(args[0]);
		lcd.set_brightness(512);
		
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File(args[0]));
		    lcd.show_image(img);
		} catch (IOException e) {
		}
    	
    	
    }
}
