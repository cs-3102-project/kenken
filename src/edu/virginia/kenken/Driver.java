package edu.virginia.kenken;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;

public class Driver extends Applet {
  public static boolean running = false;
  public static String fontPath;

  private static final long serialVersionUID = 1L;
  private static final String APPLET_FONT_PATH = "DroidSans.ttf";
  private static final String APP_FONT_PATH = "res/DroidSans.ttf";

  private Canvas display_parent;
  private GUI gui;

  private Thread gameThread;

  public void startLWJGL() {
    gameThread = new Thread() {
      @Override
      public void run() {
        startGame();
      }
    };
    gameThread.start();
  }

  private void stopLWJGL() {
    running = false;
    try {
      gameThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void start() {

  }

  @Override
  public void stop() {

  }

  @Override
  public void destroy() {
    remove(display_parent);
    super.destroy();
  }

  @Override
  public void init() {
    setLayout(new BorderLayout());
    Driver.fontPath = APPLET_FONT_PATH;
    try {
      display_parent = new Canvas() {
        private static final long serialVersionUID = 1L;

        @Override
        public final void addNotify() {
          super.addNotify();
          startLWJGL();
        }

        @Override
        public final void removeNotify() {
          stopLWJGL();
          super.removeNotify();
        }
      };
      display_parent.setSize(getWidth(), getHeight());
      add(display_parent);
      display_parent.setFocusable(true);
      display_parent.requestFocus();
      display_parent.setIgnoreRepaint(true);
      setVisible(true);
    } catch (Exception e) {
      System.err.println(e);
      throw new RuntimeException("Unable to create display");
    }
  }

  private void startGame() {
    running = true;
    gui = new GUI(6);
    gui.gameLoop();
    gui.destroy();
  }

  public String getFontPath() {
    return fontPath;
  }

  public static void main(String args[]) {
    Driver.fontPath = APP_FONT_PATH;
    Driver driver = new Driver();
    driver.startGame();
  }

}
