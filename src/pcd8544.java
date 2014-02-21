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
public class pcd8544 {
	// White backlight
	static final byte CONTRAST = (byte) 0xaa;
	static final byte ROWS = 6;
	
	static final byte COLUMNS = 14;
	static final byte PIXELS_PER_ROW = 6;
	static final boolean ON = true;
	static final boolean OFF = false;

	//#gpio's :
	static final byte DC   = 4;// # gpio pin 16 = wiringpi no. 4 (BCM 23)
	static final byte RST  = 5;// # gpio pin 18 = wiringpi no. 5 (BCM 24)
	static byte LED  = 1;// # gpio pin 12 = wiringpi no. 1 (BCM 18)

	//# SPI connection
	static final byte SCE  = 10;// # gpio pin 24 = wiringpi no. 10 (CE0 BCM 8) 
	static final byte SCLK = 14;// # gpio pin 23 = wiringpi no. 14 (SCLK BCM 11)
	static final byte DIN  = 12;// # gpio pin 19 = wiringpi no. 12 (MOSI BCM 10)
	
	static final int Channel = Spi.CHANNEL_0;
	static final int Speed = 4000000;
	
	
	byte CLSBUF[] = new byte[ROWS * COLUMNS * PIXELS_PER_ROW];
	
	public pcd8544() throws InterruptedException {
		for(int i = 0;i < ROWS * COLUMNS * PIXELS_PER_ROW; i++)
			CLSBUF[i] = (byte) 0x00;
		/**def init(dev=(0,0),speed=4000000, brightness=256, contrast=CONTRAST):
		    spi.open(dev[0],dev[1])
		    spi.max_speed_hz=speed

		 */
		int fd = Spi.wiringPiSPISetup(Channel, Speed);
        if (fd <= -1) {
            System.out.println(" ==>> SPI SETUP FAILED");
            return;
        }
        
        
        //Set pin directions.
        Gpio.wiringPiSetup();
        Gpio.pinMode(RST, Gpio.OUTPUT);
        Gpio.pinMode(DC, Gpio.OUTPUT);
        
        // Toggle RST low to reset.
        Gpio.digitalWrite(RST, OFF);
        Thread.sleep(100);
        
        Gpio.digitalWrite(RST, ON);
        
        //# Extended mode, bias, vop, basic mode, non-inverted display.
        //set_contrast(CONTRAST);
        byte command[] = {0x21, 0x14, 0x20, 0x20, 0x0c};
    	lcd_cmd(command);
        

        //# if LED == 1 set pin mode to PWM else set it to OUT
        if (LED == 1) {
        	Gpio.pinMode(LED, Gpio.PWM_OUTPUT);
	        Gpio.pwmWrite(LED, 0);
        } else {
        	Gpio.pinMode(LED, Gpio.OUTPUT);
	        Gpio.digitalWrite(LED, OFF);
        }
	}
	
	
	public void lcd_cmd(byte[] command)  {
		Gpio.digitalWrite(DC, OFF);
		if (Spi.wiringPiSPIDataRW(Channel, command, command.length) == -1) {
			System.err.println("spi failed lcd_cmd");
		}
	}
	
	public void lcd_data(byte[] data) {
		Gpio.digitalWrite(DC, ON);
		if (Spi.wiringPiSPIDataRW(Channel, data, data.length) == -1) {
			System.err.println("spi failed lcd_data");
		}
	}
	
	public void cls() {
	    gotoxy(0, 0);
	    lcd_data(CLSBUF);
	}
	
	
	public void backlight(int value) {
	    set_brightness(256 * value);
	}


	


	
	
	
	
