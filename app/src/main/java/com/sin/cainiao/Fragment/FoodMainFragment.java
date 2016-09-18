package com.sin.cainiao.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sin.cainiao.Activity.ShowDetailActivity;
import com.sin.cainiao.Activity.ShowProcessFoodDetailActivity;
import com.sin.cainiao.R;

import java.util.Random;


public class FoodMainFragment extends Fragment{
    private static final String TAG = "FoodMainFragment";
    private ImageView randomImg;

    public static FoodMainFragment newInstance() {
        
        Bundle args = new Bundle();
        
        FoodMainFragment fragment = new FoodMainFragment();
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
        View v = inflater.inflate(R.layout.food_main_fragment,container,false);
        randomImg = (ImageView)v.findViewById(R.id.img_random);
        randomImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                Integer number = random.nextInt(200);
                Log.i(TAG, "onClick: " + number);
                Intent intent = new Intent(getActivity(), ShowProcessFoodDetailActivity.class);
                intent.putExtra("FOOD_ITEM_ID",number.toString());
                startActivity(intent);
            }
        });
        return v;
    }
}
