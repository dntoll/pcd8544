package se.daniel.robot.lcd.rpi;

import java.awt.image.BufferedImage;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.Spi;

import se.daniel.robot.lcd.AbstractLcd;
import se.daniel.robot.lcd.Font;

public class Ili9341 extends AbstractLcd {


	//#gpio's :
	static final byte DC   = 4;// # gpio pin 16 = wiringpi no. 4 (BCM 23)
	static final byte RST  = 5;// # gpio pin 18 = wiringpi no. 5 (BCM 24)
	static byte LED  = 1;// # gpio pin 12 = wiringpi no. 1 (BCM 18)

	//# SPI connection, these are only for documentation...
	static final byte SCE  = 10;// # gpio pin 24 = wiringpi no. 10 (CE0 BCM 8) 
	static final byte SCLK = 14;// # gpio pin 23 = wiringpi no. 14 (SCLK BCM 11)
	static final byte DIN  = 12;// # gpio pin 19 = wiringpi no. 12 (MOSI BCM 10)
	
	int spiChannel;
	byte[] buff;
	int buffersize = 320*240*2;
	/**
	 * Default Constructor starting lcd on Spi channel 0 and with a speed of 4000000
	 * @throws Exception 
	 */
	public Ili9341() throws Exception {
		this(Spi.CHANNEL_0, 4000000);
		buff = new byte[buffersize];
		for(int i = 0;i < buffersize; i++)
			buff[i] = (byte) 0x00;
	}
	
	/**
	 * 
	 * @param spiChannel [Spi.CHANNEL_0 | Spi.CHANNEL_1]
	 * @param spiSpeed 4000000 Recommended
	 * @throws Exception 
	 */
	private Ili9341(int spiChannel, int spiSpeed) throws Exception {
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
        
        lcd_init();
        
        set_brightness(0);
	}
	
