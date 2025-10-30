package skirental;
import java.util.*;

// Simple in-memory keyed store for Identifiable objects.
// CleanCode: small API, clear errors, immutable list copy on all().
public class Store<T extends Identifiable> {
  private final Map<String,T> data = new LinkedHashMap<>();
  public void put(T v){ if(v==null||v.getId()==null||v.getId().isBlank()) throw new AppException("Ungültige ID."); data.put(v.getId(),v); }
  public T get(String id){ T v=data.get(id); if(v==null) throw new AppException("ID nicht gefunden: "+id); return v; }
  public boolean exists(String id){ return data.containsKey(id); }
  public java.util.List<T> all(){ return new ArrayList<>(data.values()); }
  public void remove(String id){ data.remove(id); }
  public int size(){ return data.size(); }
  public void clear(){ data.clear(); }
}