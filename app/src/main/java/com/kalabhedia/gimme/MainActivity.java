package com.kalabhedia.gimme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    public ActionBar actionbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    public TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextView NavHeaderUserName;
    private ImageView NavHeaderImageView;

//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mAuth = FirebaseAuth.getInstance();
//        currentUser = mAuth.getCurrentUser();
//        if (currentUser == null) {
//            Intent authIntent = new Intent(MainActivity.this, PhoneAuthActivity.class);
//            startActivity(authIntent);
//            finish();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.main_drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation);
        View headerView = navigationView.getHeaderView(0);
        NavHeaderUserName = (TextView) headerView.findViewById(R.id.nav_header_name);
        NavHeaderImageView = (ImageView) headerView.findViewById(R.id.nav_header_photo);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent authIntent = new Intent(MainActivity.this, PhoneAuthActivity.class);
            startActivity(authIntent);
            finish();
        }

//        Log.v("mainactivity", currentUser.getDisplayName().toString());
        if (currentUser.getDisplayName()!=null&&currentUser!=null)
        NavHeaderUserName.setText(currentUser.getDisplayName().toString());



        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        if (menuItem.getItemId() == R.id.nav_share) {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            String shareBody = "Check This Out";
                            String shareSub = "this is the link";
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareSub);
                            startActivity(Intent.createChooser(shareIntent, "Share Using"));
                        } else if (menuItem.getItemId() == R.id.nav_rate) {
                            Toast.makeText(MainActivity.this, "Feature to be added", Toast.LENGTH_SHORT).show();
                        } else if (menuItem.getItemId() == R.id.nav_logout) {
                            mAuth = FirebaseAuth.getInstance();
                            mAuth.signOut();
                            startActivity(new Intent(MainActivity.this, PhoneAuthActivity.class));
                            finish();

                        } else if (menuItem.getItemId() == R.id.nav_phone_auth) {
                            startActivity(new Intent(MainActivity.this, PhoneAuthActivity.class));
                        }
                        return true;
                    }
                });

        tabLayout = (TabLayout) findViewById(R.id.tab_layout_id);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.AddFragment(new OneFragment(), "Friends");
        adapter.AddFragment(new TwoFragment(), "Explore");
        adapter.AddFragment(new ThreeFragment(), "Activity");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

//        if (currentUser!=null){
//            NavHeaderUserName.setText(currentUser.getDisplayName());
//            new DownloadImageTask(NavHeaderImageView).execute(String.valueOf(currentUser.getPhotoUrl()));
//        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void swapFragment(@NonNull Fragment fragment, @Nullable Bundle args, @Nullable Fragment previousFragment) {
        // Insert the fragment by replacing any existing fragment
        if (args != null) {
            fragment.setArguments(args);
        }

        if (previousFragment != null) {
            // Removing previous fragment from history
            getSupportFragmentManager().beginTransaction().remove(previousFragment).commit();
            getSupportFragmentManager().popBackStack();
        }

        // Bringing new fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment)
                .addToBackStack(fragment.getClass().getSimpleName()).commit();
    }
}
