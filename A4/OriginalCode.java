import java.util.Set;
import java.util.ArrayList;

final public class AirlineSystem implements AirlineInterface {

  public boolean loadRoutes(String fileName) {
    return false;
  }

  public Set<String> retrieveCityNames() {
    return null;
  }

  public Set<Route> retrieveDirectRoutesFrom(String city)
    throws CityNotFoundException {
    return null;
  }

  public Set<ArrayList<String>> fewestStopsItinerary(String source,
    String destination) throws CityNotFoundException {
    return null;
  }

  public Set<ArrayList<Route>> shortestDistanceItinerary(String source,
    String destination) throws CityNotFoundException {
    return null;
  }

  public Set<ArrayList<Route>> shortestDistanceItinerary(String source,
    String transit, String destination) throws CityNotFoundException {
    return null;
  }

  public boolean addCity(String city){
    return false;
  }

  public boolean addRoute(String source, String destination, int distance,
    double price) throws CityNotFoundException {
    return false;
  }

  public boolean updateRoute(String source, String destination, int distance,
    double price) throws CityNotFoundException {
    return false;
  }
}
