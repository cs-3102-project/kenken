package edu.virginia.kenken;

public class Driver {

  /**
   * @param args
   */
  public static void main(String[] args) {
    Problem problem = new Problem(9);
    // DepthFirstSolver solver = new DepthFirstSolver(problem);
    // long startTime = System.nanoTime();
    // solver.solve();
    // long endTime = System.nanoTime();
    // System.out.println("Elapsed time: " + (endTime - startTime) * 0.000000001
    // + " seconds");
    BruteForceSolver solver = new BruteForceSolver(problem);
    solver.printSolution();
    GUI gui = new GUI(problem);
    gui.gameLoop();
    gui.destroy();
  }

}
