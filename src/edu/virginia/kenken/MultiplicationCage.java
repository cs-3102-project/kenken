package edu.virginia.kenken;

public class MultiplicationCage extends Cage {
  public MultiplicationCage(Cage src) {
    super(src);
    int product = 1;
    for (Integer d : getCells()) {
      product *= d;
    }
    setTotal(product);
  }
}
