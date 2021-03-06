package ch.hesso.santour.view.Tracking.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ch.hesso.santour.R;
import ch.hesso.santour.adapter.POIListAdapter;
import ch.hesso.santour.db.CategoryPOIDB;
import ch.hesso.santour.db.DBCallback;
import ch.hesso.santour.model.CategoryPOI;
import ch.hesso.santour.model.POI;
import ch.hesso.santour.view.Main.MainActivity;
import ch.hesso.santour.view.Main.MainFullScreenPictureActivity;
import ch.hesso.santour.view.Tracking.Activity.TrackActivity;

public class FragmentListPOI extends Fragment {

    private View rootView;

    private POIListAdapter adapter;

    public FragmentListPOI() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.tracking_fragment_list_poi, container, false);

        final ListView list = rootView.findViewById(R.id.list_view_edit_poi);
        list.setAdapter(adapter = new POIListAdapter(FragmentListPOI.this.getContext(), MainActivity.track.getPois()));

        //Search in the list
        search(list);

        //Click listener on the POI
        onClickDialog(list);

        return rootView;
    }

    /**
     * Listener that show a dialog once a POI is clicked
     * @param list
     */
    private void onClickDialog(ListView list) {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final int pos = position;
                CategoryPOIDB.getAll(new DBCallback() {
                    @Override
                    public void resolve(Object o) {
                        List<CategoryPOI> categoryPOIList = (List<CategoryPOI>) o;

                        final POI poi = adapter.getListData().get(pos);

                        StringBuilder message = new StringBuilder(getString(R.string.name_) + poi.getName() + "\n" + getString(R.string.desc_) + poi.getDescription() + "\n\n" + getString(R.string.selected_));
                        for (int i = 0; i < poi.getCategoriesID().size(); i++) {
                            if (poi.getCategoriesID().get(i).equals(categoryPOIList.get(i).getId())) {
                                message.append("\t").append(categoryPOIList.get(i).getName()).append("\n");
                            }
                        }

                        final AlertDialog.Builder builder = new AlertDialog.Builder(FragmentListPOI.this.getActivity());
                        builder.setTitle(R.string.detail_poi_)
                                .setMessage(message.toString())
                                //Close the dialog
                                .setPositiveButton((getString(R.string.yes)), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                //Show the picture un full screen
                                .setNeutralButton(getString(R.string.show_pic), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(FragmentListPOI.this.getActivity(), MainFullScreenPictureActivity.class);
                                        intent.putExtra("image", poi.getPicture());
                                        FragmentListPOI.this.startActivity(intent);
                                    }
                                })
                                //Delete the POI
                                .setNegativeButton((getString(R.string.delete)), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MainActivity.track.getPois().remove(poi);
                                        TrackActivity.fragmentListPOI.updateList();
                                    }
                                })
                                .show();
                    }
                });
            }
        });
    }

    /**
     * Search in the listView
     * @param list
     */
    private void search(final ListView list) {
        EditText editText = rootView.findViewById(R.id.input_edit_search_poi);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<POI> pois = new ArrayList<>();
                ArrayList<POI> poisAdapter = adapter.getListData();

                for (POI poi : poisAdapter) {
                    if (poi.getName().contains(s.toString())) {
                        pois.add(poi);
                    }
                }
                list.setAdapter(new POIListAdapter(FragmentListPOI.this.getContext(), pois));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Update hte list with the new POI (called once a POI is created)
     */
    public void updateList() {
        ListView list = rootView.findViewById(R.id.list_view_edit_poi);
        list.setAdapter(new POIListAdapter(this.getContext(), MainActivity.track.getPois()));
    }
}
