package ch.hesso.santour.view.Edition.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import ch.hesso.santour.R;
import ch.hesso.santour.adapter.SectionsPageAdapter;
import ch.hesso.santour.db.TrackDB;
import ch.hesso.santour.model.POI;
import ch.hesso.santour.model.Track;
import ch.hesso.santour.view.Edition.Fragment.FragmentEditDetailsPOI;
import ch.hesso.santour.view.Edition.Fragment.FragmentEditPOIListCategories;

public class TrackEditPOIActivity extends AppCompatActivity {

    //Tabs layout adapter for fragment
    private SectionsPageAdapter sectionsPageAdapter;
    private ViewPager viewPager;

    public static Track poiDetails;

    private FragmentEditDetailsPOI fragmentEditDetailsPOI;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        menu.clear();
        inflater.inflate(R.menu.navigation_check, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //Handle check button activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.button_navigation_check:
                fragmentEditDetailsPOI.updateFiledsToDB();
                TrackDB.update(poiDetails);
                this.finish();
                return true;
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentEditDetailsPOI = new FragmentEditDetailsPOI();

        setContentView(R.layout.edition_activity_edit_poi);
        sectionsPageAdapter = new SectionsPageAdapter(getFragmentManager());

        viewPager = findViewById(R.id.track_edit_container_poi);
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.edition_tabs_POI);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getFragmentManager());
        adapter.addFragment(fragmentEditDetailsPOI, getString(R.string.edition_details_poi));
        adapter.addFragment(new FragmentEditPOIListCategories(), getString(R.string.edition_categories_poi));
        viewPager.setAdapter(adapter);
    }
}
