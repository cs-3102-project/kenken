package edu.virginia.kenken;

public class AdditionCage extends Cage {
  public AdditionCage(Cage src) {
    super(src);
    int sum = 0;
    for (Integer d : getCells()) {
      sum += d;
    }
    setTotal(sum);
  }
}
