package ch.hesso.santour.view.Edition.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import ch.hesso.santour.R;
import ch.hesso.santour.adapter.PODListAdapter;
import ch.hesso.santour.model.Track;
import ch.hesso.santour.view.Edition.Activity.TrackEditActivity;
import ch.hesso.santour.view.Edition.Activity.TrackEditPODActivity;

public class FragmentListPOD extends Fragment {

    private View rootView;
    private PODListAdapter adapter;

    private FragmentManager fragmentManager;
    private Fragment fragment;

    public FragmentListPOD() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.edition_fragment_list_pod, container, false);

        final ListView list = rootView.findViewById(R.id.list_view_pod);

        //Add a list of POD if there is any on the track
        if(TrackEditActivity.trackDetails.getPods().size() != 0) {
            list.setAdapter(adapter = new PODListAdapter(FragmentListPOD.this.getContext(), TrackEditActivity.trackDetails.getPods()));

            //Open a new fragment once a POD is clicked
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //A changer pour cast POI
                    TrackEditPODActivity.podDetails = (Track) adapterView.getItemAtPosition(i);

                    Intent intent = new Intent(rootView.getContext(), TrackEditPODActivity.class);
                    startActivity(intent);
                    /*
                    fragmentManager  = getFragmentManager();
                    fragment = new FragmentEditDetailsPOD();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.edition_content, fragment).commit();
                    */
                }
            });
        }
        return rootView;
    }
}