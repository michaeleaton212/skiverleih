package skirental;

import java.util.Locale;
import java.util.Scanner;

// CLI entrypoint for the ski rental app.
// CleanCode: thin UI layer delegating to service; small helper methods for parsing and handling commands.
public class Main {

  // Command constants (avoid magic strings)
  private static final String CMD_HELP = "help";
  private static final String CMD_CUSTOMERS = "customers";
  private static final String CMD_ADD_CUSTOMER = "add-customer";
  private static final String CMD_SKIS = "skis";
  private static final String CMD_AVAILABLE = "available";
  private static final String CMD_ADD_SKI = "add-ski";
  private static final String CMD_QUOTE = "quote";
  private static final String CMD_RENT = "rent";
  private static final String CMD_RETURN = "return-rental";
  private static final String CMD_RENTALS = "rentals";
  private static final String CMD_PRICING = "pricing";
  private static final String CMD_SAVE = "save";
  private static final String CMD_LOAD = "load";
  private static final String CMD_EXIT = "exit";
  private static final String CMD_QUIT = "quit";

  // Help text centralized to avoid duplication
  private static final String HELP_TEXT = """
            Befehle:
              help                               Zeigt diese Hilfe
              customers                          Liste Kunden
              add-customer <id> <name> <age> <email>

              skis                               Liste alle Ski
              available                          Liste verfügbare Ski
              add-ski <id> <type> <brand> <lengthCm> <price>
                Typen: CARVING, FREESTYLE, ALL_MOUNTAIN, RACE, TOURING

              quote <skiId> <days>               Preisangebot
              rent <customerId> <skiId> <days>   Verleihen (Status OPEN, Rechnung per E-Mail)
              return-rental <rentalId>           Rückgabe (Status RETURNED)
              rentals                            Liste aller Rentals

              pricing default                    Nutze DefaultPricing
              pricing promo <0..1>               Nutze PromoPricing mit Rabatt

              save                               In data.json speichern
              load                               Aus data.json laden
              exit                               Speichern & Beenden
            """;

  // Print help text to the console.
  private static void printHelp() { System.out.println(HELP_TEXT); }

  // Parse SkiType from user input (normalizes common variants).
  private static SkiType parseType(String s) {
    String k = s.toUpperCase(Locale.ROOT).replace("-", "_");
    if (k.equals("ALLMOUNTAIN")) k = "ALL_MOUNTAIN";
    return SkiType.valueOf(k);
  }

  // Small helper to parse ints with domain error message.
  private static int parseInt(String s, String msg) {
    try { return Integer.parseInt(s); }
    catch (NumberFormatException e) { throw new AppException(msg); }
  }

  // Small helper to parse doubles with domain error message.
  private static double parseDouble(String s, String msg) {
    try { return Double.parseDouble(s); }
    catch (NumberFormatException e) { throw new AppException(msg); }
  }

