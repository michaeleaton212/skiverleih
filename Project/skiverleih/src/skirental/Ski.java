package skirental;

// Pattern: Factory Method (static factory) - `Ski.of(...)` is a named constructor used instead of public constructors.
// Reason: Provides a clearer creation API and sets default availability to true in a single place.

// Domain object for a ski item.
// CleanCode: factory method, validate on creation, small getters.
public class Ski implements Identifiable, Validatable {
  private String id; private SkiType type; private String brand; private int lengthCm; private double dailyPrice; private boolean available;
  public static Ski of(String id,SkiType type,String brand,int lengthCm,double dailyPrice){ return new Ski(id,type,brand,lengthCm,dailyPrice,true); }
  public Ski(){}
  public Ski(String id,SkiType type,String brand,int lengthCm,double dailyPrice,boolean available){
    this.id=id; this.type=type; this.brand=brand; this.lengthCm=lengthCm; this.dailyPrice=dailyPrice; this.available=available; validate();
  }
  @Override public String getId(){ return id; } public SkiType getType(){ return type; } public String getBrand(){ return brand; }
  public int getLengthCm(){ return lengthCm; } public double getDailyPrice(){ return dailyPrice; } public boolean isAvailable(){ return available; }
  public void setAvailable(boolean a){ this.available=a; }

  // Validate domain invariants.
  @Override public void validate(){
    if(id==null||id.isBlank()) throw new AppException("Ski-ID fehlt.");
    if(type==null) throw new AppException("Ski-Typ fehlt.");
    if(brand==null||brand.isBlank()) throw new AppException("Marke fehlt.");
    if(lengthCm<=0) throw new AppException("Länge muss > 0 sein.");
    if(dailyPrice<=0) throw new AppException("Tagespreis muss > 0 sein.");
  }
  @Override public String toString(){
    return String.format("%s | %s %s %dcm CHF %.2f | %s", id, type, brand, lengthCm, dailyPrice, available?"frei":"verliehen");
  }
}