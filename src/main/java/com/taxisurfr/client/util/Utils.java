package com.taxisurfr.client.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.geometry.Spherical;

public class Utils
{
    public static List<LatLng> getOpimimzedPlaces(List<LatLng> optimizedPlaces, List<LatLng> places, LatLng destination)
    {
        Map<LatLng, Double> placesMap = new HashMap<LatLng, Double>();

        // filling place-distance map
        for (LatLng pickupPlace : places)
        {
            placesMap.put(pickupPlace, Spherical.computeDistanceBetween(destination, pickupPlace));
        }

        // Getting far most pickup place as start
        Double longestDistanceFromDest = 0.0;
        LatLng farestPickupFromDest = null;
        Double imediateLongestDistance = 0.0;
        LatLng imediatePickup = null;
        // Checking hashmap for longest distance...
        for (Object key : placesMap.keySet())
        {
            if (placesMap.get(key) > longestDistanceFromDest)
            {
                longestDistanceFromDest = placesMap.get(key);
                farestPickupFromDest = (LatLng) key;
            }
        }

        optimizedPlaces.add(farestPickupFromDest);
        // setup optimizedPlaceMap
        for (int i = 0; i < placesMap.size(); i++)
        {
            for (Object key : placesMap.keySet())
            {
                if (longestDistanceFromDest > placesMap.get(key) && placesMap.get(key) > imediateLongestDistance)
                {
                    imediateLongestDistance = placesMap.get(key);
                    imediatePickup = (LatLng) key;
                }
            }
            if (imediatePickup != null)
            {
                optimizedPlaces.add(imediatePickup);
                longestDistanceFromDest = imediateLongestDistance;
                imediateLongestDistance = 0.0;
            }
            imediatePickup = null;
        }
        return optimizedPlaces;
    }
}
