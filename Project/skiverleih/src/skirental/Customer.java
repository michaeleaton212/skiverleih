package skirental;

// Represents a customer in the system.
// CleanCode: validate on construction, small methods, clear errors.
public class Customer implements Identifiable, Validatable {
  private String id; private String name; private int age; private String email;
  public Customer(){}
  public Customer(String id,String name,int age,String email){ this.id=id; this.name=name; this.age=age; this.email=email; validate(); }
  @Override public String getId(){ return id; } public String getName(){ return name; } public int getAge(){ return age; } public String getEmail(){ return email; }

  // Validate invariants early — fail fast.
  @Override public void validate(){
    if(id==null||id.isBlank()) throw new AppException("Kunden-ID fehlt.");
    if(name==null||name.isBlank()) throw new AppException("Name fehlt.");
    if(age<=0) throw new AppException("Alter muss > 0 sein.");
    if(email==null||!email.contains("@")) throw new AppException("Ungültige E-Mail.");
  }

  @Override public String toString(){ return String.format("%s | %s (%d) | %s", id, name, age, email); }
}