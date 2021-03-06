package ch.hesso.santour.view.Edition.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import ch.hesso.santour.R;
import ch.hesso.santour.adapter.CategoryListAdapter;
import ch.hesso.santour.adapter.SectionsPageAdapter;
import ch.hesso.santour.db.DBCallback;
import ch.hesso.santour.db.TrackDB;
import ch.hesso.santour.model.POD;
import ch.hesso.santour.view.Edition.Fragment.FragmentEditDetailsPOD;
import ch.hesso.santour.view.Edition.Fragment.FragmentEditPODListCategories;

public class TrackEditPODActivity extends AppCompatActivity {

    public static POD podDetails;
    private static int positionPOD;

    private FragmentEditDetailsPOD fragmentEditDetailsPOD;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        menu.clear();
        inflater.inflate(R.menu.edition_track_navigation_bar_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //Handle check button activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edition_action_bar_save:
                savePOD();
                this.finish();
                return true;
            case R.id.edition_action_bar_delete:
                showAlertDialog();
                return true;
        }
        return false;
    }

    private void savePOD() {

        EditText editNamePOD  = findViewById(R.id.edit_pod_textView_namePOD);
        EditText editDescrPOD = findViewById(R.id.edit_pod_textView_descriptionContent);
        TrackEditActivity.trackDetails.getPods().get(positionPOD).setName(editNamePOD.getText().toString());
        TrackEditActivity.trackDetails.getPods().get(positionPOD).setDescription(editDescrPOD.getText().toString());

        ListView list = findViewById(R.id.list_view_pod_categories_list);
        CategoryListAdapter adapter = (CategoryListAdapter)list.getAdapter();
        TrackEditActivity.trackDetails.getPods().get(positionPOD).setCategoriesID(adapter.getAllItems());

        TrackEditActivity.fragmentListPOD.updateList();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        positionPOD = getIntent().getIntExtra("position", -1);
        podDetails = TrackEditActivity.trackDetails.getPods().get(positionPOD);

        fragmentEditDetailsPOD = new FragmentEditDetailsPOD();

        setContentView(R.layout.edition_activity_edit_pod);
        SectionsPageAdapter sectionsPageAdapter = new SectionsPageAdapter(getFragmentManager());

        ViewPager viewPager = findViewById(R.id.track_edit_container_pod);
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.edition_tabs_POD);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getFragmentManager());
        adapter.addFragment(fragmentEditDetailsPOD, getString(R.string.edition_details_pod));
        adapter.addFragment(new FragmentEditPODListCategories(), getString(R.string.edition_categories_pod));
        viewPager.setAdapter(adapter);
    }

    private void showAlertDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm the deletion of the POD")
                .setMessage("Are you sur to delete this POD?")
                //Delete the POI
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TrackEditActivity.trackDetails.getPods().remove(positionPOD);
                        TrackDB.update(TrackEditActivity.trackDetails, new DBCallback() {
                            @Override
                            public void resolve(Object o) {
                                TrackEditPODActivity.this.finish();
                            }
                        });
                        TrackEditActivity.fragmentListPOD.updateList();
                        dialog.cancel();
                    }
                })
                //Close the dialog
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