	private void set_brightness(int led_value) {
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
	
	private void gotorc(int r, int c) {
		gotoxy(c*ROWS, r);
	}
	
	public void text(String text) {
		
	    for (int i =0; i< text.length(); i++) {
	        display_char(text.charAt(i));
	    }
	}
	
	public void display_char(char b) {
        byte[] character =getFontChar(b);// {0x00, 0x00, 0x5f, 0x00, 0x00, 0x00};
		lcd_data(character);//+[0]
	}


	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		
		pcd8544 lcd = new pcd8544();
		lcd.cls();
		lcd.gotorc(1,1);
		for (int i = 0; i< 1023; i++) {
			lcd.set_brightness(i);
		    Thread.sleep(5);
		}
		lcd.text("hello");
		
		for (int i = 1022; i>= 0; i--) {
			lcd.set_brightness(i);
		    Thread.sleep(5);
		}
		
/*
		ORIGINAL_CUSTOM = FONT['\x7f']

		def bit_reverse(value, width=8):
		  result = 0
		  for _ in xrange(width):
		    result = (result << 1) | (value & 1)
		    value >>= 1

		  return result

		BITREVERSE = map(bit_reverse, xrange(256))

		spi = spidev.SpiDev()

		def init(dev=(0,0),speed=4000000, brightness=256, contrast=CONTRAST):
		    spi.open(dev[0],dev[1])
		    spi.max_speed_hz=speed

		    # Set pin directions.
		    wiringpi.wiringPiSetup()
		    for pin in [DC, RST]:
		        wiringpi.pinMode(pin, 1)

		    # Toggle RST low to reset.
		    wiringpi.digitalWrite(RST, OFF)
		    time.sleep(0.100)
		    wiringpi.digitalWrite(RST, ON)
		    # Extended mode, bias, vop, basic mode, non-inverted display.
		    set_contrast(contrast)

		    # if LED == 1 set pin mode to PWM else set it to OUT
		    if LED == 1:
		        wiringpi.pinMode(LED, 2)
		        wiringpi.pwmWrite(LED,0)
		    else:
		        wiringpi.pinMode(LED, 1)
		        wiringpi.digitalWrite(LED, OFF)
		 


		def lcd_cmd(value):
		    wiringpi.digitalWrite(DC, OFF)
		    spi.writebytes([value])


		def lcd_data(value):
		    wiringpi.digitalWrite(DC, ON)
		    spi.writebytes([value])


		def cls():
		    gotoxy(0, 0)
		    wiringpi.digitalWrite(DC, ON)
		    spi.writebytes(CLSBUF)


		def backlight(value):
		    set_brightness(256*value)


		def set_brightness(led_value):
		    if  LED == 1:
		        if (0 <= led_value < 1023):
		            wiringpi.pwmWrite(LED,led_value)
		    else:
		        if led_value == 0:
		            wiringpi.digitalWrite(LED, OFF)
		        else:
		            wiringpi.digitalWrite(LED, ON)


		def set_contrast(contrast):
		    if ( 0x80 <= contrast < 0xFF):
		        wiringpi.digitalWrite(DC, OFF)
		        spi.writebytes([0x21, 0x14, contrast, 0x20, 0x0c])


		def gotoxy(x, y):
		    if ( (0 <= x < COLUMNS) and (0 <= y < ROWS)):
		        wiringpi.digitalWrite(DC, OFF)
		        spi.writebytes([x+128,y+64])


		def gotorc(r, c):
		    gotoxy(c*6,r)


		def text(string, font=FONT):
		    for char in string:
		        display_char(char, font)


		def centre_text(r, word):
		    gotorc(r, max(0, (COLUMNS - len(word)) // 2))
		    text(word)


		def show_custom_char(font=FONT):
		    display_char('\x7f', font)


		def define_custom_char(values):
		    FONT['\x7f'] = values


		def restore_custom_char():
		    define_custom_char(ORIGINAL_CUSTOM)


		def alt_custom_char():
		    define_custom_char([0x00, 0x50, 0x3C, 0x52, 0x44])


		def pi_custom_char():
		    define_custom_char([0x19, 0x25, 0x5A, 0x25, 0x19])


		def display_char(char, font=FONT):
		    try:
		        wirls
		        ingpi.digitalWrite(DC, ON)
		        spi.writebytes(font[char]+[0])

		    except KeyError:
		        pass # Ignore undefined characters.


		def load_bitmap(filename, reverse=False):
		    mask = 0x00 if reverse else 0xff
		    gotoxy(0, 0)
		    with open(filename, 'rb') as bitmap_file:
		        for x in xrange(6):
		          for y in xrange(84):
		            bitmap_file.seek(0x3e + y * 8 + x)
		            lcd_data(BITREVERSE[ord(bitmap_file.read(1))] ^ mask)


		def show_image(im):
		    # Rotate and mirror the image
		    rim = im.rotate(-90).transpose(Image.FLIP_LEFT_RIGHT)

		    # Change display to vertical write mode for graphics
		    wiringpi.digitalWrite(DC, OFF)
		    spi.writebytes([0x22])

		    # Start at upper left corner
		    gotoxy(0, 0)
		    # Put on display with reversed bit order
		    wiringpi.digitalWrite(DC, ON)
		    spi.writebytes( [ BITREVERSE[ord(x)] for x in list(rim.tostring()) ] )

		    # Switch back to horizontal write mode for text
		    wiringpi.digitalWrite(DC, OFF)
		    spi.writebytes([0x20])*/
	}
	byte[] getFontChar(char c) {
		
	 String characters = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVXYZ[\\]^_`abcdefghijklmnopqrstuvxyz{|}~";
	 byte font[][] = {
			{0x00, 0x00, 0x00, 0x00, 0x00},
			{0x00, 0x00, 0x5f, 0x00, 0x00},
			{0x00, 0x07, 0x00, 0x07, 0x00},
			{0x14, 0x7f, 0x14, 0x7f, 0x14},
			{0x24, 0x2a, 0x7f, 0x2a, 0x12},
			{0x23, 0x13, 0x08, 0x64, 0x62},
			{0x36, 0x49, 0x55, 0x22, 0x50},
			{0x00, 0x05, 0x03, 0x00, 0x00},
			{0x00, 0x1c, 0x22, 0x41, 0x00},
			{0x00, 0x41, 0x22, 0x1c, 0x00},
			{0x14, 0x08, 0x3e, 0x08, 0x14},
			{0x08, 0x08, 0x3e, 0x08, 0x08},
			{0x00, 0x50, 0x30, 0x00, 0x00},
			{0x08, 0x08, 0x08, 0x08, 0x08},
			{0x00, 0x60, 0x60, 0x00, 0x00},
			{0x20, 0x10, 0x08, 0x04, 0x02},
			{0x3e, 0x51, 0x49, 0x45, 0x3e},
			{0x00, 0x42, 0x7f, 0x40, 0x00},
			{0x42, 0x61, 0x51, 0x49, 0x46},
			{0x21, 0x41, 0x45, 0x4b, 0x31},
			{0x18, 0x14, 0x12, 0x7f, 0x10},
			{0x27, 0x45, 0x45, 0x45, 0x39},
			{0x3c, 0x4a, 0x49, 0x49, 0x30},
			{0x01, 0x71, 0x09, 0x05, 0x03},
			{0x36, 0x49, 0x49, 0x49, 0x36},
			{0x06, 0x49, 0x49, 0x29, 0x1e},
			{0x00, 0x36, 0x36, 0x00, 0x00},
			{0x00, 0x56, 0x36, 0x00, 0x00},
			{0x08, 0x14, 0x22, 0x41, 0x00},
			{0x14, 0x14, 0x14, 0x14, 0x14},
			{0x00, 0x41, 0x22, 0x14, 0x08},
			{0x02, 0x01, 0x51, 0x09, 0x06},
			{0x32, 0x49, 0x79, 0x41, 0x3e},
			{0x7e, 0x11, 0x11, 0x11, 0x7e},
			{0x7f, 0x49, 0x49, 0x49, 0x36},
			{0x3e, 0x41, 0x41, 0x41, 0x22},
			{0x7f, 0x41, 0x41, 0x22, 0x1c},
			{0x7f, 0x49, 0x49, 0x49, 0x41},
			{0x7f, 0x09, 0x09, 0x09, 0x01},
			{0x3e, 0x41, 0x49, 0x49, 0x7a},
			{0x7f, 0x08, 0x08, 0x08, 0x7f},
			{0x00, 0x41, 0x7f, 0x41, 0x00},
			{0x20, 0x40, 0x41, 0x3f, 0x01},
			{0x7f, 0x08, 0x14, 0x22, 0x41},
			{0x7f, 0x40, 0x40, 0x40, 0x40},
			{0x7f, 0x02, 0x0c, 0x02, 0x7f},
			{0x7f, 0x04, 0x08, 0x10, 0x7f},
			{0x3e, 0x41, 0x41, 0x41, 0x3e},
			{0x7f, 0x09, 0x09, 0x09, 0x06},
			{0x3e, 0x41, 0x51, 0x21, 0x5e},
			{0x7f, 0x09, 0x19, 0x29, 0x46},
			{0x46, 0x49, 0x49, 0x49, 0x31},
			{0x01, 0x01, 0x7f, 0x01, 0x01},
			{0x3f, 0x40, 0x40, 0x40, 0x3f},
			{0x1f, 0x20, 0x40, 0x20, 0x1f},
			{0x3f, 0x40, 0x38, 0x40, 0x3f},
			{0x63, 0x14, 0x08, 0x14, 0x63},
			{0x07, 0x08, 0x70, 0x08, 0x07},
			{0x61, 0x51, 0x49, 0x45, 0x43},
			{0x00, 0x7f, 0x41, 0x41, 0x00},
			{0x02, 0x04, 0x08, 0x10, 0x20},
			{0x00, 0x41, 0x41, 0x7f, 0x00},
			{0x04, 0x02, 0x01, 0x02, 0x04},
			{0x40, 0x40, 0x40, 0x40, 0x40},
			{0x00, 0x01, 0x02, 0x04, 0x00},
			{0x20, 0x54, 0x54, 0x54, 0x78},
			{0x7f, 0x48, 0x44, 0x44, 0x38},
			{0x38, 0x44, 0x44, 0x44, 0x20},
			{0x38, 0x44, 0x44, 0x48, 0x7f},
			{0x38, 0x54, 0x54, 0x54, 0x18},
			{0x08, 0x7e, 0x09, 0x01, 0x02},
			{0x0c, 0x52, 0x52, 0x52, 0x3e},
			{0x7f, 0x08, 0x04, 0x04, 0x78},
			{0x00, 0x44, 0x7d, 0x40, 0x00},
			{0x20, 0x40, 0x44, 0x3d, 0x00},
			{0x7f, 0x10, 0x28, 0x44, 0x00},
			{0x00, 0x41, 0x7f, 0x40, 0x00},
			{0x7c, 0x04, 0x18, 0x04, 0x78},
			{0x7c, 0x08, 0x04, 0x04, 0x78},
			{0x38, 0x44, 0x44, 0x44, 0x38},
			{0x7c, 0x14, 0x14, 0x14, 0x08},
			{0x08, 0x14, 0x14, 0x18, 0x7c},
			{0x7c, 0x08, 0x04, 0x04, 0x08},
			{0x48, 0x54, 0x54, 0x54, 0x20},
			{0x04, 0x3f, 0x44, 0x40, 0x20},
			{0x3c, 0x40, 0x40, 0x20, 0x7c},
			{0x1c, 0x20, 0x40, 0x20, 0x1c},
			{0x3c, 0x40, 0x30, 0x40, 0x3c},
			{0x44, 0x28, 0x10, 0x28, 0x44},
			{0x0c, 0x50, 0x50, 0x50, 0x3c},
			{0x44, 0x64, 0x54, 0x4c, 0x44},
			{0x00, 0x08, 0x36, 0x41, 0x00},
			{0x00, 0x00, 0x7f, 0x00, 0x00},
			{0x00, 0x41, 0x36, 0x08, 0x00},
			{0x10, 0x08, 0x08, 0x10, 0x08},
			{0x00, 0x7e, 0x42, 0x42, 0x7e}
			};
	 int index = characters.indexOf(c);
	 
	 return font[index];
	}

}
