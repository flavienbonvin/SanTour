package ch.hesso.santour.view.Edition.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.hesso.santour.R;

public class FragmentEditDetailsPOI extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private View rootView;

    public FragmentEditDetailsPOI() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.edition_fragment_edit_details_poi, container, false);

        return  rootView;
    }

}
