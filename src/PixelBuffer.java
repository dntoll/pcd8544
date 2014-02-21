
public class PixelBuffer {

	byte data[];
	byte rows;
	byte columns;
	byte pixelsPerRow;
	
	

	public PixelBuffer(byte rows, byte columns, byte pixelsPerRow) {
		int sizeInBytes = rows * columns * pixelsPerRow;
		data = new byte[sizeInBytes];
		
		for(int i = 0;i < sizeInBytes; i++)
			data[i] = (byte) 0x00;
	}

	public byte[] getData() {
		return data;
	}
	
	private byte _BV(int v) {
		return (byte) (1 << v);
	}

	public void setPixel(int x, int y, boolean value) {
		//https://github.com/adafruit/Adafruit-PCD8544-Nokia-5110-LCD-library/blob/master/Adafruit_PCD8544.cpp
		if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) {
			if (value) 
			    data[x+ (y/8)*getWidth()] |= _BV(y%8);  
			  else
				  data[x+ (y/8)*getWidth()] &= ~_BV(y%8); 
		}
	}
	//84x48
	private int getHeight() {
		
		return 48;
	}

	private int getWidth() {
		// TODO Auto-generated method stub
		return 84;
	}

	

}
