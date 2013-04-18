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
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

/**
 * @author art
 *
 */
public class GUI {

  // Board constants
  private static final int WINDOW_WIDTH = 480;
  private static final int WINDOW_HEIGHT = 480;
  private static final int BOARD_WIDTH = WINDOW_HEIGHT - 30;
  private static final float LINE_WIDTH = 2.0f;
  private static final int BOARD_OFFSET_X = 15;
  private static final int BOARD_OFFSET_Y = 15;

  // Clue constants
  private static final int CLUE_OFFSET_X = 3;
  private static final int CLUE_OFFSET_Y = 1;
  private static final int CLUE_FONT_SIZE = 12;

  // guess constants
  private static final int guess_OFFSET_X = 17;
  private static final int guess_OFFSET_Y = 10;
  private static final int guess_FONT_SIZE = 25;

  private static final String FONT_PATH = "res/DroidSans.ttf";

  // Height (or width) of problem in cells
  private int size;

  // Grid of cage IDs
  ArrayList<ArrayList<Integer>> cageIDs;

  // Pixel width of a cell
  private int cellWidth;

  // Number fonts
  private UnicodeFont clueFont;
  private UnicodeFont guessFont;

  // Matrix of user's cell guesses
  private ArrayList<ArrayList<Integer>> guessGrid;

  // Maps clue cells to clue text
  private TreeMap<Integer, String> clueText;

  // Grid indices of the currently hovered cell
  private int hoverCellX;
  private int hoverCellY;

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

    guessGrid = new ArrayList<ArrayList<Integer>>();
    for (int i = 0; i < size; ++i) {
      guessGrid.add(new ArrayList<Integer>(Collections.nCopies(size, -1)));
    }

