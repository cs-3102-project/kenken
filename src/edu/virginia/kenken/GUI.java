package edu.virginia.kenken;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class GUI {

  private static final int H_OFFSET         = 15;
  private static final int V_OFFSET         = 15;
  private static final int WINDOW_WIDTH     = 640;
  private static final int WINDOW_HEIGHT    = 480;
  private static final int OPERATION_OFFSET = 5;
  private static String fontPath = "res/DroidSans.ttf";
  private static UnicodeFont cageOperation = null;
  private static ArrayList<Boolean> cageProcessed;

  public GUI() {
    init();
  }

  /**
   * Initialize LWJGL and create the window.
   */
  private void init() {
    try {
      Display.setDisplayMode(new DisplayMode(WINDOW_WIDTH, WINDOW_HEIGHT));
      Display.setTitle("KenKen");
      Display.create();
    } catch (LWJGLException e) {
      System.err.println("Display wasn't initialized correctly.");
      System.exit(1);
    }
    
    glEnable(GL_TEXTURE_2D);
    glShadeModel(GL_SMOOTH);       
    glDisable(GL_DEPTH_TEST);
    glDisable(GL_LIGHTING);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glOrtho(0, WINDOW_WIDTH, WINDOW_HEIGHT, 0, 1, -1);
    glMatrixMode(GL_MODELVIEW);
    glEnable(GL_COLOR_MATERIAL);

    // Set background color to white

    glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    // Line thickness

    glLineWidth(2.0f);

    System.out.println("Window initialized.");
  }

  /**
   * Constantly refresh the window.
   */
  public void loop() {
    System.out.println("Main loop starting.");

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
    int cellWidth = 450 / size;

    // Draw grid guides

    glColor3f(0.9f, 0.9f, 0.9f);

    for (int i = 1; i < size; ++i) {
      // Horizontal lines
      glBegin(GL_LINES);
      glVertex2i(H_OFFSET, V_OFFSET + cellWidth * i);
      glVertex2i(H_OFFSET + size * cellWidth, V_OFFSET + cellWidth * i);
      glEnd();

      // Vertical lines
      glBegin(GL_LINES);
      glVertex2i(H_OFFSET + i * cellWidth, V_OFFSET);
      glVertex2i(H_OFFSET + i * cellWidth, V_OFFSET + cellWidth * size);
      glEnd();
    }

    // Draw cell walls (note that when traversing the grid in either the
    // left-to-right or top-to-bottom direction, a wall needs to be placed if
    // and only if the current cell belongs to a different cage from the
    // previous cell)

    glColor3f(0.0f, 0.0f, 0.0f);

    int hID = 0;
    int vID = 0;
    ArrayList<ArrayList<Integer>> grid = problem.getGrid();
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (grid.get(j).get(i) != hID) {
          glBegin(GL_LINES);
          glVertex2i(H_OFFSET + i * cellWidth, V_OFFSET + cellWidth * j);
          glVertex2i(H_OFFSET + (i + 1) * cellWidth, V_OFFSET + cellWidth * j);
          glEnd();
          hID = grid.get(j).get(i);
        }
        if (grid.get(i).get(j) != vID) {
          glBegin(GL_LINES);
          glVertex2i(H_OFFSET + j * cellWidth, V_OFFSET + cellWidth * i);
          glVertex2i(H_OFFSET + j * cellWidth, V_OFFSET + cellWidth * (i + 1));
          glEnd();
          vID = grid.get(i).get(j);
        }
      }
    }

    // Draw boundary

    glBegin(GL_LINES); // Top
    glVertex2i(H_OFFSET, V_OFFSET);
    glVertex2i(H_OFFSET + size * cellWidth, V_OFFSET);
    glEnd();

    glBegin(GL_LINES); // Bottom
    glVertex2i(H_OFFSET, V_OFFSET + cellWidth * size);
    glVertex2i(H_OFFSET + size * cellWidth, V_OFFSET + cellWidth * size);
    glEnd();

    glBegin(GL_LINES); // Left
    glVertex2i(H_OFFSET, V_OFFSET);
    glVertex2i(H_OFFSET, V_OFFSET + cellWidth * size);
    glEnd();

    glBegin(GL_LINES); // Right
    glVertex2i(H_OFFSET + size * cellWidth, V_OFFSET);
    glVertex2i(H_OFFSET + size * cellWidth, V_OFFSET + cellWidth * size);
    glEnd();
    
    // Draw cage operations
    cageProcessed = new ArrayList<Boolean>(Collections.nCopies(problem
      .getCurID(), false));
    
    try {
      // Specify the font size with the second parameter to the constructor
      cageOperation = new UnicodeFont(fontPath , 12, false, false);
      cageOperation.addAsciiGlyphs();   // Add Glyphs
      cageOperation.addGlyphs(400, 600); // Add Glyphs
      cageOperation.getEffects().add(new ColorEffect(java.awt.Color.BLACK));
      cageOperation.loadGlyphs();  // Load Glyphs
      
    } catch (SlickException e) {
      // TODO Auto-generated catch block
      System.out.println("FAILED TO CREATE FONT!! EXITING...");
      System.exit(1);
      e.printStackTrace();
    }
    
    // Traverse through grid; if we find a number that we have not seen before,
    // then write the operation on the corresponding cell
    // note: there are curID cages, from 0 to curID - 1
    for(int i = 0; i < size; ++i)
    {
      for(int j = 0; j < size; ++j)
      {
        if(cageProcessed.get(grid.get(i).get(j)) == false)
        {
          cageOperation.drawString(H_OFFSET + OPERATION_OFFSET + cellWidth * j,
            V_OFFSET + OPERATION_OFFSET + cellWidth * i, "Hi");
          cageProcessed.set(grid.get(i).get(j), true); 
        }
      }
    }    
  }
}
