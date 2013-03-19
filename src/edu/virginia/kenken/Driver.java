package edu.virginia.kenken;

public class Driver {

  /**
   * @param args
   */
  public static void main(String[] args) {
    GUI gui = new GUI();
    gui.drawProblem(new Problem(9)); // TODO Call this from a GUI event instead
    gui.loop();
    gui.destroy();
  }

}
