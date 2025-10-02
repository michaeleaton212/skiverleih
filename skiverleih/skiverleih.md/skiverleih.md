# Projekt: Ski-Verleih (M4 – Einzelarbeit)

## Kurzbeschreibung
Dieses Projekt ist eine konsolenbasierte Java-Applikation zur Verwaltung eines Ski-Verleihs.  
Die Daten (Kunden, Ski, Rentals) werden beim Start aus einer **JSON-Datei geladen** und beim Beenden bzw. auf Befehl wieder gespeichert.  
Bei **Statusänderungen** (Miete oder Rückgabe) werden automatische **E-Mail-Benachrichtigungen** verschickt.  

---

## Ziele & Lernziele
- Ich kann ein **grobes UML-Klassendiagramm** für meine Aufgabenstellung erstellen.  
- Ich kann eine **Vererbungshierarchie** und **Interfaces** einsetzen.  
- Ich kann einen Use Case als **Sequenzdiagramm** darstellen.  
- Nach Projektabschluss kann ich ein **detailliertes UML-Klassendiagramm** und Sequenzdiagramm erstellen.  
- Ich setze **OO-Konzepte** (Delegation, Polymorphismus) und **Design Patterns** (z. B. Strategy, Repository) ein.  
- Ich dokumentiere mein Projekt professionell und halte **Clean-Code-Regeln** ein.  

---

## Anforderungen

### Fachlich
- **Kundenverwaltung**: Kunden mit ID, Name, Alter und E-Mail verwalten.  
- **Ski-Bestand**: Verschiedene Ski-Typen (Carving, Freestyle, AllMountain, Race, Touring) mit Marke, Länge, Tagespreis und Verfügbarkeit erfassen.  
- **Verleih**:  
  - Verfügbarkeit prüfen  
  - Preisangebot für eine Mietdauer berechnen  
  - Ski an Kunden verleihen → neuer Rental mit Status *OPEN*  
  - Rückgabe verbuchen → Status auf *RETURNED* setzen, Ski wieder verfügbar  
- **E-Mail-Benachrichtigung**:  
  - Beim **Mieten**: Bestätigung mit Details (Ski, Dauer, Preis, Rückgabedatum)  
  - Bei **Rückgabe**: Bestätigung über erfolgte Rückgabe  
- **Persistenz (JSON)**:  
  - Beim Start werden alle Daten geladen  
  - Beim Beenden/Save werden Änderungen wieder in JSON gespeichert  
- **Validierung**: Falsche Eingaben (z. B. ungültige ID, Tage ≤ 0, fehlende E-Mail) führen zu einer Fehlermeldung.  

### Technisch
- Sprache: **Java 21 (JVM)**, Konsolenbasiert.  
- Klare Trennung von **UI**, **Logik**, **Persistenz**.  
- **OOP**: Vererbung (`Ski` abstrakt → 5 konkrete Typen), Interfaces (`PricingStrategy`, `NotificationService`).  
- **Design Patterns**:  
  - Repository (JSON-Persistenz)  
  - Strategy (Preisberechnung)  
  - optional Factory (Erzeugung von Ski aus JSON)  
- Anwendung von **Clean-Code-Regeln**.  

---

## UML Klassendiagramm (Basis)

```mermaid
classDiagram
  direction LR

  class Customer {
    +id: String
    +name: String
    +age: int
    +email: String
  }

  class Ski {
    <<abstract>>
    +id: String
    +brand: String
    +size: int
    +dailyRate: double
    +available: boolean
  }

  class CarvingSki
  class FreestyleSki
  class AllMountainSki
  class RaceSki
  class TouringSki

  class Rental {
    +id: String
    +customer: Customer
    +ski: Ski
    +start: LocalDate
    +end: LocalDate
    +priceTotal: double
    +status: RentalStatus
  }

  class RentalStatus {
    <<enum>>
    OPEN
    RETURNED
  }

  class PricingStrategy {
    <<interface>>
    +calculate(ski:Ski, days:int, c:Customer): double
  }
  class StandardPricing
  class YouthPricing

  class RentalService {
    +quoteByIds(skiId, custId, days): double
    +rentByIds(skiId, custId, start, days): Rental
    +returnRental(rentalId, returnDate): void
  }

  class NotificationService {
    <<interface>>
    +send(to:String, subject:String, body:String): void
  }
  class EmailNotificationService

  %% Beziehungen
  Customer "1" --> "many" Rental : hat >
  Rental "1" --> "1" Ski : leiht >
  Ski <|-- CarvingSki
  Ski <|-- FreestyleSki
  Ski <|-- AllMountainSki
  Ski <|-- RaceSki
  Ski <|-- TouringSki
  PricingStrategy <|.. StandardPricing
  PricingStrategy <|.. YouthPricing
  RentalService --> PricingStrategy
  RentalService --> NotificationService : sendMailOnStatusChange
