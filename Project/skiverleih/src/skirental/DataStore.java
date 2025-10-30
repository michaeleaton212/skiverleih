package skirental;
import com.google.gson.*; import com.google.gson.reflect.TypeToken;
import java.io.*; import java.lang.reflect.Type; import java.nio.charset.StandardCharsets; import java.util.*;

// In-memory stores with JSON persistence to data.json.
// CleanCode: single instance (singleton), small clear methods, explicit error messages.
public class DataStore {
  private static final DataStore INSTANCE = new DataStore(); public static DataStore get(){ return INSTANCE; }
  private final Store<Customer> customers=new Store<>(); private final Store<Ski> skis=new Store<>(); private final Store<Rental> rentals=new Store<>();
  private final Gson gson=new GsonBuilder().setPrettyPrinting().create(); private final String file="data.json";
  private DataStore(){}
  public Store<Customer> customers(){ return customers; } public Store<Ski> skis(){ return skis; } public Store<Rental> rentals(){ return rentals; }

  // Load data.json if present — keep structure tolerant.
  public synchronized void load(){
    File f=new File(file); if(!f.exists()){ save(); return; }
    try(Reader r=new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)){
      Type mapType=new TypeToken<Map<String,Object>>(){}.getType(); Map<String,Object> root=new Gson().fromJson(r,mapType); if(root==null) return;
      customers.clear(); skis.clear(); rentals.clear();
      String cJson=gson.toJson(root.getOrDefault("customers", java.util.List.of()));
      java.util.List<Customer> cl=gson.fromJson(cJson,new TypeToken<java.util.List<Customer>>(){}.getType()); for(Customer c:cl) customers.put(c);
      String sJson=gson.toJson(root.getOrDefault("skis", java.util.List.of()));
      java.util.List<Ski> sl=gson.fromJson(sJson,new TypeToken<java.util.List<Ski>>(){}.getType()); for(Ski s:sl) skis.put(s);
      String rJson=gson.toJson(root.getOrDefault("rentals", java.util.List.of()));
      java.util.List<Rental> rl=gson.fromJson(rJson,new TypeToken<java.util.List<Rental>>(){}.getType()); for(Rental rr:rl) rentals.put(rr);
    } catch(IOException e){ throw new AppException("Konnte Daten nicht laden: "+e.getMessage()); }
  }

  // Persist current state to data.json — atomic write not necessary for demo.
  public synchronized void save(){
    Map<String,Object> root=new LinkedHashMap<>(); root.put("customers",customers.all()); root.put("skis",skis.all()); root.put("rentals",rentals.all());
    try(Writer w=new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)){ gson.toJson(root,w); }
    catch(IOException e){ throw new AppException("Konnte Daten nicht speichern: "+e.getMessage()); }
  }

  // Generate next Rental ID (R1, R2, ...). Simple scan — OK for small datasets.
  public String nextRentalId(){ int max=0; for(Rental r:rentals.all()){ String id=r.getId(); if(id!=null&&id.startsWith("R")) try{ max=Math.max(max,Integer.parseInt(id.substring(1))); }catch(Exception ignored){} } return "R"+(max+1); }
}