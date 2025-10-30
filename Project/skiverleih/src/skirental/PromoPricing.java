package skirental;

// Pattern: Strategy (concrete + decorator-like) - `PromoPricing` is another concrete strategy that
// reuses `DefaultPricing` and applies a discount. It's effectively a simple decorator over the default algorithm.
// Reason: Keeps promo logic separate and composable without modifying `DefaultPricing` or the service layer.

// Pricing with a percentage discount applied to DefaultPricing.
// CleanCode: validate inputs, delegate to DefaultPricing.
public class PromoPricing implements PricingStrategy {
  private final double discount;

  public PromoPricing(double discount){
    if(discount<0||discount>=1)
      throw new AppException("Rabatt 0..1 erwartet.");

    this.discount=discount;
  }

  @Override
  public double priceFor(Ski ski,int days){
    double base=new DefaultPricing().priceFor(ski,days);

    return Math.round(base*(1.0-discount)*100.0)/100.0;
  }
}