package com.sambroughton.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.maps.DirectionsApi.RouteRestriction;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

/**
 * App for calculating the walking distance between
 * two cities
 * 
 * @author sambroughton
 *
 */
public class App {

    /**
     * Returns all the towns from the first col in the csv file
     * 
     * @param fileName is the csv file location with all the towns 
     * @return all the towns in an ArrayList of Strings
     */
    public static ArrayList<String> getTownList(String fileName) {
        
        try {
            ArrayList<String> towns = new ArrayList<String>();
            File csvFile = new File(fileName);
            BufferedReader br = new BufferedReader(new FileReader(csvFile));
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] arr = line.split(",");
                towns.add(arr[0]);
            } 
            
            return towns;
        } catch(IOException ex) {
            ex.printStackTrace();
            System.out.println("Failed: could not find file.");
        }
        
        return null;
    }
    
    /**
     * Returns a random town
     * 
     * @param towns is the ArrayList<String> of towns from the file
     * @return a town from the file with a randomly generated index
     */
    public static String getTown(ArrayList<String> towns) {
        
        int townsLen = towns.size();
        int rand = (int)(Math.random() * townsLen + 1);
        
        return towns.get(rand);
    }
    
    
    /**
     * Returns the distance in meters between two towns 
     * using google API distance matrix
     * 
     * @param addrOne is the first town
     * @param addrTwo is the second town
     * @return the distance apart in meters 
     * @throws ApiException
     * @throws InterruptedException
     * @throws IOException
     */
    public static long getDriveDist(String addrOne, String addrTwo) throws ApiException, InterruptedException, IOException {
			
	//set up key
   	GeoApiContext distCalcer = new GeoApiContext.Builder()
		    .apiKey("AIzaSyAiQdVuuENGrASqlcc0ENiggVIWPtJdlQc")
		    .build();
   	
   	DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(distCalcer); 
    DistanceMatrix result = req.origins(addrOne)
            .destinations(addrTwo)
            .mode(TravelMode.DRIVING)
            .avoid(RouteRestriction.TOLLS)
            .language("en-US")
            .await();
    
    long distApart = result.rows[0].elements[0].distance.inMeters;
    
	return distApart;
    
    }

    /**
     * Main method
     *
     */
    public static void main(String[] args) {
        
        // ArrayList of all towns from col1 of csv
        ArrayList<String> towns = getTownList("src/main/java/com/sambroughton/app/test_three.csv");
        
        if (towns != null) {
            
            // get 2 specific towns from the file
            // remove the first from ArrayList before getting the second
            String town1 = getTown(towns);
            towns.remove(towns.indexOf(town1));
            String town2 = getTown(towns);
            
            try {
                // get the distance between the towns in meters
                long dist = getDriveDist(town1, town2);
                
                // totalMinutes is based on walking at a speed of 10.6 minutes per km
                long totalMinutes = (long)(((dist / 1000) * 10.6));
                
                // calculate the days, hours and minutes from total minutes
                int days = (int)(totalMinutes / 24 / 60);
                int hours = (int)(totalMinutes / 60 % 24);
                int minutes = (int)(totalMinutes / 60);
                
                // print
                System.out.println("It will take " 
                                   + days + " days " 
                                   + hours + " hours and " 
                                   + minutes + " minutes to walk from "
                                   + town1 + " to "
                                   + town2 + ".");
                
                
            } catch (Exception e) {
                // when distance could not be found
                System.out.println("Sorry, could not find distance between "
                                   + town1 + " and "
                                   + town2 + ".");
            }
            
        } else {
            System.out.println("Failed: null ArrayList for towns");
        }

    }
}
