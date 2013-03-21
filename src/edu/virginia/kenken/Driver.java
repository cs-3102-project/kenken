package edu.virginia.kenken;

public class Driver {

  /**
   * @param args
   */
  public static void main(String[] args) {
    GUI gui = new GUI(new Problem(9));
    gui.loop();
    gui.destroy();
  }

}
