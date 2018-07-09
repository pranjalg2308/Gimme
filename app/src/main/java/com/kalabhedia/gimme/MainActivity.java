package com.kalabhedia.gimme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.onesignal.OneSignal;

import java.io.File;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public ActionBar actionbar;
    private DrawerLayout mDrawerLayout;
    private int READ_CONTACT_PERMISSION = 1;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    public TabLayout tabLayout;
    public ViewPager viewPager;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextView NavHeaderUserName;
    private ImageView NavHeaderImageView;
    private DataBaseHelper db;

    @Override
    public void onSupportActionModeStarted(@NonNull ActionMode mode) {
        super.onSupportActionModeStarted(mode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DataBaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);


        mDrawerLayout = findViewById(R.id.main_drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation);
        View headerView = navigationView.getHeaderView(0);
        NavHeaderUserName = (TextView) headerView.findViewById(R.id.nav_header_name);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        SharedPreferences sh=getSharedPreferences("UserProfile",MODE_PRIVATE);
        NavHeaderUserName.setText(sh.getString("UserName",""));
        if (currentUser == null) {
            Intent authIntent = new Intent(MainActivity.this, PhoneAuthActivity.class);
            startActivity(authIntent);
            finish();
        } else if (currentUser.getDisplayName() != null && currentUser != null) {
            NavHeaderUserName.setText(currentUser.getDisplayName().toString());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, READ_CONTACT_PERMISSION);

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
                            clearApplicationData();
                            sh.edit().clear().commit();
                            startActivity(new Intent(MainActivity.this, PhoneAuthActivity.class));
                            finish();
                        }
                        return true;
                    }
                });

        tabLayout = (TabLayout) findViewById(R.id.tab_layout_id);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.AddFragment(new com.kalabhedia.gimme.OneFragment(), "Friends");
        adapter.AddFragment(new com.kalabhedia.gimme.TwoFragment(), "Explore");
        adapter.AddFragment(new com.kalabhedia.gimme.ThreeFragment(), "Activity");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    public void clearApplicationData() {
        File cacheDirectory = getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    deleteDir(new File(applicationDirectory, fileName));
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        viewPager.setVisibility(View.VISIBLE);
        OneFragment.fab.setVisibility(View.VISIBLE);
        actionbar.setTitle("Gimme");
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Loader<Cursor> cursorLoader = new CursorLoader(getApplicationContext(), CONTENT_URI, null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Gimme", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String[] conversion = number.split(" ");
            String[] conversion1 = number.split("-");
            if (conversion1.length > 1) {
                number = "";
                for (String i : conversion1) {
                    number += i;
                }
            } else if (conversion.length > 1) {
                number = "";
                for (String i : conversion1) {
                    number += i;
                }
            }
            Log.w("Contact :", number);
            editor.putString(number, name);
            editor.apply();
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    private boolean checkExternalPermission() {
        String permission = android.Manifest.permission.READ_CONTACTS;
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

}