  public static void main(String[] args) {
    Locale.setDefault(Locale.GERMANY);

    // Initialize services (no global mailer passed to service - service handles invoice generation).
    PricingStrategy strategy = new DefaultPricing();
    RentalService service = new RentalService(strategy); // UI -> Service
    service.load();

    // Seed some data if empty (demo convenience).
    if (service.listCustomers().isEmpty())
      service.addCustomer("C1", "Max Muster", 22, "max@example.com");
    if (service.listSkis().isEmpty()) {
      service.addSki("S1", SkiType.CARVING, "Atomic", 170, 35.0);
      service.addSki("S2", SkiType.RACE, "Head", 175, 49.0);
    }

    System.out.println("Ski-Verleih CLI. Tippe 'help' für Befehle.");
    Scanner in = new Scanner(System.in);

    while (true) {
      System.out.print("> ");
      if (!in.hasNextLine()) break;
      String line = in.nextLine().trim();
      if (line.isBlank()) continue;

      String[] t = line.split("\\s+");
      String cmd = t[0].toLowerCase(Locale.ROOT);

      try {
        switch (cmd) {
          case CMD_HELP -> printHelp();
          case CMD_CUSTOMERS -> service.listCustomers().forEach(System.out::println);
          case CMD_ADD_CUSTOMER -> handleAddCustomer(service, t);
          case CMD_SKIS -> service.listSkis().forEach(System.out::println);
          case CMD_AVAILABLE -> service.listAvailable().forEach(System.out::println);
          case CMD_ADD_SKI -> handleAddSki(service, t);
          case CMD_QUOTE -> handleQuote(service, t);
          case CMD_RENT -> handleRent(service, t);
          case CMD_RETURN -> handleReturn(service, t);
          case CMD_RENTALS -> service.listRentals().forEach(System.out::println);
          case CMD_PRICING -> service = handlePricing(service, t);
          case CMD_SAVE -> { service.save(); System.out.println("Gespeichert in data.json"); }
          case CMD_LOAD -> { service.load(); System.out.println("Geladen."); }
          case CMD_EXIT, CMD_QUIT -> { service.save(); System.out.println("Bye."); return; }
          default -> System.out.println("Unbekannter Befehl. 'help' eingeben.");
        }
      } catch (AppException | IllegalArgumentException ex) {
        System.out.println("Fehler: " + ex.getMessage());
      }
    }
  }

  // ---- Command handlers (small, focused methods -> SRP) ----

  private static void handleAddCustomer(RentalService service, String[] t) {
    if (t.length < 5) throw new AppException("Syntax: add-customer <id> <name> <age> <email>");
    service.addCustomer(t[1], t[2], parseInt(t[3], "Alter muss eine Zahl sein."), t[4]);
    System.out.println("Kunde angelegt.");
  }

  private static void handleAddSki(RentalService service, String[] t) {
    if (t.length < 6) throw new AppException("Syntax: add-ski <id> <type> <brand> <lengthCm> <price>");
    service.addSki(
            t[1],
            parseType(t[2]),
            t[3],
            parseInt(t[4], "Länge muss Zahl sein."),
            parseDouble(t[5], "Preis muss Zahl sein."));
    System.out.println("Ski angelegt.");
  }

  private static void handleQuote(RentalService service, String[] t) {
    if (t.length < 3) throw new AppException("Syntax: quote <skiId> <days>");
    double price = service.quote(t[1], parseInt(t[2], "Tage muss Zahl sein."));
    System.out.printf("Preis: CHF %.2f%n", price);
  }

  private static void handleRent(RentalService service, String[] t) {
    if (t.length < 4) throw new AppException("Syntax: rent <customerId> <skiId> <days>");
    var r = service.rent(t[1], t[2], parseInt(t[3], "Tage muss Zahl sein."));
    // Invoice is generated by service via template; currently not sent automatically.
    System.out.println("Vermietet: " + r + "  (Rechnung generiert)");
  }

  private static void handleReturn(RentalService service, String[] t) {
    if (t.length < 2) throw new AppException("Syntax: return-rental <rentalId>");
    service.returnRental(t[1]);
    System.out.println("Retourniert: " + t[1]);
  }

  private static RentalService handlePricing(RentalService service, String[] t) {
    if (t.length < 2) throw new AppException("Syntax: pricing <default|promo d>");
    PricingStrategy strategy;
    if (t[1].equalsIgnoreCase("default")) {
      strategy = new DefaultPricing();
    } else if (t[1].equalsIgnoreCase("promo")) {
      if (t.length < 3) throw new AppException("Syntax: pricing promo <0..1>");
      strategy = new PromoPricing(parseDouble(t[2], "Rabatt muss Zahl 0..1 sein."));
    } else throw new AppException("Unbekannte Pricing-Option.");

    // new strategy – rewire service (keep previous persistence state)
    RentalService newService = new RentalService(strategy);
    newService.load();
    System.out.println("Pricing gesetzt: " + strategy.getClass().getSimpleName());
    return newService;
  }
}
