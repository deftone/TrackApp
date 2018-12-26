package de.deftone.trackapp.utils;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

import de.deftone.trackapp.model.MyLocation;

public class TrackingUtils {

//    public static String getAverageSpeedInMotion(List<MyLocation> myLocations) {
//        //die app nimmt schrott auf :(
//        Float speedSum = 0F;
//        int count = 0;
//        for (MyLocation location : myLocations) {
//            if (location.getSpeed() > 0 && location.getSpeedAccuracy_km_h() < 0.1) {
//                speedSum += location.getSpeed_km_h();
//                count++;
//            }
//        }
//        System.out.println("speed>0: " + count);
//        System.out.println("speed=0: " + (myLocations.size() - count));
//
//        return (speedSum == 0) ? " - " : String.format("%.2f km/h", speedSum / count);
//    }

    public static String getCurrentSpeed(List<MyLocation> myLocations) {
        int lastItem = myLocations.size() - 1;
        return String.format("%.2f km/h", myLocations.get(lastItem).getSpeed_km_h());
    }

    public static String getAccuracy(List<MyLocation> myLocations) {
        return String.format("%.0f m", myLocations.get(myLocations.size() - 1).getAccuracy());
    }

    public static String getVerticalAccuracy(List<MyLocation> myLocations) {
        return String.format("%.0f m", myLocations.get(myLocations.size() - 1).getVerticalAccuracy());
    }

    public static String getDuration(List<MyLocation> myLocations) {
        int last = myLocations.size() - 1;
        long diffMillisec = myLocations.get(last).getTimestamp() -
                myLocations.get(0).getTimestamp();
        long minutes = diffMillisec / 1000 / 60;
        if (minutes < 60) {
            return "0h:" + preZero(minutes) + "min";
        } else {
            return minutes / 60 + "h:" + preZero(minutes % 60) + "min";
        }
    }

    public static float getDurationInH(List<MyLocation> myLocations) {
        int last = myLocations.size() - 1;
        long diffMillisec = myLocations.get(last).getTimestamp() -
                myLocations.get(0).getTimestamp();
        return (float) diffMillisec / 1000 / 60 / 60;

    }

    private static String preZero(long number) {
        return number < 10 ? "0" + number : "" + number;
    }

    public static String getDistanceInKm(float distance) {
        return String.format("%.2f km", distance);
    }

    public static float getDistanceInKm(List<Location> locations) {
        float distance = 0;
        float[] result = {0};
        //only if speed is > 0 for at least one of them?
        //only if accuracy is not too bad?
        //no to both, comparison with others show, this is closer to the other gpses
        for (int i = 0; i < locations.size() - 1; i++) {
            Location.distanceBetween(locations.get(i).getLatitude(),
                    locations.get(i).getLongitude(),
                    locations.get(i + 1).getLatitude(),
                    locations.get(i + 1).getLongitude(),
                    result);
            distance += result[0];
        }
        return distance / 1000;
    }

    public static float getAndSetDistanceInKm(List<Location> locations, List<MyLocation> myLocations) {
        float distance = 0;
        float[] result = {0};
        //only if speed is > 0 for at least one of them?
        //only if accuracy is not too bad?
        //no to both, comparison with others show, this is closer to the other gpses
        for (int i = 0; i < locations.size() - 1; i++) {
            Location.distanceBetween(locations.get(i).getLatitude(),
                    locations.get(i).getLongitude(),
                    locations.get(i + 1).getLatitude(),
                    locations.get(i + 1).getLongitude(),
                    result);
            distance += result[0];
            myLocations.get(i).setDistance(distance);
        }
        return distance / 1000;
    }

    public static List<Double> getAllAltitudesInM(List<MyLocation> myLocations) {
        List<Double> altitudeList = new ArrayList<>();
        for (MyLocation location : myLocations) {
            if (location.getVerticalAccuracy() <= 10)
                altitudeList.add(location.getAltitude());
        }
        return altitudeList;
    }

    public static String getLastAltitude(List<MyLocation> myLocations) {
        //wtf is wrong here????
//        //loop backwards until good altitude is found
//        String altitude = "";
//        for (int i = myLocations.size() - 1; i == 0; i--) {
//            if (myLocations.get(i).getAltitude() > 0
//                    && myLocations.get(i).getVerticalAccuracy() <= 15) {
//                altitude = String.format("%.0f m", myLocations.get(i).getAltitude());
//                return altitude;
//            }
//        }
//        return altitude.equals("") ? "-" : altitude;

//todo: use last value if current value is 0 !!
        return String.format("%.0f m", myLocations.get(myLocations.size() - 1).getAltitude());
    }

    public static String getDifferenceAltitude(ArrayList<MyLocation> myLocations) {
        return myLocations.get(myLocations.size() - 1).getAltitude() -
                myLocations.get(0).getAltitude() + " m";
    }
}
