package com.omatt.canicoffee;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {
    private final String TAG = "AboutFragment";

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        TextView mTextViewAboutDetails = (TextView) rootView.findViewById(R.id.tv_about_details);
        mTextViewAboutDetails.setMovementMethod(LinkMovementMethod.getInstance());

        TextView mTextViewAboutAppVersion = (TextView) rootView.findViewById(R.id.tv_about_app_version);

        PackageInfo packageInfo;
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String versionName = packageInfo.versionName;
            mTextViewAboutAppVersion.setText("version " + versionName);
            Log.i(TAG, "Get APP VERSION: " + versionName);
        } catch (Exception e) {
            Log.e(TAG, "Get APP VERSION error: " + e);
        }

        return rootView;
    }

}
