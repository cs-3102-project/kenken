package edu.virginia.kenken;

public abstract class Solver {
  private final GUI gui;
  private final Problem problem;
  private long startTime;
  private long endTime;
  private long elapsedTime;

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

  public void startTimer() {
    startTime = System.nanoTime();
  }

  public void stopTimer() {
    endTime = System.nanoTime();
    elapsedTime = endTime - startTime;
  }

  public void printElapsedTime() {
    System.out.println("Elapsed time: " + elapsedTime * 0.000000001
      + " seconds");
  }

  public long getElapsedTime() {
    return elapsedTime;
  }

}
