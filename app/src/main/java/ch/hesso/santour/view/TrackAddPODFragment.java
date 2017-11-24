package ch.hesso.santour.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.hesso.santour.R;




public class TrackAddPODFragment extends Fragment {
    //Bottom Navigation Bar
    private BottomNavigationView navigation;

    public TrackAddPODFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.track_navigation_bar_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_track_add_pod, container, false);
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        //Bottom navigation
        navigation = getActivity().findViewById(R.id.track_bottom_navigation);
        navigation.setVisibility(View.GONE);

        return rootView;
    }
}
