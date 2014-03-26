package se.daniel.robot.lcd;

import java.awt.image.BufferedImage;


public abstract class AbstractLcd {
	protected static final byte ROWS = 6;
	protected static final byte COLUMNS = 14;
	protected static final byte PIXELS_PER_ROW = 6;
	protected static final boolean ON = true;
	protected static final boolean OFF = false;
	protected static final PixelBuffer clsBuffer = new PixelBuffer(ROWS, COLUMNS, PIXELS_PER_ROW);
	protected PixelBuffer buffer = new PixelBuffer(ROWS, COLUMNS, PIXELS_PER_ROW);
	public abstract void cls();
	
	public abstract void backlight(int value);
	
	public abstract void set_contrast(byte contrast);
	
	public abstract void set_brightness(int i);
	
	public void show_image(BufferedImage im) {
		for (int x = 0; x < im.getWidth(); x++) {
			for (int y = 0; y < im.getHeight(); y++) {
				int rgb = im.getRGB(x, y);
				boolean bw = blackAndWhite(rgb);
				//System.out.println("rgb : " + rgb + " bw: " +bw );
				buffer.setPixel(x, y, bw);//rgb < -1);
			}	
		}
	}

	public abstract void gotorc(int row, int column);

	public abstract void text(String string);

	//TYPE_INT_ARGB
	private boolean blackAndWhite(int colorInteger) {
		int redMask = 0x00FF0000;
		int greenMask = 0x0000FF00;
		int blueMask = 0x000000FF;
		int red = (colorInteger & redMask) >> 16;
		int green = (colorInteger & greenMask) >> 8;
		int blue = (colorInteger & blueMask);
		
		//grayscale
		int medium = (red + green + blue) / 3;
		
		
		return (medium) < 128;
	}
}
