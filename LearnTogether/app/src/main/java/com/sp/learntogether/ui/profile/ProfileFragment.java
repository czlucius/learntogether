package com.sp.learntogether.ui.profile;

import static com.sp.learntogether.Utils.strArrFromJsonArray;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.sp.learntogether.BR;
import com.sp.learntogether.R;
import com.sp.learntogether.astraDBHelper;
import com.sp.learntogether.data.AppDatabase;
import com.sp.learntogether.data.TrackingDao;
import com.sp.learntogether.databinding.FragmentProfileBinding;
import com.sp.learntogether.io.DatabaseInteractor;
import com.sp.learntogether.models.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private ProfileViewModel vm;
    private FragmentProfileBinding binding;
    private DatabaseInteractor dbIO;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private Profile profile = null;

    private TrackingDao trackingDao;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        trackingDao = AppDatabase.getInstance(requireContext()).trackingDao();
//        ProfileFragmentArgs

        return binding.getRoot();
    }

    private ProfileFragmentArgs args;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbIO = DatabaseInteractor.getInstance(getContext());
        args = ProfileFragmentArgs.fromBundle(getArguments());
        Log.i(TAG, "onViewCreated: profile is " + args.getProfile());
        if (args.getProfile() == null) {
            Log.i(TAG, "onViewCreated: No profile supplied, fetching own profile");
            getMyProfile();
        }
        NavController nc = NavHostFragment.findNavController(this);
        binding.logout.setOnClickListener(v -> {
            trackingDao.clear();
            auth.signOut();
            nc.navigate(R.id.action_profile_fragment_to_loginFragment);
        });
    }
    private void useProfile(Profile profile) {
        this.profile = profile;
        binding.setProfile(profile);
        binding.notifyPropertyChanged(BR.profile);
    }


    public void getProfileOf(String uid) {

        dbIO.getRow(response -> {
            JSONArray arr;
            try {
                arr = response.getJSONArray("data");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            /*
            (
    uid text PRIMARY KEY,
    email text,
    fcmtoken text,
    friends list<text>,
    hasface boolean,
    idtoken text,
    meetups list<text>,
    name text,
    profilepicurl text,
    username text
)
             */

            if (arr.length() >= 1) {
                // get profile here.
                JSONObject obj;
                try {
                    obj = arr.getJSONObject(0);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "JSON decoding failed: " + e.getMessage());
                    throw new RuntimeException(e);
                }
                try {
                    profile  = Profile.Companion.fromJsonObject(obj);

                    useProfile(profile);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, astraDBHelper.userDetailsUrl, uid);
    }


    public void getMyProfile() {
        getProfileOf(auth.getUid());
    }


}