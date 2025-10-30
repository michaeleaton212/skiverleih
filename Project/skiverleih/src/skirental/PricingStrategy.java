package skirental;

// Strategy interface for pricing calculation.
// CleanCode: small explicit contract, easy to swap strategies.
public interface PricingStrategy { double priceFor(Ski ski, int days); }