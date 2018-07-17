package com.kalabhedia.gimme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public ActionBar actionbar;
    private DrawerLayout mDrawerLayout;
    private int READ_CONTACT_PERMISSION = 1;
    private int WRITE_EXTERNAL_STORAGE_PERMISSION = 2;
    private int READ_EXTERNAL_STORAGE_PERMISSION = 3;

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    public ArrayList<HashMap<String, String>> contactdetails;
    public TabLayout tabLayout;
    public ViewPager viewPager;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextView NavHeaderUserName;
    private ImageView NavHeaderImageView;
    Context context;
    private DataBaseHelper db;
    private AdView mAdView;

    @Override
    protected void onResume() {
        super.onResume();
        contactdetails = new ArrayList<>();
        if (checkExternalPermission())
            getSupportLoaderManager().initLoader(1, null, this);
//        Dataupdate();
    }

    @Override
    public void onSupportActionModeStarted(@NonNull ActionMode mode) {
        super.onSupportActionModeStarted(mode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-3085607779570484/2826426787");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("1ED63523084F0BB6943F6C4C977B9C37").build();
        mAdView.loadAd(adRequest);


        contactdetails = new ArrayList<>();
        db = new DataBaseHelper(this);
        context = getApplicationContext();
        Dataupdate();
        if (checkExternalPermission())
            getSupportLoaderManager().initLoader(1, null, this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);


        mDrawerLayout = findViewById(R.id.main_drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation);
        View headerView = navigationView.getHeaderView(0);
        NavHeaderUserName = (TextView) headerView.findViewById(R.id.nav_header_name);
        Dataupdate();


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        SharedPreferences sh = getSharedPreferences("UserProfile", MODE_PRIVATE);
        NavHeaderUserName.setText(sh.getString("UserName", ""));
        if (currentUser == null) {
            Intent authIntent = new Intent(MainActivity.this, PhoneAuthActivity.class);
            startActivity(authIntent);
            finish();
        } else if (currentUser.getDisplayName() != null && currentUser != null) {
            NavHeaderUserName.setText(currentUser.getDisplayName().toString());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, READ_CONTACT_PERMISSION);
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION);
        }
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
                            String shareSub = "https://play.google.com/store/apps/details?id=com.kalabhedia.gimme";
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareSub);
                            startActivity(Intent.createChooser(shareIntent, "Share Using"));
                        } else if (menuItem.getItemId() == R.id.nav_rate) {


                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);

                            //Copy App URL from Google Play Store.
                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.kalabhedia.gimme"));

                            startActivity(intent);
//                            Toast.makeText(MainActivity.this, "Feature to be added", Toast.LENGTH_SHORT).show();
                        } else if (menuItem.getItemId() == R.id.nav_logout) {
                            mAuth = FirebaseAuth.getInstance();
                            mAuth.signOut();
                            if (!isNetworkAvailable()) {
                                try {
                                    setMobileDataEnabled(getApplicationContext(), true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(currentUser.getUid());
                            reference.setValue(null);
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
        viewPager.setOffscreenPageLimit(2);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.AddFragment(new com.kalabhedia.gimme.OneFragment(), "Friends");
        adapter.AddFragment(new com.kalabhedia.gimme.ThreeFragment(), "Activity");
        adapter.AddFragment(new com.kalabhedia.gimme.TwoFragment(), "Explore");

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
        SharedPreferences sharedPref = context.getSharedPreferences("Gimme", Context.MODE_PRIVATE);
        if (sharedPref != null) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            HashMap<String, String> item;
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                item = new HashMap<>();
                item.put("Name", name);
                item.put("Number", number);
                if (!number.startsWith("+91")) {
                    number = "+91" + number;
                }
                contactdetails.add(item);
                String[] conversion = number.split(" ");
                String[] conversion1 = number.split("-");
                if (conversion1.length > 1) {
                    number = "";
                    for (String i : conversion1) {
                        number += i;
                    }
                } else if (conversion.length > 1) {
                    number = "";
                    for (String i : conversion) {
                        number += i;
                    }
                }
                editor.putString(number, name);
                editor.apply();
                cursor.moveToNext();
            }
        } else {
            Toast.makeText(context, "Unable to load", Toast.LENGTH_SHORT).show();
        }
        Log.w("Number Of Contacts", contactdetails.size() + "");
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    private boolean checkExternalPermission() {
        String permission = android.Manifest.permission.READ_CONTACTS;
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setMobileDataEnabled(Context context, boolean enabled) throws Exception {
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class conmanClass = null;
        try {
            conmanClass = Class.forName(conman.getClass().getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
        iConnectivityManagerField.setAccessible(true);
        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
        final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
    }

    public String getName(String phoneNumber) {
        String[] conversionNumber = phoneNumber.split(" ");
        phoneNumber = "";
        for (String i : conversionNumber) {
            phoneNumber += i;
        }
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Gimme", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString(phoneNumber, null);
        if (name == null) {
            return phoneNumber;
        } else {
            return name;
        }

    }

    private void Dataupdate() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Gimme", Context.MODE_PRIVATE);
        TreeSet<String> contactsContainingApp = new TreeSet<>();
        ArrayList<String> receiverKey = new ArrayList<>();
        ArrayList<String> phoneNumbers = new ArrayList<>();
        OnlineUserDataBase onlineUserDataBase = new OnlineUserDataBase(context);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("Users").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, ?> allEntries = sharedPreferences.getAll();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Log.w("Device numbers", data.child("device_number").getValue().toString());
                            String[] conversion = data.child("device_number").getValue().toString().split(" ");
                            String converted = "";
                            for (String i : conversion) {
                                converted += i;
                            }
//                            onlineUserDataBase.clearDatabase();
                            if (sharedPreferences.getString(converted, null) != null) {
                                contactsContainingApp.add(sharedPreferences.getString(converted, null));
                                onlineUserDataBase.insertData(converted, data.getKey(), 0);
                            }
                        }
//                        Cursor cr = onlineUserDataBase.getAllData();
//                        if (cr != null && cr.getCount() > 0) {
//                            cr.moveToFirst();
//                            while (!cr.isAfterLast()) {
//                                String numberTemp = cr.getString(0);
//                                if (sharedPreferences.getString(numberTemp, null) == null) {
//                                    onlineUserDataBase.deleteUser(numberTemp);
//                                }
//                                cr.moveToNext();
//                            }
//
//                        }
//                        Log.w("Contacts", allEntries.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("MyApp", "getUser:onCancelled", databaseError.toException());
                        Toast.makeText(context, "Unable to fetch users", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}