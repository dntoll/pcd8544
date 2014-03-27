package se.daniel.robot.lcd.simulator;

import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import se.daniel.robot.lcd.AbstractLcd;
import se.daniel.robot.lcd.Font;

import com.jogamp.opengl.util.*;

public class Window extends AbstractLcd implements GLEventListener  {
	private int row = 0;
	private int column = 0;
	
	public Window() {
		GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps) {
        	   /**
			 * 
			 */
			private static final long serialVersionUID = -7126411011488666440L;

			@Override
        	   public Dimension getPreferredSize() {
        	      return new Dimension(84, 48);
        	   }
        };

        JFrame frame = new JFrame("AWT Window Test");
        frame.getContentPane().add(canvas);
        frame.pack();
        frame.setVisible(true);
        //frame.setResizable(false);
        

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        canvas.addGLEventListener(this);

        FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();
	}
	
    public static void main(String[] args) {
    	new Window();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
    	
	    
        render(drawable);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void init(GLAutoDrawable drawable) {
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
    }

    

    private void render(GLAutoDrawable drawable) {
    	
    	
    	
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers
		GLU glu = new GLU();
		gl.glViewport(0, 0, 84, 48);
	    gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
	    gl.glLoadIdentity();             // reset projection matrix
	    glu.gluOrtho2D(0, 84, 0, 48); 
	    gl.glMatrixMode(GL_MODELVIEW);
	    gl.glLoadIdentity(); 
	    

        // draw a triangle filling the window
        gl.glBegin(GL.GL_TRIANGLES);
        for (int x = 0; x < buffer.getWidth(); x++) {
        	for (int y = 0; y < buffer.getHeight(); y++) {
        		if (buffer.getPixel(x, y)) {
        			gl.glColor3f(1, 1, 1);
        		} else {
        			gl.glColor3f(0, 0, 0);
        		}
        		
		        gl.glVertex2d(x, y+1);
		        gl.glVertex2d(x+1, y+1);
		        gl.glVertex2d(x, y);
		        //gl.glEnd();
		       // gl.glBegin(GL.GL_TRIANGLES);
		        gl.glVertex2d(x, y);
		        gl.glVertex2d(x+1, y+1);
		        gl.glVertex2d(x+1, y);
		        
        	}
        }
        gl.glEnd();
       
    }

	@Override
	public void cls() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void backlight(int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void set_contrast(byte contrast) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void set_brightness(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show_image(BufferedImage im) {
		
		
		super.show_image(im);
		
		
		//im.getRGB(x, y);
	}

	@Override
	public void gotorc(int row, int column) {
		this.row = row;
		this.column = column;
	}

	@Override
	public void text(String text) {
		 for (int i =0; i< text.length(); i++) {
			 byte[] character = Font.getFontChar(text.charAt(i));
			 
			 for (int x = 0; x < 5; x++) {
				 for (int y = 0; y < 8; y++) {
					 byte b = character[x];
					 boolean value = (b & 1 << y)  > 0;
					 buffer.setPixel((this.column + i)*5 + x, (5-this.row)*8 + 8-y, value);
				 }
			 }
		 }
	}
}
