import java.util.Set;
import java.util.*;
import java.io.*;
import java.util.ArrayList;

final public class AirlineSystem implements AirlineInterface {
  // make a route set
  private Map<Integer, String> cityNames; // indexes are all off
  private Set<Route> routes;
  private Digraph G = null;
  private static final int INFINITY = Integer.MAX_VALUE;


  public boolean loadRoutes(String fileName){
  try{
      File file = new File(fileName);
      Scanner fileScan = new Scanner(file);
      int v = Integer.parseInt(fileScan.nextLine());
      G = new Digraph(v);
      cityNames = new HashMap<>();
      for(int i=0; i<v; i++){
          cityNames.put(i, fileScan.nextLine());
      }
      while(fileScan.hasNext()){
        int from = fileScan.nextInt();
        int to = fileScan.nextInt();
        int weightDistance = fileScan.nextInt();
        double weightPrice = fileScan.nextDouble();
        G.addEdge(new Route(cityNames.get(from-1), cityNames.get(to-1), weightDistance, weightPrice));
        G.addEdge(new Route(cityNames.get(to-1),cityNames.get(from-1), weightDistance, weightPrice));
      }
      fileScan.close();
      return true;
    }catch(FileNotFoundException e){
      System.out.println("File Not Found");
      return false;
   }
  }

  public Set<String> retrieveCityNames() {
    Set<String> cities = new HashSet<String>();
    for(int i = 0; i < cityNames.size(); i++){
      cities.add(cityNames.get(i));
    }
    return cities;
  }

  public Map<Integer, String> retrieveCityMap(){
    return cityNames;
  }

  public Set<Route> retrieveDirectRoutesFrom(String city) throws CityNotFoundException {
    if(!retrieveCityNames().contains(city)){
      return null;
      //  throw new CityNotFoundException();
    }


    Set<Route> r = new HashSet<Route>();
    for (Route e : G.adj(indexOf(city))){
      r.add(new Route(e.source, e.destination, e.distance, e.price));
    }



    return r;

  }

//

  public Set<ArrayList<String>> fewestStopsItinerary(String source,
    String destination) throws CityNotFoundException {
      if(!retrieveCityNames().contains(source) || !retrieveCityNames().contains(destination)){
        return null;
        //throw new CityNotFoundException;
      }

      Set <ArrayList<String>> fewestStops = new HashSet<ArrayList<String>>();
      ArrayList<String> shortest = new ArrayList<String>();
      int s = indexOf(source);
      int d = indexOf(destination);
      G.bfs(s);
      Stack<Integer> st = new Stack();
      int i;
      for(i = d; i != s; i = G.edgeTo[i]){
        st.push(i);
      }

      st.push(i);
      while(!st.empty()){
        shortest.add(cityNames.get(st.pop()));
        fewestStops.add(shortest);
      }

    return fewestStops;


  }

  public Set<ArrayList<Route>> shortestDistanceItinerary(String source,
    String destination) throws CityNotFoundException {
    if(!retrieveCityNames().contains(source) || !retrieveCityNames().contains(destination)){
        return null;
        //throw new CityNotFoundException;
      }
        Set <ArrayList<Route>> shortestDistance = new HashSet<ArrayList<Route>>();
        ArrayList<Route> shortest = new ArrayList<Route>();
        int s = indexOf(source);
        int d = indexOf(destination);
        G.dijkstras(s, d);
        Stack<Route> path = new Stack<>();
        for (int x = d; x != s; x = G.edgeTo[x]){
            Route e = G.findRoute(cityNames.get(x), cityNames.get(G.edgeTo[x]));
            path.push(e);
          }


          while(!path.empty()){
            Route r = path.pop();
            shortest.add(r);
            shortestDistance.add(shortest);
          }

          return shortestDistance;
  }

  public Set<ArrayList<Route>> shortestDistanceItinerary(String source,
    String transit, String destination) throws CityNotFoundException {
      Set <ArrayList<Route>> shortestDistance = new HashSet<ArrayList<Route>>();
     if(!retrieveCityNames().contains(source) || !retrieveCityNames().contains(destination) || !retrieveCityNames().contains(transit)){
          return shortestDistance;
          //throw new CityNotFoundException;
        }
        ArrayList<Route> shortest = new ArrayList<Route>();
        int from = indexOf(source);
        int t = indexOf(transit);
        G.dijkstras(from, t);
        Stack<Route> path = new Stack<>();
        for (int x = t; x != from; x = G.edgeTo[x]){
            Route e = G.findRoute(cityNames.get(x), cityNames.get(G.edgeTo[x]));
            path.push(e);
          }


          while(!path.empty()){
            Route r = path.pop();
            shortest.add(r);
            shortestDistance.add(shortest);
          }

          ArrayList<Route> sh = new ArrayList<Route>();
          int to = indexOf(destination);
          for (int x = to; x != t; x = G.edgeTo[x]){
              Route e = G.findRoute(cityNames.get(x), cityNames.get(G.edgeTo[x]));
              path.push(e);
            }

            while(!path.empty()){
              Route r = path.pop();
              sh.add(r);
              shortestDistance.add(sh);
            }


          return shortestDistance;

  }


  public boolean addCity(String city){
    if(!retrieveCityNames().contains(city)){
      cityNames.put(cityNames.size(), city);
      return true;
    }
    return false;  //
  }

