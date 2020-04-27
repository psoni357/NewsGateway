package com.paavansoni.newsgateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
//import android.widget.EditText;
import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";

    private ArrayList<Source> sourceList = new ArrayList<>();
    private HashMap<String, ArrayList<Source>> sourceData = new HashMap<>();

    private Menu opt_menu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private List<Fragment> fragments;
    private MyPageAdapter pageAdapter;
    private ViewPager pager;

    private SampleReceiver sampleReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, NewsStoryService.class);
        startService(intent);

        sampleReceiver = new SampleReceiver();

        IntentFilter filter = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(sampleReceiver, filter);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);

        // Set up the drawer item click callback method
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Source s = sourceList.get(position);
                        Intent intent = new Intent();
                        intent.setAction(ACTION_MSG_TO_SERVICE);
                        intent.putExtra("SOURCE", s);
                        sendBroadcast(intent);
                        setTitle(s.getName());
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                }
        );

        // Create the drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        fragments = new ArrayList<>();

        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        // Load the data
        if (sourceData.isEmpty()) {
            new NewsSourceAsync(this).execute();
        }
    }


    @Override
    protected void onStop(){
        unregisterReceiver(sampleReceiver);
        Intent intent = new Intent(MainActivity.this, NewsStoryService.class);
        stopService(intent);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // Don't forget to unregister!
        //unregisterReceiver(sampleReceiver);
        super.onDestroy();
    }
    // You need the 2 below to make the drawer-toggle work properly:

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        opt_menu = menu;
        return true;
    }

    // You need the below to open the drawer when the toggle is clicked
    // Same method is called when an options menu item is selected.

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        setTitle(item.getTitle());

        sourceList.clear();
        ArrayList<Source> clist = sourceData.get(item.getTitle().toString());
        if (clist != null) {
            sourceList.addAll(clist);
        }

        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
        return super.onOptionsItemSelected(item);

    }

    public void setSources(ArrayList<Source> sources) {
        sourceData = new HashMap<>();
        sourceList = new ArrayList<>();

        for(Source s: sources){
            if(s.getCategory().isEmpty()){
                s.setCategory("general");
            }
            if (!sourceData.containsKey(s.getCategory())) {
                sourceData.put(s.getCategory(), new ArrayList<Source>());
            }
            ArrayList<Source> slist = sourceData.get(s.getCategory());
            if (slist != null) {
                slist.add(s);
            }
        }

        sourceData.put("All", sources);

        ArrayList<String> tempList = new ArrayList<>(sourceData.keySet());
        Collections.sort(tempList);
        for (String s : tempList)
            opt_menu.add(s);

        sourceList.addAll(sources);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, sourceList));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    /////////////////////////////////////////////////////////////

    class SampleReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action == null)
                return;
            switch (action) {
                case ACTION_NEWS_STORY:
                    Bundle b = intent.getBundleExtra("BUNDLE");
                    if(b!=null){
                        ArrayList<Article> a = (ArrayList<Article>) b.getSerializable("ARTICLES");
                        reDoFragments(a);
                    }
                    break;
                default:
                    Log.d(TAG, "onReceive: Unknown broadcast received");
            }

        }
    }

    private void reDoFragments(ArrayList<Article> a) {
        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);

        fragments.clear();
        for (int i = 0; i < a.size(); i++) {
            fragments.add(
                    ArticleFragment.newInstance(a.get(i), i+1, a.size()));
            pageAdapter.notifyChangeInPosition(i);
        }

        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
    }

//////////////////////////////////////

    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        MyPageAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }

    }
}
