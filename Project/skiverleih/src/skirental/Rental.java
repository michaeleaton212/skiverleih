package skirental;

// Rental record linking customer + ski + duration + price + status.
// CleanCode: validate on creation, immutable-ish fields where sensible.
public class Rental implements Identifiable, Validatable {
  private String id; private String customerId; private String skiId; private int days; private double totalPrice; private RentalStatus status;
  public Rental(){} public Rental(String id,String customerId,String skiId,int days,double totalPrice,RentalStatus status){
    this.id=id; this.customerId=customerId; this.skiId=skiId; this.days=days; this.totalPrice=totalPrice; this.status=status; validate();
  }
  @Override public String getId(){ return id; } public String getCustomerId(){ return customerId; } public String getSkiId(){ return skiId; }
  public int getDays(){ return days; } public double getTotalPrice(){ return totalPrice; } public RentalStatus getStatus(){ return status; } public void setStatus(RentalStatus s){ this.status=s; }

  // Validate invariants early — fail fast.
  @Override public void validate(){
    if(id==null||id.isBlank()) throw new AppException("Rental-ID fehlt.");
    if(customerId==null||customerId.isBlank()) throw new AppException("Kunde fehlt.");
    if(skiId==null||skiId.isBlank()) throw new AppException("Ski fehlt.");
    if(days<=0) throw new AppException("Tage müssen > 0 sein.");
    if(totalPrice<=0) throw new AppException("Preis muss > 0 sein.");
    if(status==null) throw new AppException("Status fehlt.");
  }
  @Override public String toString(){
    return String.format("%s | Kunde=%s Ski=%s %dd | CHF %.2f | %s", id, customerId, skiId, days, totalPrice, status);
  }
}