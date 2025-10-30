package skirental;

// Types of skis with price factor multiplier.
// CleanCode: explicit values, easy to read/extend.
public enum SkiType {
  CARVING(1.00), FREESTYLE(1.05), ALL_MOUNTAIN(1.07), RACE(1.15), TOURING(1.10);
  private final double priceFactor; SkiType(double f){ this.priceFactor=f; } public double factor(){ return priceFactor; }
}