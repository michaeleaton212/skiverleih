package skirental;

// Pattern: Strategy - defines a pricing algorithm interface so implementations can be swapped at runtime.
// Reason: Allows `RentalService` to use different pricing logic (e.g. default vs promo) without changing its code.

// Strategy interface for pricing calculation.
// CleanCode: small explicit contract, easy to swap strategies.
public interface PricingStrategy { double priceFor(Ski ski, int days); }