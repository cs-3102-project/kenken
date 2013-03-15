package edu.virginia.kenken;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class GUI {

  private final int hOffset = 50;
  private final int vOffset = 100;

  public GUI() {
    init();
    drawProblem(new Problem(9)); // TODO Call this from a  GUI event instead
  }

  /**
   * Initialize LWJGL and create the window.
   */
  private void init() {
    try {
      Display.setDisplayMode(new DisplayMode(640, 480));
      Display.setTitle("KenKen");
      Display.create();
    } catch (LWJGLException e) {
      System.err.println("Display wasn't initialized correctly.");
      System.exit(1);
    }

    glMatrixMode(GL_PROJECTION);
    glLoadIdentity(); // Resets any previous projection matrices
    glOrtho(0, 640, 640, 0, 1, -1);
    glMatrixMode(GL_MODELVIEW);
    glEnable(GL_COLOR_MATERIAL);

    // set the background color to white
    glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    // increase line thickness
    glLineWidth(2.0f);
  }

  /**
   * Constantly refresh the window.
   */
  public void loop() {
    while (!Display.isCloseRequested()) {
      Display.update();
      Display.sync(60);
    }
  }

  /**
   * Tear down the window
   */
  public void destroy() {
    Display.destroy();
    System.out.println("Execution terminated.");
  }

  /**
   * Draw the given problem onto the main window.
   * 
   * @param problem The problem instance
   */
  public void drawProblem(Problem problem) {

    int size = problem.getSize();

    ArrayList<ArrayList<Boolean>> hWalls = new ArrayList<ArrayList<Boolean>>();
    ArrayList<ArrayList<Boolean>> vWalls = new ArrayList<ArrayList<Boolean>>();

    Random rand = new Random();

    for (int i = 0; i < size; ++i) {
      hWalls.add(new ArrayList<Boolean>());
      vWalls.add(new ArrayList<Boolean>());
      for (int j = 0; j < size; ++j) {
        hWalls.get(i).add(rand.nextBoolean());
        vWalls.get(i).add(rand.nextBoolean());
      }
    }

    // draw the start-up grid
    glColor3f(0.9f, 0.9f, 0.9f);

    for (int i = 0; i < size; ++i) {
      // draw the horizontal lines
      for (int j = 0; j < size; ++j) {
        if (i == 0) {
          glColor3f(0.0f, 0.0f, 0.0f);
        } else {
          glColor3f(0.9f, 0.9f, 0.9f);
        }
        glBegin(GL_LINES);
        glVertex2i(hOffset + j * 50, vOffset + 50 * i);
        glVertex2i(hOffset + j * 50 + 50, vOffset + 50 * i);
        glEnd();
      }

      // draw the vertical lines
      for (int j = 0; j < size + 1; ++j) {
        if (j == 0 || j == size) {
          glColor3f(0.0f, 0.0f, 0.0f);
        } else {
          glColor3f(0.9f, 0.9f, 0.9f);
        }
        glBegin(GL_LINES);
        glVertex2i(hOffset + j * 50, vOffset + 50 * i);
        glVertex2i(hOffset + j * 50, vOffset + 50 + 50 * i);
        glEnd();
      }
    }

    glColor3f(0.0f, 0.0f, 0.0f);
    for (int i = 0; i < size; ++i) {
      glBegin(GL_LINES);
      glVertex2i(hOffset + i * 50, vOffset + 50 * size);
      glVertex2i(hOffset + i * 50 + 50, vOffset + 50 * size);
      glEnd();
    }

    // draw the cages
    glColor3f(0.0f, 0.0f, 0.0f);

    // draw hWalls/vWalls
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (hWalls.get(i).get(j)) {
          glBegin(GL_LINES);
          glVertex2i(hOffset + j * 50, vOffset + 50 * i);
          glVertex2i(hOffset + j * 50 + 50, vOffset + 50 * i);
          glEnd();
        }
        if (vWalls.get(i).get(j)) {
          glBegin(GL_LINES);
          glVertex2i(hOffset + j * 50, vOffset + 50 * i);
          glVertex2i(hOffset + j * 50, vOffset + 50 + 50 * i);
          glEnd();
        }
      }
    }
  }

}
