package pt.pa.transportmap;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import pt.pa.view.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Import data from CSV files to the TransportMap
 */
public class ImportCsv {
    private static final String stopCSV = "src/main/resources/dataset/stops.csv";
    private static final String coordsCSV = "src/main/resources/dataset/xy.csv";
    private static final String routeCSV = "src/main/resources/dataset/routes.csv";

    /**
     * Update the transport map by importing data from CSV files. Subsequent calls add additional stops or routes found in the files.
     */
    public static void update(TransportMap transportMap){
        Map<String, Stop> stopMap = readStop();
        // add stops to transportMap
        Set<Stop> vertexList = (Set<Stop>) transportMap.getStops();
        for(Stop stop : stopMap.values()){
            if(!vertexList.contains(stop)){
                transportMap.insertVertex(stop);
            }
        }
        readRoute(transportMap, stopMap); // also imports routes in parallel
    }

    /**
     * Read routes from CSV file
     * @param transportMap TransportMap the transport map instance
     * @param stopMap Map<String, Stop> map containing all stops
     */
    private static void readRoute(TransportMap transportMap, Map<String, Stop> stopMap){
        try (CSVReader reader = new CSVReaderBuilder(new BufferedReader(new FileReader(routeCSV)))
                .withSkipLines(1).build()) { // Skip first line

            String[] nextLine;
            final int numTypes = TransportType.values().length;
            Set<Route> edgeList = (Set<Route>) transportMap.getRoutes();

            while ((nextLine = reader.readNext()) != null) {
                // create route and add transports
                Route route = new Route();
                int colIndex = 2;
                for(TransportType type : TransportType.values()){
                    if(!nextLine[colIndex].isEmpty()){
                        route.addTransport(
                                type,
                                Double.parseDouble(nextLine[colIndex]), // distance
                                Double.parseDouble(nextLine[colIndex + numTypes]), // duration
                                Double.parseDouble(nextLine[colIndex + (numTypes * 2)]) // cost
                        );
                    }
                    colIndex++;
                }
                // add route to transportMap
                if(!edgeList.contains(route)) {
                    transportMap.insertEdge(
                            stopMap.get(nextLine[0]), // get origin stop by code
                            stopMap.get(nextLine[1]), // get destination stop by code
                            route
                    );
                }
            }
            reader.close(); // close reader
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Read stops from CSV file
     * @return Map<String, Stop> map containing all stops with stop code as key and the object Stop as value
     */
    private static Map<String, Stop> readStop(){
        Map<String, Stop> map = new HashMap<>();

        try (CSVReader reader = new CSVReaderBuilder(new BufferedReader(new FileReader(stopCSV)))
                .withSkipLines(1).build()) { // Skip first line

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                Stop stop = new Stop(
                        nextLine[0], // code
                        nextLine[1], // name
                        Double.parseDouble(nextLine[2]), // latitude
                        Double.parseDouble(nextLine[3]) // longitude
                );
                map.put(nextLine[0], stop);
            }
            reader.close(); // close reader
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return map;
    }

    /**
     * Read coordinates from CSV file and returns a map with the stop code as key and the coordinates as value
     * @return Map<String, Coordinate> the dictionary with the stop code as key and the coordinates object as value
     */
    public static Map<String, Coordinate> readCoords(){
        Map<String, Coordinate> map = new HashMap<>();
        try (CSVReader reader = new CSVReaderBuilder(new BufferedReader(new FileReader(coordsCSV)))
                .withSkipLines(1).build()) { // Skip first line

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // stop code, coordinate(stop code, x, y)
                map.put(nextLine[0], new Coordinate(Integer.parseInt(nextLine[1]), Integer.parseInt(nextLine[2])));
            }
            reader.close(); // close reader
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return map;
    }
}