package edu.virginia.kenken;

import static org.lwjgl.opengl.GL11.*;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

  // Guess constants
  private static final int GUESS_OFFSET_X = 17;
  private static final int GUESS_OFFSET_Y = 10;
  private static final int GUESS_FONT_SIZE = 25;

  // Note constants
  private static final int NOTE_OFFSET_X = 10;
  private static final int NOTE_OFFSET_Y = 15;
  private static final int NOTE_FONT_SIZE = 10;

  // Help text constants
  private static final int HELP_OFFSET_X = 19;
  private static final int HELP_OFFSET_Y = 11;
  private static final int HELP_FONT_SIZE = 20;

  private static final String FONT_PATH = "res/DroidSans.ttf";

  // Current problem
  private Problem problem;

  // Height (or width) of problem in cells
  private int size;

  // Grid of cage IDs
  HashMap<Integer, Integer> cageIDs;

  // Cell and cages relationship
  private ArrayList<Cage> cellCages;

  // Pixel width of a cell
  private int cellWidth;

  // Number fonts
  private UnicodeFont clueFont;
  private UnicodeFont guessFont;
  private UnicodeFont noteFont;

  // Help font
  private UnicodeFont helpFont;

  // Matrix of user's cell guesses
  private HashMap<Integer, Integer> guessGrid;

  // Matrix of user's cell notes
  private HashMap<Integer, ArrayList<Boolean>> noteGrid;

  // Matrix of incorrect cells
  private HashMap<Integer, Boolean> incorrectGrid;

  // Matrix of incorrect cell (cage)
  private ArrayList<ArrayList<Boolean>> incorrectCellCages;

  // Maps clue cells to clue text
  private TreeMap<Integer, String> clueText;

  // Grid indices of the currently hovered cell
  private int hoverCellX;
  private int hoverCellY;

  // Whether entry mode is "guess" or "note"
  private boolean inGuessMode;

  // Whether or not to show help on the board
  private boolean showHelp;

  // Whether main loop should be running
  private boolean running;

  public GUI(int startupSize) {
    setNewProblem(startupSize);
    init();
  }

  /*
   * Load a new problem instance into the main window.
   */
  private void setNewProblem(int size) {
    Display.setTitle("KenKen");
    this.size = size;
    cellWidth = BOARD_WIDTH / size;

    problem = new Problem(size);
    cageIDs = problem.getGrid();
    cellCages = problem.getCellCages();

    running = true;

    reset();

    clueText = new TreeMap<Integer, String>();
    for (Cage c : problem.getCages()) {
      clueText.put(c.getCells().get(0), c.getClueText() + "");
    }
  }

  private void reset() {
    guessGrid = new HashMap<Integer, Integer>();
    noteGrid = new HashMap<Integer, ArrayList<Boolean>>();
    incorrectGrid = new HashMap<Integer, Boolean>();
    incorrectCellCages = new ArrayList<ArrayList<Boolean>>();
    for (int i = 0; i < size; ++i) {
      incorrectCellCages.add(new ArrayList<Boolean>());
      for (int j = 0; j < size; ++j) {
        guessGrid.put(i * size + j, -1);
        noteGrid.put(i * size + j,
          new ArrayList<Boolean>(Collections.nCopies(size, false)));
        incorrectGrid.put(i * size + j, false);
        incorrectCellCages.get(i).add(false);
      }
    }

    inGuessMode = true;
  }

  /**
   * Initialize LWJGL and create the window.
   */
  @SuppressWarnings("unchecked")
  private void init() {
    // Create window
    try {
      Display.setDisplayMode(new DisplayMode(WINDOW_WIDTH, WINDOW_HEIGHT));
      Display.setTitle("KenKen");
      Display.create();
    } catch (LWJGLException e) {
      System.err.println("Display wasn't initialized correctly.");
      System.exit(1);
    }

    // Create keyboard
    try {
      Keyboard.create();
    } catch (LWJGLException e) {
      System.out.println("Keyboard could not be created.");
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
      // Temporarily disable System.out
      System.setOut(new PrintStream(new OutputStream() {
        @Override
        public void write(int b) {
          // Do nothing
        }
      }));

      clueFont = new UnicodeFont(FONT_PATH, CLUE_FONT_SIZE, false, false);
      clueFont.addAsciiGlyphs();
      clueFont.addGlyphs(400, 600);
      clueFont.getEffects().add(new ColorEffect());
      clueFont.loadGlyphs();

      guessFont = new UnicodeFont(FONT_PATH, GUESS_FONT_SIZE, false, false);
      guessFont.addAsciiGlyphs();
      guessFont.addGlyphs(400, 600);
      guessFont.getEffects().add(new ColorEffect());
      guessFont.loadGlyphs();

      noteFont = new UnicodeFont(FONT_PATH, NOTE_FONT_SIZE, false, false);
      noteFont.addAsciiGlyphs();
      noteFont.addGlyphs(400, 600);
      noteFont.getEffects().add(new ColorEffect());
      noteFont.loadGlyphs();

      helpFont = new UnicodeFont(FONT_PATH, HELP_FONT_SIZE, false, false);
      helpFont.addAsciiGlyphs();
      helpFont.addGlyphs(400, 600);
      helpFont.getEffects().add(new ColorEffect());
      helpFont.loadGlyphs();

      // Re-enable System.out
      System.setOut(System.out);

    } catch (SlickException e) {
      System.out.println("Failed to create font. Exiting.");
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Constantly refresh the window.
   */
  public void gameLoop() {
    while (!Display.isCloseRequested() && running) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
      Display.sync(60);
      pollInput();
      renderFrame();
      Display.update();
    }
  }

  /**
   * Draw the given problem onto the main window.
   * 
   * @param problem
   *          The problem instance
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
      if (inGuessMode) {
        glColor3f(0.8f, 0.8f, 0.8f);
      } else {
        glColor3f(0.7f, 0.7f, 1.0f);
      }
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

    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (incorrectGrid.get(i * size + j) == true) {
          // Highlight errors in red
          glColor3f(0.8f, 0.4f, 0.4f);
          glBegin(GL_QUADS);
          glVertex2f(BOARD_OFFSET_X + j * cellWidth, BOARD_OFFSET_Y + i
            * cellWidth);
          glVertex2f(BOARD_OFFSET_X + (j + 1) * cellWidth, BOARD_OFFSET_Y + i
            * cellWidth);
          glVertex2f(BOARD_OFFSET_X + (j + 1) * cellWidth, BOARD_OFFSET_Y
            + (i + 1) * cellWidth);
          glVertex2f(BOARD_OFFSET_X + j * cellWidth, BOARD_OFFSET_Y + (i + 1)
            * cellWidth);
          glEnd();
        }
      }
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
        if (cageIDs.get(j * size + i) != leftNeighborID) {
          glBegin(GL_LINES);
          glVertex2i(BOARD_OFFSET_X + i * cellWidth, BOARD_OFFSET_Y + cellWidth
            * j);
          glVertex2i(BOARD_OFFSET_X + (i + 1) * cellWidth, BOARD_OFFSET_Y
            + cellWidth * j);
          glEnd();
          leftNeighborID = cageIDs.get(j * size + i);
        }
        if (cageIDs.get(i * size + j) != topNeighborID) {
          glBegin(GL_LINES);
          glVertex2i(BOARD_OFFSET_X + j * cellWidth, BOARD_OFFSET_Y + cellWidth
            * i);
          glVertex2i(BOARD_OFFSET_X + j * cellWidth, BOARD_OFFSET_Y + cellWidth
            * (i + 1));
          glEnd();
          topNeighborID = cageIDs.get(i * size + j);
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

    // TODO Make overlay dimensions dependent on text size, not window size
    if (showHelp) {
      // Fade board
      glColor4f(0.0f, 0.0f, 0.0f, 0.8f);
      glBegin(GL_QUADS);
      glVertex2f(0, 0);
      glVertex2f(WINDOW_WIDTH, 0);
      glVertex2f(WINDOW_WIDTH, WINDOW_HEIGHT);
      glVertex2f(0, WINDOW_HEIGHT);
      glEnd();

      // Modal overlay
      glColor3f(1.0f, 1.0f, 1.0f);
      glBegin(GL_QUADS);
      glVertex2f(WINDOW_WIDTH * 0.1f, WINDOW_HEIGHT * 0.15f);
      glVertex2f(WINDOW_WIDTH * 0.9f, WINDOW_HEIGHT * 0.15f);
      glVertex2f(WINDOW_WIDTH * 0.9f, WINDOW_HEIGHT * 0.85f);
      glVertex2f(WINDOW_WIDTH * 0.1f, WINDOW_HEIGHT * 0.85f);
      glEnd();

      helpFont
        .drawString(
          HELP_OFFSET_X + WINDOW_WIDTH * 0.1f,
          HELP_OFFSET_Y + WINDOW_HEIGHT * 0.15f,
          "ESC:\nF1:\nF2:\nF3:\nF4:\nF5:\nF6:\nF7:\nF8:\nF9:\nF10:\nF11:\nOTHER:",
          Color.black);
      helpFont.drawString(HELP_OFFSET_X + WINDOW_WIDTH * 0.1f + 85,
        HELP_OFFSET_Y + WINDOW_HEIGHT * 0.15f,
        "EXIT\nHELP\nRESET\nNEW 3x3 PUZZLE\nNEW 4x4 PUZZLE\nNEW 5x5 PUZZLE\n"
          + "NEW 6x6 PUZZLE\nNEW 7x7 PUZZLE\nNEW 8x8 PUZZLE\n"
          + "NEW 9x9 PUZZLE\nSOLVE (BRUTE FORCE)\nSOLVE (DFS)\n"
          + "TOGGLE GUESS/NOTE MODE", Color.black);
    } else {
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
      Color normal = Color.black;
      Color incorrect = Color.red;
      Color guessColor;
      // Draw guess text and note text
      for (int i = 0; i < size; ++i) {
        for (int j = 0; j < size; ++j) {
          if (!incorrectCellCages.get(i).get(j)) {
            guessColor = normal;
          } else {
            guessColor = incorrect;
          }
          if (guessGrid.get(i * size + j) > 0) {
            guessFont.drawString(BOARD_OFFSET_X + j * cellWidth
              + GUESS_OFFSET_X,
              BOARD_OFFSET_Y + i * cellWidth + GUESS_OFFSET_Y,
              Integer.toString(guessGrid.get(i * size + j)), guessColor);
          } else {
            for (int k = 0; k < size; ++k) {
              if (noteGrid.get(i * size + j).get(k)) {
                noteFont.drawString(BOARD_OFFSET_X + j * cellWidth
                  + NOTE_OFFSET_X + 12 * (k % 3), BOARD_OFFSET_Y + i
                  * cellWidth + NOTE_OFFSET_Y + 10 * (2 - k / 3),
                  Integer.toString(k + 1), Color.blue);
              }
            }
          }
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
        case Keyboard.KEY_ESCAPE:
          running = false;
          break;
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
        case Keyboard.KEY_F1:
          showHelp = !showHelp;
          break;
        case Keyboard.KEY_F2:
          showHelp = false;
          reset();
          break;
        case Keyboard.KEY_F3:
          showHelp = false;
          setNewProblem(3);
          break;
        case Keyboard.KEY_F4:
          showHelp = false;
          setNewProblem(4);
          break;
        case Keyboard.KEY_F5:
          showHelp = false;
          setNewProblem(5);
          break;
        case Keyboard.KEY_F6:
          showHelp = false;
          setNewProblem(6);
          break;
        case Keyboard.KEY_F7:
          showHelp = false;
          setNewProblem(7);
          break;
        case Keyboard.KEY_F8:
          showHelp = false;
          setNewProblem(8);
          break;
        case Keyboard.KEY_F9:
          showHelp = false;
          setNewProblem(9);
          break;
        case Keyboard.KEY_F10:
          showHelp = false;
          BruteForceSolver bf = new BruteForceSolver(this, problem);
          bf.startTimer();
          bf.solve();
          bf.stopTimer();
          bf.printElapsedTime();
          Display.setTitle("KenKen -- Brute Force Solver took "
            + bf.getElapsedTime() * 0.000000001 + " s");
          break;
        case Keyboard.KEY_F11:
          showHelp = false;
          DepthFirstSolver dfs = new DepthFirstSolver(this, problem);
          dfs.startTimer();
          dfs.solve();
          dfs.stopTimer();
          dfs.printElapsedTime();
          Display.setTitle("KenKen -- DFS Solver took " + dfs.getElapsedTime()
            * 0.000000001 + " s");
          break;
        default:
          inGuessMode = !inGuessMode;
          break;
      }
    }
  }

  private void markCell(int n) {
    boolean isRemoval;
    int lastNumber = 0;
    if (hoverCellX >= 0 && hoverCellX < size && hoverCellY >= 0
      && hoverCellY < size) {
      if (inGuessMode) {
        // Mark guess
        if (guessGrid.get(hoverCellY * size + hoverCellX) == n) {
          guessGrid.put(hoverCellY * size + hoverCellX, -1);
          isRemoval = true;
        } else {
          lastNumber = guessGrid.get(hoverCellY * size + hoverCellX);
          guessGrid.put(hoverCellY * size + hoverCellX, n);
          isRemoval = false;
        }
        // Verify row
        ArrayList<Integer> currRow = new ArrayList<Integer>();
        for (int i = 0; i < size; ++i) {
          currRow.add(guessGrid.get(hoverCellY * size + i));
        }
        for (int i = 0; i < size; ++i) {
          if (currRow.get(i) < 0) {
            incorrectGrid.put(hoverCellY * size + i, false);
          } else {
            if (currRow.lastIndexOf(Integer.valueOf(currRow.get(i))) != i) {
              incorrectGrid.put(hoverCellY * size + i, true);
              incorrectGrid.put(
                hoverCellY * size
                  + currRow.lastIndexOf(Integer.valueOf(currRow.get(i))), true);
            }
            if (Collections.frequency(currRow, currRow.get(i)) < 2
              && incorrectGrid.get(hoverCellY * size + i) == true) {
              incorrectGrid.put(hoverCellY * size + i, false);
            }
          }
        }

        // Verify column
        ArrayList<Integer> currCol = new ArrayList<Integer>();
        for (int i = 0; i < size; ++i) {
          currCol.add(guessGrid.get(i * size + hoverCellX));
        }
        for (int i = 0; i < size; ++i) {
          if (currCol.get(i) < 0) {
            incorrectGrid.put(i * size + hoverCellX, false);
          } else {
            if (currCol.lastIndexOf(Integer.valueOf(currCol.get(i))) != i) {
              incorrectGrid.put(i * size + hoverCellX, true);
              incorrectGrid.put(
                currCol.lastIndexOf(Integer.valueOf(currCol.get(i))) * size
                  + hoverCellX, true);

            }
            if (Collections.frequency(currCol, currCol.get(i)) < 2
              && Collections.frequency(currRow, currCol.get(i)) < 2
              && incorrectGrid.get(i * size + hoverCellX) == true) {
              incorrectGrid.put(i * size + hoverCellX, false);
            }
          }
        }

        // Yes, recheck ALL the rows again
        ArrayList<Boolean> modifiedCols =
          new ArrayList<Boolean>(Collections.nCopies(size, false));
        for (int j = 0; j < size; ++j) {
          ArrayList<Integer> row = new ArrayList<Integer>();
          for (int m = 0; m < size; ++m) {
            row.add(guessGrid.get(j * size + m));
          }
          for (int k = 0; k < size; ++k) {
            if (row.get(k) < 0) {
              incorrectGrid.put(j * size + k, false);
              modifiedCols.set(k, true);
            } else {
              if (row.lastIndexOf(Integer.valueOf(row.get(k))) != k) {
                incorrectGrid.put(j * size + k, true);
                incorrectGrid
                  .put(j * size + row.lastIndexOf(Integer.valueOf(row.get(k))),
                    true);
              }
            }
          }
        }

        // verify all changed columns
        for (int i = 0; i < size; ++i) {
          if (modifiedCols.get(i)) {
            ArrayList<Integer> col = new ArrayList<Integer>();
            for (int j = 0; j < size; ++j) {
              col.add(guessGrid.get(j * size + i));
            }
            for (int k = 0; k < size; ++k) {

              if (col.get(k) < 0) {
                incorrectGrid.put(k * size + i, false);
              } else {
                if (col.lastIndexOf(Integer.valueOf(col.get(k))) != k) {
                  incorrectGrid.put(k * size + i, true);

                  incorrectGrid.put(
                    col.lastIndexOf(Integer.valueOf(col.get(k))) * size + i,
                    true);
                }
              }
            }
          }
        }

        // verify cell of user input once more
        if (isRemoval) {
          incorrectGrid.put(hoverCellY * size + hoverCellX, false);
        }

        // Deal with cages
        Cage cageToCheck = cellCages.get(hoverCellY * size + hoverCellX);
        if (guessGrid.get(hoverCellY * size + hoverCellX) > -1) {
          if (cageToCheck.isFilled(size, guessGrid)
            && !cageToCheck.isSatisfied(size, guessGrid)) {
            for (Integer i : cageToCheck.getCells()) {
              incorrectCellCages.get(i / size).set(i % size, true);

            }
          } else if (cageToCheck.isFilled(size, guessGrid)
            && cageToCheck.isSatisfied(size, guessGrid)) {
            for (Integer i : cageToCheck.getCells()) {
              incorrectCellCages.get(i / size).set(i % size, false);
            }
          }
        } else {
          for (Integer i : cageToCheck.getCells()) {
            incorrectCellCages.get(i / size).set(i % size, false);
          }
        }

      } else {
        // Mark note
        if (noteGrid.get(hoverCellY * size + hoverCellX).get(n - 1)) {
          noteGrid.get(hoverCellY * size + hoverCellX).set(n - 1, false);
        } else {
          noteGrid.get(hoverCellY * size + hoverCellX).set(n - 1, true);
        }
      }
    }
  }

  public void showProgress(HashMap<Integer, HashSet<Integer>> state) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    Display.sync(60);
    for (int i = 0; i < size * size; ++i) {
      guessGrid.put(i, (state.get(i).size() == 1) ? state.get(i).iterator()
        .next() : -1);
    }
    renderFrame();
    Display.update();
  }

  /**
   * Tear down the window
   */
  public void destroy() {
    Display.destroy();
  }
}
