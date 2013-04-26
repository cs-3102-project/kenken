package edu.virginia.kenken;

public class Driver {

  /**
   * @param args
   */
  public static void main(String[] args) {
    Problem problem = new Problem(9);
    Solver solver = new Solver(problem);
    solver.solve();
    GUI gui = new GUI(problem);
    gui.gameLoop();
    gui.destroy();
  }

}