	private void lcd_init() throws InterruptedException {
		
		// Toggle RST low to reset.
        Gpio.digitalWrite(RST, OFF);
        Thread.sleep(10);
        Gpio.digitalWrite(RST, ON);
        
		LCD_Write_COM(0xCB);  
        LCD_Write_DATA(0x39); 
        LCD_Write_DATA(0x2C); 
        LCD_Write_DATA(0x00); 
        LCD_Write_DATA(0x34); 
        LCD_Write_DATA(0x02); 

        LCD_Write_COM(0xCF);  
        LCD_Write_DATA(0x00); 
        LCD_Write_DATA(0XC1); 
        LCD_Write_DATA(0X30); 

        LCD_Write_COM(0xE8);  
        LCD_Write_DATA(0x85); 
        LCD_Write_DATA(0x00); 
        LCD_Write_DATA(0x78); 

        LCD_Write_COM(0xEA);  
        LCD_Write_DATA(0x00); 
        LCD_Write_DATA(0x00); 
 
        LCD_Write_COM(0xED);  
        LCD_Write_DATA(0x64); 
        LCD_Write_DATA(0x03); 
        LCD_Write_DATA(0X12); 
        LCD_Write_DATA(0X81); 

        LCD_Write_COM(0xF7);  
        LCD_Write_DATA(0x20); 
  
        LCD_Write_COM(0xC0);    //Power control 
        LCD_Write_DATA(0x23);   //VRH[5:0] 
 
        LCD_Write_COM(0xC1);    //Power control 
        LCD_Write_DATA(0x10);   //SAP[2:0];BT[3:0] 

        LCD_Write_COM(0xC5);    //VCM control 
        LCD_Write_DATA(0x3e);   //Contrast
        LCD_Write_DATA(0x28); 
 
        LCD_Write_COM(0xC7);    //VCM control2 
        LCD_Write_DATA(0x86);   //--
 
        LCD_Write_COM(0x36);    // Memory Access Control 
        LCD_Write_DATA(0x48);   //C8	   //48 68竖屏//28 E8 横屏

        LCD_Write_COM(0x3A);    
        LCD_Write_DATA(0x55); 

        LCD_Write_COM(0xB1);    
        LCD_Write_DATA(0x00);  
        LCD_Write_DATA(0x18); 
 
        LCD_Write_COM(0xB6);    // Display Function Control 
        LCD_Write_DATA(0x08); 
        LCD_Write_DATA(0x82);
        LCD_Write_DATA(0x27);  
/* 
        LCD_Write_COM(0xF2);    // 3Gamma Function Disable 
        LCD_Write_DATA(0x00); 
 
        LCD_Write_COM(0x26);    //Gamma curve selected 
        LCD_Write_DATA(0x01); 

        LCD_Write_COM(0xE0);    //Set Gamma 
        LCD_Write_DATA(0x0F); 
        LCD_Write_DATA(0x31); 
        LCD_Write_DATA(0x2B); 
        LCD_Write_DATA(0x0C); 
        LCD_Write_DATA(0x0E); 
        LCD_Write_DATA(0x08); 
        LCD_Write_DATA(0x4E); 
        LCD_Write_DATA(0xF1); 
        LCD_Write_DATA(0x37); 
        LCD_Write_DATA(0x07); 
        LCD_Write_DATA(0x10); 
        LCD_Write_DATA(0x03); 
        LCD_Write_DATA(0x0E); 
        LCD_Write_DATA(0x09); 
        LCD_Write_DATA(0x00); 

        LCD_Write_COM(0XE1);    //Set Gamma 
        LCD_Write_DATA(0x00); 
        LCD_Write_DATA(0x0E); 
        LCD_Write_DATA(0x14); 
        LCD_Write_DATA(0x03); 
        LCD_Write_DATA(0x11); 
        LCD_Write_DATA(0x07); 
        LCD_Write_DATA(0x31); 
        LCD_Write_DATA(0xC1); 
        LCD_Write_DATA(0x48); 
        LCD_Write_DATA(0x08); 
        LCD_Write_DATA(0x0F); 
        LCD_Write_DATA(0x0C); 
        LCD_Write_DATA(0x31); 
        LCD_Write_DATA(0x36); 
        LCD_Write_DATA(0x0F); 
*/
        LCD_Write_COM(0x11);    //Exit Sleep 
        Thread.sleep(120);
				
        LCD_Write_COM(0x29);    //Display on 
        LCD_Write_COM(0x2c);   
	}
	
	
	private void LCD_Write_DATA(int i) {
		byte[] command = new byte[] {(byte) i};
		lcd_data(command);
	}

	private void LCD_Write_COM(int i) {
		byte[] command = new byte[] {(byte) i};
		lcd_cmd(command);
	}

	
	private void lcd_cmd(byte[] command)  {
		
		Gpio.digitalWrite(DC, OFF);
		if (Spi.wiringPiSPIDataRW(spiChannel, command, command.length) == -1) {
			System.err.println("spi failed lcd_cmd");
		}
	
	}
	int maxBufferSize = 1024;
	byte[]  sendBuffer = new byte[maxBufferSize];
	private void lcd_data(byte[] command) {
		/*System.out.println("lcd_cmd " + command.length);
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}*/
		
		Gpio.digitalWrite(DC, ON);
		
		
	
		if (command.length < maxBufferSize) {
			if (Spi.wiringPiSPIDataRW(spiChannel, command, command.length) == -1) {
				System.err.println("spi failed lcd_cmd");
			}
		} else {
			int numcalls = command.length / maxBufferSize;
			
			for (int call = 0; call < numcalls; call++) {
				
				for (int b = 0; b < maxBufferSize; b++) {
					sendBuffer[b] = command[call * maxBufferSize + b];
				}
				//System.out.println("Sent call " + call);
				if (Spi.wiringPiSPIDataRW(spiChannel, sendBuffer, sendBuffer.length) == -1) {
					System.err.println("spi failed lcd_cmd");
				}
				
				
			}
			
			int numLeftOvers = command.length % maxBufferSize;
			
			if (numLeftOvers > 0) {
				//leftovers
				byte leftovers[] = new byte[numLeftOvers];
				for (int b = 0; b < 1024; b++) {
					leftovers[b] = command[numcalls * maxBufferSize + b];
				}
				//System.out.println("Sent leftover " + numLeftOvers);
				if (Spi.wiringPiSPIDataRW(spiChannel, leftovers, leftovers.length) == -1) {
					System.err.println("spi failed lcd_cmd");
				}
				
				
			}
		}
	}
	
