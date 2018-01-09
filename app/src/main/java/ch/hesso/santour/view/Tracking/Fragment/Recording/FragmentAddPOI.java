package ch.hesso.santour.view.Tracking.Fragment.Recording;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import ch.hesso.santour.R;
import ch.hesso.santour.business.LocationManagement;
import ch.hesso.santour.business.PictureManagement;
import ch.hesso.santour.db.DBCallback;
import ch.hesso.santour.model.POI;
import ch.hesso.santour.model.Position;

public class FragmentAddPOI extends Fragment {
    private static final int SELECT_PICTURE = 1;

    private Position position;

    private String imageName = "";
    private View rootView;

    public FragmentAddPOI() {
        // Required empty public constructor
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_close:
                getActivity().getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.track_navigation_bar_actions_cancel, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tracking_fragment_recording_add_poi, container, false);
        setHasOptionsMenu(true);

        final ImageButton imageButton = rootView.findViewById(R.id.track_add_poi_add_picture);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGetMessage = new Intent(getActivity(), PictureManagement.class);
                startActivityForResult(intentGetMessage, PictureManagement.REQUEST_IMAGE_CAPTURE);
            }
        });

        Button nextButton = rootView.findViewById(R.id.next_poi);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editTextName = getActivity().findViewById(R.id.edit_text_poi_name);
                EditText editTextDesc = getActivity().findViewById(R.id.edit_text_poi_desc);
                String poiName = editTextName.getText().toString();
                String poiDesc = editTextDesc.getText().toString();

                //Go to the category choice ince all the fileds are completed
                //TODO show the unfilled filed with a red line (error below the textedit)
                if(!poiName.equals("") && !poiDesc.equals("")  && !imageName.equals("")){
                //Go to the category choice ince all the fileds are completed
                //TODO show the unfilled filed with a red line (error below the textedit)
                    POI poi = new POI();
                    poi.setName(poiName);
                    poi.setDescription(poiDesc);
                    poi.setPicture(imageName);
                    poi.setPosition(position);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("poi", poi);

                    Fragment fragment = new FragmentCategoriesPOI();
                    fragment.setArguments(bundle);

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.main_content, fragment).commit();
                }
            }
        });

        LocationManagement.getLastKnownPosition(getActivity(), new DBCallback() {
            @Override
            public void resolve(Object o) {
                position = (Position) o;

                TextView textViewLat = rootView.findViewById(R.id.tv_lat_add_poi);
                TextView textViewLng = rootView.findViewById(R.id.tv_lng_add_poi);

                //Update the text of where the latitude and longitude are displayed
                textViewLat.setText(getString(R.string.lat_) + Math.floor(position.latitude * 100) / 100);
                textViewLng.setText(getString(R.string.lng_) + Math.floor(position.longitude * 100) / 100);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PictureManagement.REQUEST_IMAGE_CAPTURE) {
            Bundle extras = data.getExtras();
            imageName = extras.getString("imageName");
            Log.d("test", imageName);
            Bitmap loaded = BitmapFactory.decodeFile(PictureManagement.localStoragePath+imageName);
            ((ImageView) rootView.findViewById(R.id.track_add_poi_picture_view)).setImageBitmap(PictureManagement.rotatePicture(loaded));
        }
    }
}