    clueText = new TreeMap<Integer, String>();
    for (Cage c : problem.getCages()) {
      clueText.put(c.getCells().get(0), c.getClueText() + "");
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

      guessFont = new UnicodeFont(FONT_PATH, guess_FONT_SIZE, false, false);
      guessFont.addAsciiGlyphs();
      guessFont.addGlyphs(400, 600);
      guessFont.getEffects().add(new ColorEffect());
      guessFont.loadGlyphs();
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
  public void gameLoop() {
    System.out.println("Main loop starting.");

    while (!Display.isCloseRequested()) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
      Display.sync(60);
      pollInput();
      renderFrame();
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
  public void renderFrame() {
    // Draw cageIDs guides
    glColor3f(0.925f, 0.925f, 0.925f);

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

    // Draw highlighted cell's background
    if (hoverCellX >= 0 && hoverCellX < size && hoverCellY >= 0
      && hoverCellY < size) {
      // Highlight the new cell
      // TODO replace GL_QUADS with GL_TRIANGLEs
      glColor3f(0.9f, 0.9f, 0.9f);
      glBegin(GL_QUADS);
      glVertex2f(BOARD_OFFSET_X + hoverCellX * cellWidth, BOARD_OFFSET_Y
        + hoverCellY * cellWidth);
      glVertex2f(BOARD_OFFSET_X + (hoverCellX + 1) * cellWidth, BOARD_OFFSET_Y
        + hoverCellY * cellWidth);
      glVertex2f(BOARD_OFFSET_X + (hoverCellX + 1) * cellWidth, BOARD_OFFSET_Y
        + (hoverCellY + 1) * cellWidth);
      glVertex2f(BOARD_OFFSET_X + hoverCellX * cellWidth, BOARD_OFFSET_Y
        + (hoverCellY + 1) * cellWidth);
      glEnd();
    }

    // Draw cell walls (note that when traversing the cageIDs in either the
    // left-to-right or top-to-bottom direction, a wall needs to be placed if
    // and only if the current cell belongs to a different cage from the
    // previous cell)
    glColor3f(0.0f, 0.0f, 0.0f);
    int leftNeighborID = 0;
    int topNeighborID = 0;
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (cageIDs.get(j).get(i) != leftNeighborID) {
          glBegin(GL_LINES);
          glVertex2i(BOARD_OFFSET_X + i * cellWidth, BOARD_OFFSET_Y + cellWidth
            * j);
          glVertex2i(BOARD_OFFSET_X + (i + 1) * cellWidth, BOARD_OFFSET_Y
            + cellWidth * j);
          glEnd();
          leftNeighborID = cageIDs.get(j).get(i);
        }
        if (cageIDs.get(i).get(j) != topNeighborID) {
          glBegin(GL_LINES);
          glVertex2i(BOARD_OFFSET_X + j * cellWidth, BOARD_OFFSET_Y + cellWidth
            * i);
          glVertex2i(BOARD_OFFSET_X + j * cellWidth, BOARD_OFFSET_Y + cellWidth
            * (i + 1));
          glEnd();
          topNeighborID = cageIDs.get(i).get(j);
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

    // All fonts must be rendered last!

    // Draw clue text
    for (Map.Entry<Integer, String> e : clueText.entrySet()) {
      // TODO Check whether cell contains note and doesn't contain guess; draw
      // note if so
      clueFont.drawString(
        BOARD_OFFSET_X + CLUE_OFFSET_X + cellWidth * (e.getKey() % size),
        BOARD_OFFSET_Y + CLUE_OFFSET_Y + cellWidth * (e.getKey() / size),
        e.getValue(), Color.darkGray);
    }

    // Draw guess text
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (guessGrid.get(i).get(j) > 0) {
          guessFont.drawString(BOARD_OFFSET_X + j * cellWidth + guess_OFFSET_X,
            BOARD_OFFSET_Y + i * cellWidth + guess_OFFSET_Y,
            Integer.toString(guessGrid.get(i).get(j)), Color.black);
        }
      }
    }

    // Call this last, after rendering fonts
    GL11.glDisable(GL11.GL_TEXTURE_2D);
  }

  /**
   * Detect user input from keyboard and mouse.
   */
  private void pollInput() {
    // Need "+ cellWidth ... - 1" to make -0.5 round to -1 instead of 0
    // TODO Find a better way to do the above
    hoverCellX = (Mouse.getX() - BOARD_OFFSET_X + cellWidth) / cellWidth - 1;
    hoverCellY =
      (WINDOW_HEIGHT - Mouse.getY() - BOARD_OFFSET_Y + cellWidth) / cellWidth
        - 1;

    // Draw only if mouse is over board
    while (Keyboard.next()) {
      // Discard keydown events
      if (Keyboard.getEventKeyState()) {
        continue;
      }
      switch (Keyboard.getEventKey()) {
        case Keyboard.KEY_1:
        case Keyboard.KEY_NUMPAD1:
          markCell(1);
          break;
        case Keyboard.KEY_2:
        case Keyboard.KEY_NUMPAD2:
          markCell(2);
          break;
        case Keyboard.KEY_3:
        case Keyboard.KEY_NUMPAD3:
          markCell(3);
          break;
        case Keyboard.KEY_4:
        case Keyboard.KEY_NUMPAD4:
          markCell(4);
          break;
        case Keyboard.KEY_5:
        case Keyboard.KEY_NUMPAD5:
          markCell(5);
          break;
        case Keyboard.KEY_6:
        case Keyboard.KEY_NUMPAD6:
          markCell(6);
          break;
        case Keyboard.KEY_7:
        case Keyboard.KEY_NUMPAD7:
          markCell(7);
          break;
        case Keyboard.KEY_8:
        case Keyboard.KEY_NUMPAD8:
          markCell(8);
          break;
        case Keyboard.KEY_9:
        case Keyboard.KEY_NUMPAD9:
          markCell(9);
          break;
        default:
          break;
      }
    }
  }

  private void markCell(int n) {
    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
      || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
      // Mark note
      // TODO Note marking code
    } else {
      // Mark guess
      if (guessGrid.get(hoverCellY).get(hoverCellX) == n) {
        guessGrid.get(hoverCellY).set(hoverCellX, -1);
      } else {
        guessGrid.get(hoverCellY).set(hoverCellX, n);
      }
    }
  }
}
