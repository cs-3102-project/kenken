package edu.virginia.kenken;

public class Driver {

  /**
   * @param args
   */
  public static void main(String[] args) {
    Problem problem = new Problem(8);
    DepthFirstSolver solver = new DepthFirstSolver(problem);
    solver.solve();
    // solver.solveBruteForce();
    GUI gui = new GUI(problem);
    gui.gameLoop();
    gui.destroy();
  }

}
