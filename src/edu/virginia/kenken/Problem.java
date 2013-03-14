package edu.virginia.kenken;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Problem {

  private final int size;

  public Problem(int size) {
    this.size = size;

    ArrayList<ArrayList<Integer>> cells = new ArrayList<ArrayList<Integer>>();

    // Start with a legal, non-random board

    for (int i = 0; i < size; ++i) {
      cells.add(new ArrayList<Integer>());
      for (int j = 0; j < size; ++j) {
        cells.get(i).add((i + j) % size + 1);
      }
    }

    // Shuffle rows

    Collections.shuffle(cells);

    // Transpose board matrix

    int tmp;
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < i; ++j) {
        tmp = cells.get(i).get(j);
        cells.get(i).set(j, cells.get(j).get(i));
        cells.get(j).set(i, tmp);
      }
    }

    // Shuffle rows (which were the columns before transposition) again

    Collections.shuffle(cells);

    // Print matrix (for testing only)

    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        System.out.print(cells.get(i).get(j));
      }
      System.out.print("\n");
    }
    
    // Graphics set-up here
    final int horizOffset  = 50;
    final int vertiOffset = 100;
    ArrayList<ArrayList<Boolean>> hWalls = new ArrayList<ArrayList<Boolean>>();
    ArrayList<ArrayList<Boolean>> vWalls = new ArrayList<ArrayList<Boolean>>();
    
    
    Random rand = new Random();
    for(int i = 0; i < size-1; ++i)
    {
      hWalls.add(new ArrayList<Boolean>());
      vWalls.add(new ArrayList<Boolean>());
      for(int j = 0; j < size; ++j)
      {

        boolean randhWall = (rand.nextInt(2) == 1) ? true : false;
        hWalls.get(i).add(randhWall);
        boolean randvWall = (rand.nextInt(2) == 1) ? true : false;
        vWalls.get(i).add(randvWall);
      }
    }
    
    
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
    glOrtho(0, 640, 640, 0, 1, -1);
    glMatrixMode(GL_MODELVIEW);
    glEnable(GL_COLOR_MATERIAL);
    
    // set the background color to white
    glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    
    // increase line thickness
    glLineWidth(2.0f);
    
    // draw the start-up grid
    glColor3f(0.7f, 0.7f, 0.7f);
    
    for(int i = 0; i < size; ++i)
    {
      // draw the horizontal lines
      for(int j = 0; j < size; ++j) 
      {
        if(i == 0)
        {
          glColor3f(0.0f, 0.0f, 0.0f);
        } else {
          glColor3f(0.7f, 0.7f, 0.7f);
        }
        glBegin(GL_LINES);
          glVertex2i(horizOffset + j* 50, vertiOffset + 50 * i);
          glVertex2i(horizOffset + j * 50 + 50, vertiOffset + 50 * i);
        glEnd();
      }
      
      // draw the vertical lines
      for(int j = 0; j < size + 1; ++j)
      {
        if( j == 0 || j == size)
        {
          glColor3f(0.0f, 0.0f, 0.0f);
        } else {
          glColor3f(0.7f, 0.7f, 0.7f);
        }
        glBegin(GL_LINES);
          glVertex2i(horizOffset + j * 50, vertiOffset + 50*i);
          glVertex2i(horizOffset + j * 50, vertiOffset + 50 + 50 * i);
        glEnd();
      }
    }
    
    glColor3f(0.0f, 0.0f, 0.0f);
    for(int i = 0; i < size; ++i) 
    {
      glBegin(GL_LINES);
        glVertex2i(horizOffset + i* 50, vertiOffset + 50 * size);
        glVertex2i(horizOffset + i * 50 + 50, vertiOffset + 50 * size);
      glEnd();
    }
    
    // draw the cages
    glColor3f(0.0f, 0.0f, 0.0f);
    
    // start with the hWalls; ignore top and bottom
    for(int i = 1; i < size; ++i)
    {
      for(int j = 0; j < size; ++j)
      {
        if(hWalls.get(i-1).get(j) == true) {
          glBegin(GL_LINES);
            glVertex2i(horizOffset + j* 50, vertiOffset + 50 * i);
            glVertex2i(horizOffset + j * 50 + 50, vertiOffset + 50 * i);
          glEnd();
        }
      }
    }
    
    // then with the vWalls; ignore leftmost and rightmost
    for(int i = 1; i < size; ++i)
    {
      for(int j = 0; j < size; ++j)
      {
        if(vWalls.get(i-1).get(j) == true) {
          glBegin(GL_LINES);
            glVertex2i(horizOffset + j * 50, vertiOffset + 50*i);
            glVertex2i(horizOffset + j * 50, vertiOffset + 50 + 50 * i);
          glEnd();
        }
      }
    }
        
    while (!Display.isCloseRequested()) {
        Display.update();
        Display.sync(60);
    }
  
    Display.destroy();
    System.exit(0);
    
  }

}
