package com.omatt.canicoffee.modules.about;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omatt.canicoffee.R;

import java.util.Objects;

/**
 * Created by Omatt on 4/10/2015.
 * About
 */

public class AboutFragment extends Fragment {
    private final String TAG = "AboutFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        TextView mTextViewAboutDetails = rootView.findViewById(R.id.tv_about_details);
        mTextViewAboutDetails.setMovementMethod(LinkMovementMethod.getInstance());

        TextView mTextViewAboutAppVersion = rootView.findViewById(R.id.tv_about_app_version);

        PackageInfo packageInfo;
        try {
            packageInfo = Objects.requireNonNull(getActivity()).getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String versionName = packageInfo.versionName;
            String version = "version " + versionName;
            mTextViewAboutAppVersion.setText(version);
            Log.i(TAG, "Get APP VERSION: " + versionName);
        } catch (Exception e) {
            Log.e(TAG, "Get APP VERSION error: " + e);
        }

        return rootView;
    }

}
