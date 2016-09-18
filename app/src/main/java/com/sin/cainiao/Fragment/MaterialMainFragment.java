package com.sin.cainiao.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sin.cainiao.R;


public class MaterialMainFragment extends Fragment {

    public static MaterialMainFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MaterialMainFragment fragment = new MaterialMainFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.material_main_fragment,container,false);
        return v;
    }
}
