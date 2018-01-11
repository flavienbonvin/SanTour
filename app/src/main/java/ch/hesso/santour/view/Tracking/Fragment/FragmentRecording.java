package ch.hesso.santour.view.Tracking.Fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.os.SystemClock;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.android.gms.maps.model.MapStyleOptions;

import ch.hesso.santour.R;
import ch.hesso.santour.business.FragmentInterface;
import ch.hesso.santour.business.LocationManagement;
import ch.hesso.santour.business.TrackingManagement;
import ch.hesso.santour.db.DBCallback;
import ch.hesso.santour.model.Position;
import ch.hesso.santour.view.Main.MainActivity;
import ch.hesso.santour.view.Tracking.Fragment.Recording.FragmentAddPOD;
import ch.hesso.santour.view.Tracking.Fragment.Recording.FragmentAddPOI;

public class FragmentRecording extends Fragment implements OnMapReadyCallback, FragmentInterface {

    //Play & Stop button
    private ImageButton trackPlayButton;
    private ImageButton trackStopButton;
    private ImageButton trackPauseButton;
    private long timeWhenStopped;
    private CardView cardViewRecord;
    private CardView cardViewPause;
    
    //Add POI / POD button
    private ImageButton addPOIButton;
    private ImageButton addPODButton;

    //Chronometer
    private Chronometer chrono;

    //Google Map
    private MapView mapView;
    private GoogleMap map;

    //Fragment
    private Fragment fragment;
    private FragmentManager fragmentManager;

    //Bottom Navigation Bar
    private BottomNavigationView navigation;

    private Marker marker;

    private boolean isPaused = false;


    public FragmentRecording() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tracking_fragment_recording, container, false);
        getActivity().setTitle(MainActivity.track.getName());
        setHasOptionsMenu(true);

        mapView = rootView.findViewById(R.id.track_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        trackPlayButton = rootView.findViewById(R.id.track_play_button);
        trackStopButton = rootView.findViewById(R.id.track_stop_button);
        trackPauseButton = rootView.findViewById(R.id.track_pause_button);
        cardViewRecord = rootView.findViewById(R.id.track_card_view_record);
        cardViewPause = rootView.findViewById(R.id.track_card_view_pause);
        
        trackPauseButton.setEnabled(false);

        chrono = rootView.findViewById(R.id.track_chronometer);
        trackPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackPlayButton.setVisibility(View.GONE);
                trackStopButton.setVisibility(View.VISIBLE);
                trackPauseButton.setClickable(true);
                trackPauseButton.setEnabled(true);
                cardViewRecord.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryRed));
                cardViewPause.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

                chrono.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                chrono.start();

                if(!isPaused) {
                    TrackingManagement.startTracking(FragmentRecording.this.getActivity());
                }else {
                    TrackingManagement.resumeTracking(FragmentRecording.this.getActivity());
                    isPaused = false;
                }

                //TODO change style of the button when disabled
                addPOIButton.setEnabled(true);
                addPODButton.setEnabled(true);
            }
        });

        trackPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackPlayButton.setVisibility(View.VISIBLE);
                trackStopButton.setVisibility(View.GONE);
                cardViewRecord.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                cardViewPause.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryRed));

                timeWhenStopped = chrono.getBase() - SystemClock.elapsedRealtime();
                chrono.stop();

                isPaused = true;

                TrackingManagement.pauseTracking(FragmentRecording.this.getActivity());
            }
        });

        trackStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackPlayButton.setVisibility(View.VISIBLE);
                trackStopButton.setVisibility(View.GONE);
                trackPauseButton.setClickable(false);
                trackPauseButton.setEnabled(false);
                cardViewRecord.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                chrono.stop();

                //TODO change style of the button when disabled
                addPOIButton.setEnabled(false);
                addPODButton.setEnabled(false);

                TrackingManagement.stopTracking(FragmentRecording.this.getActivity());
                long time = SystemClock.elapsedRealtime() - chrono.getBase();

                MainActivity.track.setDuration(time);

                fragmentManager = getFragmentManager();
                fragment = new FragmentEndTrack();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.addToBackStack("NoReturn");
                transaction.replace(R.id.main_content, fragment).commit();

                isPaused = false;
            }
        });


        LocationManagement.interfaceToWatch(FragmentRecording.this);

        addPOIButton = rootView.findViewById(R.id.track_add_poi_button);
        addPOIButton.setEnabled(false);
        addPOIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager  = getFragmentManager();
                fragment  = new FragmentAddPOI();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.addToBackStack("ADD");
                transaction.replace(R.id.main_content, fragment).commit();
            }
        });

        addPODButton = rootView.findViewById(R.id.track_add_pod_button);
        addPODButton.setEnabled(false);
        addPODButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager  = getFragmentManager();
                fragment  = new FragmentAddPOD();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.addToBackStack("ADD");
                transaction.replace(R.id.main_content, fragment).commit();
            }
        });

        return  rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) throws SecurityException {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_json));
        map = googleMap;

        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setAllGesturesEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);

        LatLng coordinate = new LatLng(86, 20);
        map.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
        LocationManagement.getLastKnownPosition(FragmentRecording.this.getActivity(), new DBCallback() {
            @Override
            public void resolve(Object o) {
                Position p = (Position)o;
                LatLng latlng =new LatLng(p.latitude,p.longitude);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latlng);

                map.addMarker(markerOptions);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,18));
            }
        });
    }




    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void updateMap(PolylineOptions polyLine, Position position) {
        map.clear();
        map.addPolyline(polyLine);

        LatLng latLng = new LatLng(position.latitude, position.longitude);
        float[] hsv = new float[3];
        map.addMarker(new MarkerOptions().position(latLng));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
    }

    @Override
    public void setTextDistance(String text) {
        TextView textView = getActivity().findViewById(R.id.tv_distance);
        textView.setText(text);
    }
}