  public boolean addRoute(String source, String destination, int distance,
    double price) throws CityNotFoundException {
     if(!retrieveCityNames().contains(source) || !retrieveCityNames().contains(destination))
        return false;
        //throw new CityNotFoundException();

      if(!G.containsRoute(source,destination) ){
        G.addEdge(new Route(source, destination, distance, price));
        G.addEdge(new Route(destination, source, distance, price));
        return true;
      }
    return false; // if it already exits then

  }

  public boolean updateRoute(String source, String destination, int distance,
    double price) throws CityNotFoundException {
     if(!retrieveCityNames().contains(source) || !retrieveCityNames().contains(destination))
        return false;
        //throw new CityNotFoundException();

      Route e = G.findRoute(source,destination);
      Route r = G.findRoute(destination, source);
        if(e != null && r != null){
          e.distance = distance;
          e.price = price;
          r.distance = distance;
          r.price = price;
          return true;
        }

    return false;
  }

   public int indexOf(String city){
     for(int i = 0; i < cityNames.size(); i++){
       if(cityNames.get(i).equals(city))
          return i;

     }

     return -1;
   }


  private class Digraph {
    private final int v;
    private int e;
    private LinkedList<Route>[] adj;
    private boolean[] marked;  // marked[v] = is there an s-v path
    private int[] edgeTo;      // edgeTo[v] = previous edge on shortest s-v path
    private int[] distTo;      // distTo[v] = number of edges shortest s-v path
    private Set<String> cityNames;

    /**
    * Create an empty digraph with v vertices.
    */
    public Digraph(int v) {
      if (v < 0) throw new RuntimeException("Number of vertices must be nonnegative");
      this.v = v;
      this.e = 0;
      @SuppressWarnings("unchecked")
      LinkedList<Route>[] temp =
      (LinkedList<Route>[]) new LinkedList[v];
      adj = temp;
      for (int i = 0; i < v; i++)
        adj[i] = new LinkedList<Route>();
    }

    public int indexOf(String city){
      Map<Integer,String> cityNames = retrieveCityMap();
      for(int i = 0; i < cityNames.size(); i++){
        if(cityNames.get(i).equals(city))
           return i;

      }

      return -1;
    }

    public String getCity(int i){
      Map<Integer,String> cityNames = retrieveCityMap();
      return cityNames.get(i);
    }
    /**
    * Add the edge e to this digraph.
    */
    public void addEdge(Route r) {
      int from = indexOf(r.source);
      adj[from].add(r);
      e++;
    }


    public boolean containsRoute(String source, String destination){
      for (int i = 0; i < v; i++) {
        for (Route e : G.adj(i)) {
        // check if e == source, destination
          if( e.source.equals(source) && e.destination.equals(destination)){
            return true;
          }
        }
      }
      return false;
    }


    public Route findRoute(String source, String destination){
      for (int i = 0; i < v; i++) {
        for (Route e : G.adj(i)) {
        // check if e == source, destination
          if( e.source.equals(source) && e.destination.equals(destination)){
            return e;
          }
        }
      }
      return null;
    }

    public void dijkstras(int source, int destination) {
      marked = new boolean[this.v];
      distTo = new int[this.v];
      edgeTo = new int[this.v];


      for (int i = 0; i < v; i++){
        distTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0;
      marked[source] = true;
      int nMarked = 1;

      int current = source;
      while (nMarked < this.v) {
        for (Route w : adj(current)) {
          if (distTo[current]+w.distance < distTo[destination]) {
        //TODO:update edgeTo and distTo
            edgeTo[indexOf(w.destination)] = current;
            distTo[indexOf(w.destination)] = distTo[current]+w.distance;

          }
        }
        //Find the vertex with minimim path distance
        //This can be done more effiently using a priority queue!
        int min = INFINITY;
        current = -1;

        for(int i=0; i<distTo.length; i++){
          if(marked[i])
            continue;
          if(distTo[i] < min){
            min = distTo[i];
            current = i;
          }
        }

    //TODO: Update marked[] and nMarked. Check for disconnected graph.
        if(current >= 0)
        {
            marked[current] = true;
            nMarked++;
        }
        else
        {
          break;
        }
      }
}

    /**
    * Return the edges leaving vertex v as an Iterable.
    * To iterate over the edges leaving vertex v, use foreach notation:
    * <tt>for (DirectedEdge e : graph.adj(v))</tt>.
    */
    public Iterable<Route> adj(int v) {
      return adj[v];
    }

    public void bfs(int source) {
      marked = new boolean[this.v];
      distTo = new int[this.v];
      edgeTo = new int[this.v];

      Queue<Integer> q = new LinkedList<Integer>();
      for (int i = 0; i < v; i++){
        distTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0; //distance from starting point to starting point
      marked[source] = true; //mark starting point as visited
      q.add(source); //add starting point to the queue

      while (!q.isEmpty()) {
        int v = q.remove(); //pop 0 (starting point), then v == 0
        for (Route w : adj(v)) { //for each directed edge(w) in adjacency list of vertex(v) 0
          if (!marked[indexOf(w.destination)]) { //if destination vertex of edge(w) is not marked, so not visited
          //TODO: Complete BFS implementation
              edgeTo[indexOf(w.destination)] = v;  //update the edgeTo of destination vertex to the starting point(parent)
              distTo[indexOf(w.destination)] = distTo[v] + 1;  //update the distance by 1 from starting point to destination vertex (w.to())
              marked[indexOf(w.destination)] = true;  //destination vertex of edge(w) has been visited (true)
              q.add(indexOf(w.destination)); //add the destination vertex to queue

          }
        }
      }
    }
   }
 }
