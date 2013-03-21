package edu.virginia.kenken;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class GUI {

  // Board constants
  private static final int              WINDOW_WIDTH    = 640;
  private static final int              WINDOW_HEIGHT   = 480;
  private static final int              BOARD_WIDTH     = WINDOW_HEIGHT - 30;
  private static final float            LINE_WIDTH      = 2.0f;
  private static final int              BOARD_OFFSET_X  = 15;
  private static final int              BOARD_OFFSET_Y  = 15;

  // Clue constants
  private static final int              CLUE_OFFSET     = 5;
  private static final int              CLUE_FONT_SIZE  = 12;

  // Entry constants
  private static final int              ENTRY_OFFSET_X  = 17;
  private static final int              ENTRY_OFFSET_Y  = 10;
  private static final int              ENTRY_FONT_SIZE = 25;

  private static final String           FONT_PATH       = "res/DroidSans.ttf";

  // Height (or width) of problem in cells
  private int                           size;

  // Grid of cage IDs
  ArrayList<ArrayList<Integer>>         cageIDs;

  // Pixel width of a cell
  private int                           cellWidth;

  // Number fonts
  private UnicodeFont                   clueFont;
  private UnicodeFont                   entryFont;

  // Matrix of user's cell guesses
  private ArrayList<ArrayList<Integer>> entryGrid;

  // Maps clue cells to clue text
  private TreeMap<Integer, String>      clueText;
  private ArrayList<ArrayList<Boolean>> cellHasClue;

  // Used for pollInput()
  private int                           cellX;
  private int                           cellY;
  private int                           originX;
  private int                           originY;
  private int                           oldCellX;
  private int                           oldCellY;
  private int                           oldOriginX;
  private int                           oldOriginY;

  public GUI(Problem problem) {
    setProblem(problem);
    init();
  }

  /*
   * Load a new problem instance into the main window.
   */
  private void setProblem(Problem problem) {
    size = problem.getSize();
    cellWidth = BOARD_WIDTH / size;
    cageIDs = problem.getGrid();

    entryGrid = new ArrayList<ArrayList<Integer>>();
    cellHasClue = new ArrayList<ArrayList<Boolean>>();
    for (int i = 0; i < size; ++i) {
      entryGrid.add(new ArrayList<Integer>(Collections.nCopies(size, -1)));
      cellHasClue.add(new ArrayList<Boolean>(Collections.nCopies(size, false)));
    }

    clueText = new TreeMap<Integer, String>();
    for (Cage c : problem.getCages()) {
      clueText.put(c.getCells().get(0), c.getTotal() + "");
    }
  }

  /**
   * Initialize LWJGL and create the window.
   */
  @SuppressWarnings("unchecked")
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
    glLineWidth(LINE_WIDTH);

    try {
      clueFont = new UnicodeFont(FONT_PATH, CLUE_FONT_SIZE, false, false);
      clueFont.addAsciiGlyphs();
      clueFont.addGlyphs(400, 600);
      clueFont.getEffects().add(new ColorEffect());
      clueFont.loadGlyphs();

      entryFont = new UnicodeFont(FONT_PATH, ENTRY_FONT_SIZE, false, false);
      entryFont.addAsciiGlyphs();
      entryFont.addGlyphs(400, 600);
      entryFont.getEffects().add(new ColorEffect());
      entryFont.loadGlyphs();
    } catch (SlickException e) {
      System.out.println("Failed to create font. Exiting.");
      e.printStackTrace();
      System.exit(1);
    }

    System.out.println("Window initialized.");
  }

  /**
   * Constantly refresh the window.
   */
  public void loop() {
    System.out.println("Main loop starting.");

    while (!Display.isCloseRequested()) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
      Display.sync(60);
      pollInput();
      drawProblem();
      Display.update();
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
  public void drawProblem() {
    // Draw clue text
    // TODO Change "Hi" to e.getValue()
    for (Map.Entry<Integer, String> e : clueText.entrySet()) {
      clueFont.drawString(
        BOARD_OFFSET_X + CLUE_OFFSET + cellWidth * (e.getKey() / size),
        BOARD_OFFSET_Y + CLUE_OFFSET + cellWidth * (e.getKey() % size), "Hi",
        Color.black);
    }

    // Draw cageIDs guides
    glColor3f(0.9f, 0.9f, 0.9f);

    for (int i = 1; i < size; ++i) {
      // Horizontal lines
      glBegin(GL_LINES);
      glVertex2i(BOARD_OFFSET_X, BOARD_OFFSET_Y + cellWidth * i);
      glVertex2i(BOARD_OFFSET_X + size * cellWidth, BOARD_OFFSET_Y + cellWidth
        * i);
      glEnd();

      // Vertical lines
      glBegin(GL_LINES);
      glVertex2i(BOARD_OFFSET_X + i * cellWidth, BOARD_OFFSET_Y);
      glVertex2i(BOARD_OFFSET_X + i * cellWidth, BOARD_OFFSET_Y + cellWidth
        * size);
      glEnd();
    }

    // Draw cell walls (note that when traversing the cageIDs in either the
    // left-to-right or top-to-bottom direction, a wall needs to be placed if
    // and only if the current cell belongs to a different cage from the
    // previous cell)

    glColor3f(0.0f, 0.0f, 0.0f);

    int hID = 0;
    int vID = 0;
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (cageIDs.get(j).get(i) != hID) {
          glBegin(GL_LINES);
          glVertex2i(BOARD_OFFSET_X + i * cellWidth, BOARD_OFFSET_Y + cellWidth
            * j);
          glVertex2i(BOARD_OFFSET_X + (i + 1) * cellWidth, BOARD_OFFSET_Y
            + cellWidth * j);
          glEnd();
          hID = cageIDs.get(j).get(i);
        }
        if (cageIDs.get(i).get(j) != vID) {
          glBegin(GL_LINES);
          glVertex2i(BOARD_OFFSET_X + j * cellWidth, BOARD_OFFSET_Y + cellWidth
            * i);
          glVertex2i(BOARD_OFFSET_X + j * cellWidth, BOARD_OFFSET_Y + cellWidth
            * (i + 1));
          glEnd();
          vID = cageIDs.get(i).get(j);
        }
      }
    }

    // Draw board boundaries

    glBegin(GL_LINES); // Top
    glVertex2i(BOARD_OFFSET_X, BOARD_OFFSET_Y);
    glVertex2i(BOARD_OFFSET_X + size * cellWidth, BOARD_OFFSET_Y);
    glEnd();

    glBegin(GL_LINES); // Bottom
    glVertex2i(BOARD_OFFSET_X, BOARD_OFFSET_Y + cellWidth * size);
    glVertex2i(BOARD_OFFSET_X + size * cellWidth, BOARD_OFFSET_Y + cellWidth
      * size);
    glEnd();

    glBegin(GL_LINES); // Left
    glVertex2i(BOARD_OFFSET_X, BOARD_OFFSET_Y);
    glVertex2i(BOARD_OFFSET_X, BOARD_OFFSET_Y + cellWidth * size);
    glEnd();

    glBegin(GL_LINES); // Right
    glVertex2i(BOARD_OFFSET_X + size * cellWidth, BOARD_OFFSET_Y);
    glVertex2i(BOARD_OFFSET_X + size * cellWidth, BOARD_OFFSET_Y + cellWidth
      * size);
    glEnd();
  }

  // TODO replace GL_QUADS with GL_TRIANGLEs since the former is being
  // deprecated in OpenGL 3
  // TODO make the highlighting of the cell cover an area smaller so we don't
  // overwrite the cageIDs lines
  private void pollInput() {
    cellX = (Mouse.getX() - BOARD_OFFSET_X) / cellWidth;
    cellY = (WINDOW_HEIGHT - Mouse.getY() - BOARD_OFFSET_Y) / cellWidth;

    if (cellX >= 0 && cellX < size && cellY >= 0 && cellY < size) {
      originX = cellX * cellWidth + BOARD_OFFSET_X;
      originY = cellY * cellWidth + BOARD_OFFSET_Y;

      // Removes the rendering lag (fade effect)
      glDisable(GL_TEXTURE_2D);

      // Un-highlight the old cell
      glColor3f(1.0f, 1.0f, 1.0f);
      glBegin(GL_QUADS);
      glVertex2f(oldOriginX, oldOriginY);
      glVertex2f(oldOriginX + cellWidth, oldOriginY);
      glVertex2f(oldOriginX + cellWidth, oldOriginY + cellWidth);
      glVertex2f(oldOriginX, oldOriginY + cellWidth);
      glEnd();

      // Highlight the new cell
      glColor3f(0.9f, 0.9f, 0.9f);
      glBegin(GL_QUADS);
      glVertex2f(originX, originY);
      glVertex2f(originX + cellWidth, originY);
      glVertex2f(originX + cellWidth, originY + cellWidth);
      glVertex2f(originX, originY + cellWidth);
      glEnd();

      // Restore entry value
      if (entryGrid.get(oldCellY).get(oldCellX) >= 0) {
        entryFont.drawString(oldOriginX + ENTRY_OFFSET_X, oldOriginY
          + ENTRY_OFFSET_Y,
          Integer.toString(entryGrid.get(oldCellY).get(oldCellX)), Color.black);
      }

      if (entryGrid.get(cellY).get(cellX) >= 0) {
        entryFont.drawString(originX + ENTRY_OFFSET_X,
          originY + ENTRY_OFFSET_Y,
          Integer.toString(entryGrid.get(cellY).get(cellX)), Color.black);
      }

      // Restore hints if necessary
      if (cellHasClue.get(oldCellX).get(oldCellY)) {
        clueFont.drawString(
          BOARD_OFFSET_X + CLUE_OFFSET + cellWidth * oldCellX, BOARD_OFFSET_Y
            + CLUE_OFFSET + cellWidth * oldCellY, "Hi", Color.black);
      }

      if (cellHasClue.get(cellX).get(cellY)) {
        clueFont.drawString(BOARD_OFFSET_X + CLUE_OFFSET + cellWidth * cellX,
          BOARD_OFFSET_Y + CLUE_OFFSET + cellWidth * cellY, "Hi", Color.black);
      }

      oldCellX = cellX;
      oldCellY = cellY;
      oldOriginX = originX;
      oldOriginY = originY;

      int charCode;
      while (Keyboard.next()) {
        charCode = Keyboard.getEventKey();
        if (charCode <= 11) {
          entryFont.drawString(originX + ENTRY_OFFSET_X, originY
            + ENTRY_OFFSET_Y, Integer.toString((charCode - 1) % 10),
            Color.black);
          entryGrid.get(cellY).set(cellX, (charCode - 1) % 10);
        }
      }
    }
  }
}
