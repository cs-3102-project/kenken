package edu.virginia.kenken;

public class Solver {
  private final GUI gui;
  private final Problem problem;

  public Solver(GUI gui, Problem problem) {
    this.gui = gui;
    this.problem = problem;
  }

  public GUI getGUI() {
    return gui;
  }

  public Problem getProblem() {
    return problem;
  }

}
