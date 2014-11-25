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
		
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File(args[0]));
		    lcd.show_image(img);
		} catch (IOException e) {
			
		}
		/*lcd.gotorc(0,0);
		lcd.text("012");
		lcd.gotorc(2,2);
		lcd.text("Hello World");*/
		lcd.set_brightness(512);
		
		/**/
    	
    	
    }
}
