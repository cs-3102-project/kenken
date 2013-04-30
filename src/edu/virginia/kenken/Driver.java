package edu.virginia.kenken;

public class Driver {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // long startTime = System.nanoTime();
    // solver.solve();
    // long endTime = System.nanoTime();
    // System.out.println("Elapsed time: " + (endTime - startTime) * 0.000000001
    // + " seconds");
    // solver.printSolution();
    GUI gui = new GUI(9);
    gui.gameLoop();
    gui.destroy();
  }

}
