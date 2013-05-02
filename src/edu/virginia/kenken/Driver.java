package edu.virginia.kenken;

/**
 * @author artnc
 * @author scteps
 * 
 */
public class Driver {

  public static void main(String[] args) {
    GUI gui = new GUI(6);
    gui.gameLoop();
    gui.destroy();
  }

}
