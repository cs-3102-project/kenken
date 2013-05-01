package edu.virginia.kenken;

public class Driver {

  public static void main(String[] args) {
    GUI gui = new GUI(6);
    gui.gameLoop();
    gui.destroy();
  }

}
