package skirental;

import java.util.List;
import java.util.stream.Collectors;

// Service layer: orchestrates datastore + pricing for business operations.
// CleanCode: small API, clear method responsibilities, throws AppException for domain errors.
public class RentalService {
  private final DataStore db = DataStore.get();
  private final PricingStrategy pricing;

  public RentalService(PricingStrategy pricing) {
    this.pricing = pricing;
  }

  // Add a new customer (fail if id exists).
  public void addCustomer(String id, String name, int age, String email){
    if (db.customers().exists(id)) throw new AppException("Kunden-ID existiert bereits.");
    db.customers().put(new Customer(id, name, age, email));
  }

  // List customers.
  public List<Customer> listCustomers(){ return db.customers().all(); }

  // Add a new ski (fail if id exists).
  public void addSki(String id, SkiType type, String brand, int length, double price){
    if (db.skis().exists(id)) throw new AppException("Ski-ID existiert bereits.");
    db.skis().put(Ski.of(id, type, brand, length, price));
  }

  // List all skis.
  public List<Ski> listSkis(){ return db.skis().all(); }

  // List only available skis.
  public List<Ski> listAvailable(){
    return db.skis().all().stream().filter(Ski::isAvailable).collect(Collectors.toList());
  }

  // Price quote for given ski and days.
  public double quote(String skiId, int days){
    Ski s = db.skis().get(skiId);
    return pricing.priceFor(s, days);
  }

  // Rent a ski: create Rental, mark ski unavailable, generate invoice HTML.
  public Rental rent(String customerId, String skiId, int days){
    Customer c = db.customers().get(customerId);
    Ski s = db.skis().get(skiId);
    if (!s.isAvailable()) throw new AppException("Ski ist nicht verfügbar.");

    double price = pricing.priceFor(s, days);
    String rid = db.nextRentalId();

    Rental r = new Rental(rid, c.getId(), s.getId(), days, price, RentalStatus.OPEN);
    db.rentals().put(r);
    s.setAvailable(false);

    // Render invoice HTML (template utility) — side-effect free here.
    String html = HtmlTemplate.invoice(c, s, r);

    // If you send or save the HTML, use `html`.
    // System.out.println(html);

    return r;
  }

  // Mark rental as returned and free the ski.
  public void returnRental(String rentalId){
    Rental r = db.rentals().get(rentalId);
    if (r.getStatus() == RentalStatus.RETURNED) throw new AppException("Bereits retourniert.");
    r.setStatus(RentalStatus.RETURNED);
    db.skis().get(r.getSkiId()).setAvailable(true);
  }

  public List<Rental> listRentals(){ return db.rentals().all(); }
  public void save(){ db.save(); }
  public void load(){ db.load(); }
}
