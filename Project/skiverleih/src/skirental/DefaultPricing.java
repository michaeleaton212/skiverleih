package skirental;

// Default pricing strategy: base price * days * type factor, with multi-day discounts.
// CleanCode: single responsibility, readable thresholds, round final price.
public class DefaultPricing implements PricingStrategy {
  @Override public double priceFor(Ski ski,int days){
    if(days<=0) throw new AppException("Tage müssen > 0 sein.");
    double base = ski.getDailyPrice()*days*ski.getType().factor();
    if(days>=4) base*=0.95; if(days>=7) base*=0.90;
    return Math.round(base*100.0)/100.0;
  }
}