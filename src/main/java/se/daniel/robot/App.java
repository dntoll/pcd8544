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
    	System.out.println("Starting up");
    	AbstractLcd lcd = LCDFactory.createLcd();
    	
    	
    	System.out.println("cls");
		lcd.cls();
		
		System.out.println("brightness");
		lcd.set_brightness(512);
		BufferedImage img = null;
		try {
			System.out.println("read image");
		    img = ImageIO.read(new File(args[0]));
		    
		    System.out.println("show image");
		    lcd.show_image(img);
		} catch (IOException e) {
			
		}
		lcd.gotorc(0,0);
		lcd.text("012");
		lcd.gotorc(2,2);
		lcd.text("Hello World");
		
		System.out.println("brightness");
		lcd.set_brightness(512);
		
		/**/
    	
    	
    }
}
