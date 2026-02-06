package com.sp.learntogether;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.sp.learntogether.data.AppDatabase;
import com.sp.learntogether.data.TrackingDao;
import com.sp.learntogether.io.DatabaseInteractor;
import com.sp.learntogether.ui.profile.ProfileFragment;

import com.google.android.material.navigation.NavigationBarView;
import com.sp.learntogether.databinding.ActivityMainBinding;

import org.json.JSONException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppBarConfiguration appBar;
    private NavController navController;
    private DrawerLayout drawer;
    FragmentManager fragmentManager;
    BottomNavigationView botNavView;
    private TrackingDao dao;
    private DatabaseInteractor dbIO;
    private FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RequestQueue queue = Volley.newRequestQueue(this);


        navController = NavHostFragment.findNavController(getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main));
        int[] topLevel ={R.id.planner_fragment,
                R.id.meetupsFragment,
                R.id.communities_fragment,
                R.id.profile_fragment,
                R.id.loginFragment};

        List<Integer> a = Arrays.asList(R.id.planner_fragment,
                R.id.meetupsFragment,
                R.id.communities_fragment,
                R.id.profile_fragment,
                R.id.loginFragment);
        appBar = new AppBarConfiguration.Builder(
                topLevel
        )
                .setOpenableLayout(binding.drawerLayout)
                .build();

        dbIO = DatabaseInteractor.getInstance(this);

        NavigationUI.setupWithNavController(binding.navDrawer, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBar);

        NavigationUI.setupWithNavController(binding.navView, navController);
        binding.navView.setOnItemReselectedListener(v -> {});


        NavigationView navigationView = binding.navDrawer;
        View hView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) hView.findViewById(R.id.username_in_header);
        ImageView nav_profile = (ImageView) hView.findViewById(R.id.pfp_in_header);
        dbIO.getRow(response -> {
            try {
                nav_user.setText(response.getJSONArray("data").getJSONObject(0).getString("username"));
                ImageRequest req = new ImageRequest(response.getJSONArray("data").getJSONObject(0).getString("profilepicurl"), bitmap -> {
                    nav_profile.setImageBitmap(bitmap);
                }, 400, 400, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888, Throwable::printStackTrace);
                queue.add(req);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, astraDBHelper.userDetailsUrl, auth.getUid());
        binding.navDrawer.setNavigationItemSelectedListener(item -> {

            binding.drawerLayout.closeDrawers();
            if (item.getItemId() == R.id.ai) {
                navController.navigate(R.id.generativeChatFragment);
                return true;
            } else if (item.getItemId() == R.id.library) {
                navController.navigate(R.id.libraryFragment);
            } else if(item.getItemId()==R.id.about){
                navController.navigate(R.id.aboutFragment);
                binding.drawerLayout.closeDrawers();
                return true;
            }
            return false;
        });

        botNavView = binding.navView;


        fragmentManager = getSupportFragmentManager();


        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            int id = navDestination.getId();
            if (id == R.id.loginFragment || id == R.id.registerFragment) {
                Objects.requireNonNull(getSupportActionBar()).hide();
                binding.navView.setVisibility(View.GONE);
            }
            if (a.contains(id)) {
                Objects.requireNonNull(getSupportActionBar()).show();
                binding.navView.setVisibility(View.VISIBLE);
            } else {
                Objects.requireNonNull(getSupportActionBar()).show();
                binding.navView.setVisibility(View.GONE);
            }
        });

        dao = AppDatabase.getInstance(this).trackingDao();
        new Thread(() -> {
            if (dao.getFuture(new Date().getTime()).size() > 0) {
                reqPm();
            }
        }).start();
        App.startTracking();

    }

    private void openFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        transaction.commit();
    }


    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBar) || super.onSupportNavigateUp();
    }


    private final ActivityResultLauncher<String[]> permissionRequestLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), granted -> {
        Log.i(TAG, "perms return" + Arrays.toString(granted.values().toArray()));
        if (ContextCompat.checkSelfPermission(this, App.BACKGND_LOC_PM) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, ContextCompat.checkSelfPermission(this, App.BACKGND_LOC_PM) + App.BACKGND_LOC_PM);
            App.startTracking();
        } else {
            boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (showRationale) {
                showPermissionsRationale();
            } else {
                // Permanent denial!
                Snackbar sb = Snackbar.make(binding.getRoot(), R.string.loc_permanent_denial_promt, Snackbar.LENGTH_SHORT);
                sb.setAction(R.string.grant, v -> {
                    launchAppInfo();
                });
                sb.show();
            }
        }
    });

    private void launchAppInfo() {
        Toast.makeText(this, "Please enable location permission all the time.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri pkg = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(pkg);
        startActivity(intent);
    }

    private void showPermissionsRationale() {
        runOnUiThread(() -> {

            AlertDialog alertDialog = new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.permissions_required)
                    .setMessage(R.string.location_meetup_rationale)
                    .setPositiveButton(R.string.grant, (di, which) -> {
                        launchAppInfo();
                    })
                    .setCancelable(false)
                    .create();
            alertDialog.show();
        });

    }



    private void requestLocation() {
        Log.i(TAG, "requestLocation");
        permissionRequestLauncher.launch(App.PERMISSIONS);
    }


    private static final String TAG = "MeetupsFragment";
    private void reqPm() {
        Log.i(TAG, "reqPm");
        int granted = ContextCompat.checkSelfPermission(this, App.BACKGND_LOC_PM);
        if (granted == PackageManager.PERMISSION_GRANTED) {
            App.startTracking();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Show the rationale to the user
            showPermissionsRationale();
        } else {
            requestLocation();
        }
    }



}