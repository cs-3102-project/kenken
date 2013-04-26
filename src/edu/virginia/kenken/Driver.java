package edu.virginia.kenken;

public class Driver {

  /**
   * @param args
   */
  public static void main(String[] args) {
    Problem problem = new Problem(8);

    Solver solver = new Solver(problem);

    GUI gui = new GUI(problem);
    gui.gameLoop();
    solver.solveBruteForce();
    gui.destroy();
  }

}
