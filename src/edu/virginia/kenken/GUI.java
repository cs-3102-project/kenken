package edu.virginia.kenken;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
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

  // Guess variables
  private int guess_offset_x;
  private int guess_offset_y;
  private static final int GUESS_FONT_SIZE = 25;

  // Note constants
  private static final int NOTE_OFFSET_X = 10;
  private static final int NOTE_OFFSET_Y = 15;
  private static final int NOTE_FONT_SIZE = 10;

  // Help text constants
  private static final int HELP_OFFSET_X = 19;
  private static final int HELP_OFFSET_Y = 11;
  private static final int HELP_FONT_SIZE = 20;
  private static final String HELP_TEXT = "ESC:\n" + "F1:\n" + "F2:\n"
    + "F3:\n" + "F4:\n" + "F5:\n" + "F6:\n" + "F7:\n" + "F8:\n" + "F9:\n"
    + "F10:\n" + "F11:\n" + "F12:\n" + "BKSP:\n" + "OTHER:";
  private static final String HELP_DESC = "EXIT\n" + "HELP\n" + "RESET\n"
    + "NEW 3x3 PUZZLE\n" + "NEW 4x4 PUZZLE\n" + "NEW 5x5 PUZZLE\n"
    + "NEW 6x6 PUZZLE\n" + "NEW 7x7 PUZZLE\n" + "NEW 8x8 PUZZLE\n"
    + "NEW 9x9 PUZZLE\n" + "SOLVE (BRUTE FORCE)\n" + "SOLVE (DFS)\n"
    + "ENABLE/DISABLE % CAGES\n" + "UNDO\n" + "TOGGLE GUESS/NOTE MODE";

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

  // Guess/note history
  private Stack<Integer> numHistory;
  private Stack<Boolean> toggleHistory;
  private Stack<Integer> hoverXHistory;
  private Stack<Integer> hoverYHistory;

  // Grid indices of the currently hovered cell
  private int hoverCellX;
  private int hoverCellY;

  // Whether entry mode is "guess" or "note"
  private boolean inGuessMode;

  // Whether or not to show help on the board
  private boolean showHelp;

  // Whether or not problems with modulo cages can be generated
  private boolean modEnabled;

  // Whether main loop should be running
  private boolean running;

  // Used for checking whether player-filled board is solution
  private HashMap<Integer, HashSet<Integer>> attempt;

  // Used for displaying time player took to solve puzzle
  private long startTime;

  // Whether current guess/note entry is actually an undo action
  private boolean isUndo;

  public GUI(int startupSize) {
    running = true;
    modEnabled = false;
    init();
    setNewProblem(startupSize);
  }

  public HashMap<Integer, Integer> getGuessGrid() {
    return guessGrid;
  }

  public HashMap<Integer, ArrayList<Boolean>> getNoteGrid() {
    return noteGrid;
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
      clueFont = new UnicodeFont(Driver.fontPath, CLUE_FONT_SIZE, false, false);
      clueFont.addAsciiGlyphs();
      clueFont.getEffects().add(new ColorEffect());
      clueFont.loadGlyphs();

      guessFont =
        new UnicodeFont(Driver.fontPath, GUESS_FONT_SIZE, false, false);
      guessFont.addAsciiGlyphs();
      guessFont.getEffects().add(new ColorEffect());
      guessFont.loadGlyphs();

      noteFont = new UnicodeFont(Driver.fontPath, NOTE_FONT_SIZE, false, false);
      noteFont.addAsciiGlyphs();
      noteFont.getEffects().add(new ColorEffect());
      noteFont.loadGlyphs();

      helpFont = new UnicodeFont(Driver.fontPath, HELP_FONT_SIZE, false, false);
      helpFont.addAsciiGlyphs();
      helpFont.getEffects().add(new ColorEffect());
      helpFont.loadGlyphs();
    } catch (Exception e) {
      System.out.println("Failed to create font. Exiting.");
      e.printStackTrace();
      System.exit(1);
    }
  }

  private void reset() {
    guessGrid = new HashMap<Integer, Integer>();
    noteGrid = new HashMap<Integer, ArrayList<Boolean>>();
    incorrectGrid = new HashMap<Integer, Boolean>();
    incorrectCellCages = new ArrayList<ArrayList<Boolean>>();
    attempt = new HashMap<Integer, HashSet<Integer>>();
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
    numHistory = new Stack<Integer>();
    toggleHistory = new Stack<Boolean>();
    hoverXHistory = new Stack<Integer>();
    hoverYHistory = new Stack<Integer>();

    Display.setTitle("KenKen");
    startTime = System.nanoTime();
  }

  /*
   * Load a new problem instance into the main window.
   */
  private void setNewProblem(int size) {
    this.size = size;
    cellWidth = BOARD_WIDTH / size;

    problem = new Problem(size, modEnabled);
    cageIDs = problem.getGrid();
    cellCages = problem.getCellCages();

    // Calculate guess offsets
    guess_offset_x = (int) (cellWidth * 0.5 - 8);
    guess_offset_y = guess_offset_x - 7;

    // Clear board
    reset();

    // Generate clue texts
    clueText = new TreeMap<Integer, String>();
    for (Cage c : problem.getCages()) {
      clueText.put(c.getCells().get(0), c.getClueText() + "");
    }
  }

  /**
   * Constantly refresh the window.
   */
  public void gameLoop() {
    while (!Display.isCloseRequested() && running && Driver.running) {
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

    // Highlight errors in red
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (incorrectGrid.get(i * size + j) || incorrectCellCages.get(i).get(j)) {
          glColor3f(1.0f, 0.7f, 0.7f);
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

    // Draw highlighted cell's background
    if (!isUndo) {
      if (hoverCellX >= 0 && hoverCellX < size && hoverCellY >= 0
        && hoverCellY < size) {
        // Highlight the new cell
        if (inGuessMode) {
          glColor3f(0.8f, 0.8f, 0.8f);
        } else {
          glColor3f(0.7f, 0.7f, 1.0f);
        }
        glBegin(GL_QUADS);
        glVertex2f(BOARD_OFFSET_X + hoverCellX * cellWidth, BOARD_OFFSET_Y
          + hoverCellY * cellWidth);
        glVertex2f(BOARD_OFFSET_X + (hoverCellX + 1) * cellWidth,
          BOARD_OFFSET_Y + hoverCellY * cellWidth);
        glVertex2f(BOARD_OFFSET_X + (hoverCellX + 1) * cellWidth,
          BOARD_OFFSET_Y + (hoverCellY + 1) * cellWidth);
        glVertex2f(BOARD_OFFSET_X + hoverCellX * cellWidth, BOARD_OFFSET_Y
          + (hoverCellY + 1) * cellWidth);
        glEnd();
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

    // All fonts must be rendered last!
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
      glVertex2f(WINDOW_WIDTH * 0.1f, WINDOW_HEIGHT * 0.10f);
      glVertex2f(WINDOW_WIDTH * 0.9f, WINDOW_HEIGHT * 0.10f);
      glVertex2f(WINDOW_WIDTH * 0.9f, WINDOW_HEIGHT * 0.90f);
      glVertex2f(WINDOW_WIDTH * 0.1f, WINDOW_HEIGHT * 0.90f);
      glEnd();

      helpFont.drawString(HELP_OFFSET_X + WINDOW_WIDTH * 0.1f, HELP_OFFSET_Y
        + WINDOW_HEIGHT * 0.10f, HELP_TEXT, Color.black);
      helpFont.drawString(HELP_OFFSET_X + WINDOW_WIDTH * 0.1f + 85,
        HELP_OFFSET_Y + WINDOW_HEIGHT * 0.10f, HELP_DESC, Color.black);
    } else {
      // Draw clue text
      for (Map.Entry<Integer, String> e : clueText.entrySet()) {
        clueFont.drawString(
          BOARD_OFFSET_X + CLUE_OFFSET_X + cellWidth * (e.getKey() % size),
          BOARD_OFFSET_Y + CLUE_OFFSET_Y + cellWidth * (e.getKey() / size),
          e.getValue(), Color.darkGray);
      }
      // Draw guess text and note text
      for (int i = 0; i < size; ++i) {
        for (int j = 0; j < size; ++j) {
          if (guessGrid.get(i * size + j) > 0) {
            guessFont.drawString(BOARD_OFFSET_X + j * cellWidth
              + guess_offset_x,
              BOARD_OFFSET_Y + i * cellWidth + guess_offset_y,
              Integer.toString(guessGrid.get(i * size + j)), Color.black);
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
      isUndo = false;
      switch (Keyboard.getEventKey()) {
        case Keyboard.KEY_ESCAPE:
          running = false;
          break;
        case Keyboard.KEY_1:
        case Keyboard.KEY_NUMPAD1:
          type(1);
          break;
        case Keyboard.KEY_2:
        case Keyboard.KEY_NUMPAD2:
          type(2);
          break;
        case Keyboard.KEY_3:
        case Keyboard.KEY_NUMPAD3:
          type(3);
          break;
        case Keyboard.KEY_4:
        case Keyboard.KEY_NUMPAD4:
          type(4);
          break;
        case Keyboard.KEY_5:
        case Keyboard.KEY_NUMPAD5:
          type(5);
          break;
        case Keyboard.KEY_6:
        case Keyboard.KEY_NUMPAD6:
          type(6);
          break;
        case Keyboard.KEY_7:
        case Keyboard.KEY_NUMPAD7:
          type(7);
          break;
        case Keyboard.KEY_8:
        case Keyboard.KEY_NUMPAD8:
          type(8);
          break;
        case Keyboard.KEY_9:
        case Keyboard.KEY_NUMPAD9:
          type(9);
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
          Display.setTitle("KenKen - Brute Force Solver took "
            + String.format("%.3f", bf.getElapsedTime() * 0.000000001)
            + " seconds");
          break;
        case Keyboard.KEY_F11:
          showHelp = false;
          DepthFirstSolver dfs = new DepthFirstSolver(this, problem);
          dfs.startTimer();
          dfs.solve();
          dfs.stopTimer();
          dfs.printElapsedTime();
          Display.setTitle("KenKen - DFS Solver took "
            + String.format("%.3f", dfs.getElapsedTime() * 0.000000001)
            + " seconds");
          break;
        case Keyboard.KEY_F12:
          modEnabled = !modEnabled;
          setNewProblem(size);
          break;
        case Keyboard.KEY_BACK:
          isUndo = true;
          if (toggleHistory.size() > 0) {
            inGuessMode = toggleHistory.pop();
            hoverCellX = hoverXHistory.pop();
            hoverCellY = hoverYHistory.pop();
            markCell(numHistory.pop());
          }
          break;
        default:
          inGuessMode = !inGuessMode;
          break;
      }
    }
  }

  private void markCell(int n) {
    boolean isRemoval;
    if (boardHovered()) {
      if (inGuessMode) {
        // Mark guess
        if (guessGrid.get(hoverCellY * size + hoverCellX) == n) {
          guessGrid.put(hoverCellY * size + hoverCellX, -1);
          isRemoval = true;
        } else {
          if (!isUndo && guessGrid.get(hoverCellY * size + hoverCellX) > 0) {
            boolean tmp1;
            int tmp2;

            tmp1 = toggleHistory.pop();
            toggleHistory.push(inGuessMode);
            toggleHistory.push(tmp1);

            tmp2 = numHistory.pop();
            numHistory.push(guessGrid.get(hoverCellY * size + hoverCellX));
            numHistory.push(tmp2);

            tmp2 = hoverXHistory.pop();
            hoverXHistory.push(hoverCellX);
            hoverXHistory.push(tmp2);

            tmp2 = hoverYHistory.pop();
            hoverYHistory.push(hoverCellY);
            hoverYHistory.push(tmp2);
          }

          guessGrid.put(hoverCellY * size + hoverCellX, n);

          // Return if board contains solution
          boolean boardComplete = true;
          int guess;
          HashSet<Integer> guessSet;
          for (int i = 0; i < size * size; ++i) {
            guess = guessGrid.get(i);
            if (guess < 1) {
              boardComplete = false;
              break;
            }
            guessSet = new HashSet<Integer>();
            guessSet.add(guess);
            attempt.put(i, guessSet);
          }
          if (boardComplete && problem.checkGrid(attempt)) {
            Display.setTitle("KenKen - Player solved in "
              + String.format("%.3f",
                (System.nanoTime() - startTime) * 0.000000001) + " seconds!");
            return;
          }

          isRemoval = false;
        }
      } else {
        // Mark note
        // TODO Decide what to do with this.. nice feature but breaks history
        if (!isUndo && guessGrid.get(hoverCellY * size + hoverCellX) > 0) {
          boolean tmp1;
          int tmp2;

          tmp1 = toggleHistory.pop();
          toggleHistory.push(true);
          toggleHistory.push(tmp1);

          tmp2 = numHistory.pop();
          numHistory.push(guessGrid.get(hoverCellY * size + hoverCellX));
          numHistory.push(tmp2);

          tmp2 = hoverXHistory.pop();
          hoverXHistory.push(hoverCellX);
          hoverXHistory.push(tmp2);

          tmp2 = hoverYHistory.pop();
          hoverYHistory.push(hoverCellY);
          hoverYHistory.push(tmp2);
        }
        guessGrid.put(hoverCellY * size + hoverCellX, -1);
        if (noteGrid.get(hoverCellY * size + hoverCellX).get(n - 1)) {
          noteGrid.get(hoverCellY * size + hoverCellX).set(n - 1, false);
          isRemoval = false;
        } else {
          noteGrid.get(hoverCellY * size + hoverCellX).set(n - 1, true);
          isRemoval = true;
        }
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
              incorrectGrid.put(
                j * size + row.lastIndexOf(Integer.valueOf(row.get(k))), true);
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

                incorrectGrid.put(col.lastIndexOf(Integer.valueOf(col.get(k)))
                  * size + i, true);
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
    }
  }

  private boolean boardHovered() {
    return hoverCellX >= 0 && hoverCellX < size && hoverCellY >= 0
      && hoverCellY < size;
  }

  private void type(int n) {
    if (n <= size) {
      numHistory.push(n);
      hoverXHistory.push(hoverCellX);
      hoverYHistory.push(hoverCellY);
      toggleHistory.push(inGuessMode);
      markCell(n);
    } else {
      inGuessMode = !inGuessMode;
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
