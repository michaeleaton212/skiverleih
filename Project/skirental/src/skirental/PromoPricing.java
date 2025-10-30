// ...existing code...
package skirental;

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

// ...existing code...
package skirental;

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

