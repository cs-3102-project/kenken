package edu.virginia.kenken;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;

public class Main {

  /**
   * @param args
   */
  public static void main(String[] args) {
    Problem problem = new Problem(9);
    try {
      Display.setDisplayMode(new DisplayMode(640, 480));
      Display.setTitle("Episode 1 – Display Test");
      Display.create();
    } catch (LWJGLException e) {
      System.err.println("Display wasn't initialized correctly.");
      System.exit(1);
    }
  
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity(); // Resets any previous projection matrices
    glOrtho(0, 640, 480, 0, 1, -1);
    glMatrixMode(GL_MODELVIEW);
  
    glBegin(GL_LINES);
      glVertex2i(100,100);
      glVertex2i(200,100);
    glEnd();
  

    while (!Display.isCloseRequested()) {
      Display.update();
      Display.sync(60);
    }

    Display.destroy();
    System.exit(0);
  }

}
