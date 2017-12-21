package ch.hesso.santour.business;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import ch.hesso.santour.db.DBCallback;
import ch.hesso.santour.model.Position;
import ch.hesso.santour.view.Main.MainActivity;

/**
 * Created by flavien on 11/23/17.
 */

public class LocationManagement {

    private List<Position> positionsList;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private static FragmentInterface fragmentInterface;


    /**
     * Start the location tracking,
     * @param activity
     */
    protected void startLocationTracking(Activity activity) {
        positionsList = new ArrayList<>();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000).setFastestInterval(5000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Create the callback for the location request
        callbackCreation(activity);

        //Check that all mandatory permissions are enabled (location, camera and external storage)
        PermissionManagement.checkMandatoryPermission(activity);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    protected void resumeTracking(Activity activity){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000).setFastestInterval(5000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Create the callback for the location request
        callbackCreation(activity);

        //Check that all mandatory permissions are enabled (location, camera and external storage)
        PermissionManagement.checkMandatoryPermission(activity);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    /**
     * Stop the tracking (used at the end of the track)
     * @param activity
     * @return positions
     */
    protected List<Position> stopTracking(Activity activity){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        return positionsList;
    }

    /**
     * Get the current position of the user
     * @param activity
     * @param callback
     */
    public static void getLastKnownPosition(final Activity activity, final DBCallback callback){
        final FusedLocationProviderClient fusedTemp = LocationServices.getFusedLocationProviderClient(activity);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(0).setFastestInterval(0).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        PermissionManagement.checkMandatoryPermission(activity);
        fusedTemp.flushLocations();

        fusedTemp.requestLocationUpdates(locationRequest, null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                PermissionManagement.checkMandatoryPermission(activity);
                fusedTemp.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Position position = new Position();

                            position.setAltitude(location.getAltitude());
                            position.setLatitude(location.getLatitude());
                            position.setLongitude(location.getLongitude());
                            position.setTime(System.currentTimeMillis() / 1000);
                            callback.resolve(position);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Porblem with the location update", Toast.LENGTH_SHORT).show();
                        Log.e(this.getClass().getCanonicalName(), e.getMessage());
                    }
                });
            }
        });
    }

    /**
     * Calculate the distance of the track
     * @param positions
     * @return distance
     */
    protected double calculateTrackLength(List<Position> positions){
        Location locationFrom = new Location("temp");
        Location locationTo = new Location("temp");
        double distance = 0;

        for (int i=0; i < positions.size()-1; i++){
            locationFrom.setLatitude(positions.get(i).latitude);
            locationFrom.setLongitude(positions.get(i).longitude);
            locationFrom.setAltitude(positions.get(i).altitude);

            locationTo.setLatitude(positions.get(i+1).latitude);
            locationTo.setLongitude(positions.get(i+1).longitude);
            locationTo.setAltitude(positions.get(i+1).altitude);

            double temp = locationFrom.distanceTo(locationTo);
            distance += temp;
        }
        return distance;
    }

    /**
     * Return distance between two position in meter
     * @param from
     * @param to
     * @return double
     */
    private double calculateDistance2Points(Position from, Position to){
        Location fromLocation = convertToLocation(from);
        Location toLocation = convertToLocation(to);

        return fromLocation.distanceTo(toLocation);
    }

    /**
     * Callback needed by the requestLocationUpdates method of the FusedLocationProviderClient
     * @param activity
     */
    private void callbackCreation(final Activity activity){
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.d(LocationManagement.class.getName(), "New position logged: " + location.getLatitude() + " " + location.getLongitude());
                    cleanLocationData(location);
                }
                MainActivity.track.setDistance(calculateTrackLength(positionsList));

                if (MainActivity.track.getDistance() < 999) {
                    fragmentInterface.setTextDistance(Math.floor(MainActivity.track.getDistance()*100)/100 + " m");
                } else {
                    fragmentInterface.setTextDistance(Math.floor(MainActivity.track.getDistance())/1000 + " km");
                }
            }
        };
    }


    /**
     * Method used to clean the data we get from our GPS (avoid duplicate data)
     * @param location
     */
    private void cleanLocationData(Location location){
        Position newPosition = convertToPosition(location);
        if (positionsList.size() > 1) {
            Position lastPosition = positionsList.get(positionsList.size() - 1);

            double distance = calculateDistance2Points(newPosition, lastPosition);

            if(distance < 100 && distance > 8){
                Log.d(LocationManagement.class.getName(), "Location added to the track, distance: " + distance);

                positionsList.add(newPosition);
                mapUpdate();
            }
        } else {
            Log.d(LocationManagement.class.getName(), "First location logged");

            positionsList.add(newPosition);
            mapUpdate();
        }
    }

    /**
     * Draw a line on the map with all the last position logged
     */
    private void mapUpdate(){
        LatLng latLng;
        PolylineOptions polylineOptions = new PolylineOptions().width(7).color(Color.parseColor("#52c7b8")).geodesic(true);

        for (Position position : positionsList) {
            latLng = new LatLng(position.latitude, position.longitude);
            polylineOptions.add(latLng);
        }
        fragmentInterface.updateMap(polylineOptions, positionsList.get(positionsList.size()-1));
    }

    /**
     * Convert a Location object to a Position object
     * @param location
     * @return position
     */
    private Position convertToPosition(Location location){
        Position position = new Position();

        position.setAltitude(location.getAltitude());
        position.setLatitude(location.getLatitude());
        position.setLongitude(location.getLongitude());
        position.setTime(System.currentTimeMillis()/1000);

        return position;
    }

    /**
     * Convert a Position object to a Location
     * @param position
     * @return
     */
    private Location convertToLocation(Position position){
        Location location = new Location("temp");

        location.setLongitude(position.longitude);
        location.setLatitude(position.latitude);
        location.setAltitude(position.altitude);

        return location;
    }

    /**
     * To be able to change items on the view we have to set an interface to access some methods
     * @param fragmentInterface
     */
    public static void interfaceToWatch(FragmentInterface fragmentInterface){
        LocationManagement.fragmentInterface = fragmentInterface;
    }
}