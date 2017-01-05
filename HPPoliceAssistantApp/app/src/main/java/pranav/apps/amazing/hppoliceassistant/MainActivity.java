package pranav.apps.amazing.hppoliceassistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentContainer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private TabLayout tabLayout;
    private SearchView search;
    private int a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

       // search = (SearchView)findViewById(R.id.search_view);
        //ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.inflateMenu(R.menu.popup_menu);
        Intent i = getIntent();
        String val = i.getStringExtra("Tag");
        if(val.contentEquals("0")){
            a=0;
        }
        else if(val.contentEquals("1")){
            a=1;
        }
        else if(val.contentEquals("2")){
            a= 2;
        }
        else {
            a =3;
        }
        //DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_item_attachment:
                        mDrawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this,"Navigation features will soon be added", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.navigation_item_images:
                        mDrawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this,"Images Gallery Coming Soon !", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.navigation_item_location:
                        mDrawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this,"Item Location", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.logout:
                        mDrawerLayout.closeDrawers();
                        Intent intent = new Intent(MainActivity.this,Login.class);
                        intent.putExtra("finish", true); // if you are checking for this in your other Activities
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.help:
                        mDrawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this,"HP Police Always At Your Service", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.emergency:
                        mDrawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this,"Emergency Section", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.developers:
                        mDrawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this,"Developers", Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });
         /*
        //FloatingActionButton
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(R.id.drawer_layout), "I'm a Snackbar", Snackbar.LENGTH_LONG).setAction("Action", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "Snackbar Action", Toast.LENGTH_LONG).show();
                    }
                }).show();
            }
        });
        */
        //ViewPager and TabLayout
        final ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomAdapter(getSupportFragmentManager(),getApplicationContext()));
        tabLayout= (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(a);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                //tabLayout.setVisibility(View.GONE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
               // tabLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
               // tabLayout.setVisibility(View.VISIBLE);
            }
        });
         createTabIcons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.offline_challan:
                Intent i = new Intent(MainActivity.this,OfflineChallan.class);
                startActivity(i);
                return true;
            case R.id.offline_entry:
                Intent intent = new Intent(MainActivity.this,OfflineEntry.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                Intent intent1 = new Intent(MainActivity.this,Login.class);
                intent1.putExtra("finish", true); // if you are checking for this in your other Activities
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private class CustomAdapter extends FragmentPagerAdapter {
        private String fragments[]={"Entry","Challan","Stolen","Search"};
        public CustomAdapter(FragmentManager supportFragmentManager, Context applicationContext) {
        super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new Entry_veh();
                case 1:
                    return new Challan();
                case 2:
                    return new Stolen();
                case 3:
                    return new Search();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments[position];
        }
    }
    //to generate text inside tabs of tablayout and set icons
    private void createTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Entry");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_done, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Challan");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_attachment, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("Stolen");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("Search");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_place, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }
}