	public void cls() {
	   /* gotoxy(0, 0);
	    lcd_data(clsBuffer.getData());*/
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
	   /* if ( 0x80 <= contrast && contrast < 0xFF) {
	    	byte command[] = {0x21, 0x14, contrast, 0x20, 0x0c};
	    	lcd_cmd(command);
	    }*/
	}
	
	/*private void gotoxy(int x, int y) {
		if ( (0 <= x && x < COLUMNS) && (0 <= y && y < ROWS)) {
			
			byte command[] = {(byte) (x+128), (byte) (y + 64) };
			lcd_cmd(command);
		}
	}*/
	
	void Address_set( int x1, int y1, int x2, int y2)
	{
	   LCD_Write_COM(0x2a);
	   LCD_Write_DATA(x1>>8);
	   LCD_Write_DATA(x1);
	   LCD_Write_DATA(x2>>8);
	   LCD_Write_DATA(x2);
	  
	   LCD_Write_COM(0x2b);
	   LCD_Write_DATA(y1>>8);
	   LCD_Write_DATA(y1);
	   LCD_Write_DATA(y2>>8);
	   LCD_Write_DATA(y2);

	   LCD_Write_COM(0x2C);         				 
	}
	
	public void gotorc(int r, int c) {
		gotoxy(c*ROWS, r);
	}
	
	private void gotoxy(int x, int y) {
		Address_set(x,y, 240, 320);
	}

	public void text(String text) {
	   /* for (int i =0; i< text.length(); i++) {
	        display_char(text.charAt(i));
	    }*/
	}
	
	private void display_char(char b) {
       /* byte[] character = Font.getFontChar(b);
		lcd_data(character);//+[0]*/
	}
	
	public void show_image(BufferedImage im) {
		
		
		super.show_image(im);
		
		Pant(0x00);
		//gotoxy(0, 0);
		//Address_set(0,0,240,320);
	    //lcd_data(buffer.getData());
		
		/*Pant(0xFF);   
		Pant(0xF0);   
		Pant(0xE0);  
		Pant(0x05);  
		Pant(0x1F);    
		Pant(0x00); */
	}
	
	void Pant(int VL)
	{
		System.out.println("Pant started + VL");
	  int i,j;
	  Address_set(0,0,240,320);
	  
	  int pixel = 0;
	  
	  for(i=0;i<320;i++)
	  {
	    for (j=0;j<240;j++)
	    {
	    	if (i < buffer.getWidth() && j < buffer.getHeight()) {
		    	if (buffer.getPixel(i, j) ) {
		    		setColor(buff, pixel, 255, 255, 255);
		    	} else {
		    		setColor(buff, pixel, 0, 0, 0);
		    	}
	    	} else {
	    		setColor(buff, pixel, 0, 255, 0);
	    	}
	    	pixel++;
	    }
	  }
	  System.out.println("Done buffering");
	  lcd_data(buff);
	  
	  System.out.println("Pant ended");
	}
	
	void setColor(byte[] buff, int index, int r, int g, int b)
	{
		// rrrrrggggggbbbbb
		int bch=((r&248)|g>>5);
		int bcl=((g&28)<<3|b>>3);
		int color = (bch<<8) | bcl;
		
		if (index * 2 + 1 < buffersize) {
			buff[index*2] = (byte) bch;
			buff[index*2+1] = (byte) bcl;
		}
		/*LCD_Write_DATA(bch);
		LCD_Write_DATA(bcl);*/
	}


}
