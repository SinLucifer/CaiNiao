package com.sin.cainiao.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sin.cainiao.R;

/**
 * Created by Sin on 2016/9/10.
 */
public class MaterialDetailFragment extends Fragment {
    private final static String TAG = "MaterialDetailFragment";

    public static MaterialDetailFragment newInstance(String cl) {
        Bundle args = new Bundle();
        args.putString("cl",cl);
        MaterialDetailFragment fragment = new MaterialDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static MaterialDetailFragment newInstance() {
        Bundle args = new Bundle();
        MaterialDetailFragment fragment = new MaterialDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_main2,container,false);

        Bundle bundle = getArguments();
        String cl = bundle.getString("cl");
        Log.i(TAG, "onCreateView: " + cl);
        TextView tv = (TextView)mView.findViewById(R.id.section_label);
        tv.setText(cl);
        return mView;
    }
}
