package edu.virginia.kenken;

public class Driver {

  /**
   * @param args
   */
  public static void main(String[] args) {
    Problem problem = new Problem(7);
    Solver solver = new Solver(problem);
    solver.solveBruteForce();
    GUI gui = new GUI(problem);
    gui.gameLoop();
    gui.destroy();
  }

}
