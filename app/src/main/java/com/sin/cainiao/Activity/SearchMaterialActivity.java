package com.sin.cainiao.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.sin.cainiao.Adapter.MaterialSuggestion;
import com.sin.cainiao.DataHelper.MaterialDataHelper;
import com.sin.cainiao.Fragment.MaterialDetailFragment;
import com.sin.cainiao.JavaBean.Material;
import com.sin.cainiao.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class SearchMaterialActivity extends AppCompatActivity {
    private final static String TAG = "SearchMaterialActivity";

    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;

    private FloatingSearchView mSearchView;
    private MaterialDetailFragment mDetailFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_material_activity);
        setupSearchView();
        mDetailFragment = MaterialDetailFragment.newInstance();

        Intent intent = getIntent();
        List<String> cl = (List<String>)intent.getSerializableExtra("cl");

        if (cl != null){
            Log.i(TAG, "onCreate: " + cl.size());
            if(cl.size() == 1){
                MaterialDataHelper.findMaterials(this, cl.get(0), new MaterialDataHelper.onFindMaterialsListener() {
                    @Override
                    public void onResults(Material result, boolean status) {
                        if (status){
                            mDetailFragment = MaterialDetailFragment.newInstance(result);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.search_material_container,mDetailFragment).commit();
                        }else {
                            Log.i(TAG, "onResults: No such food");
                        }
                    }
                });
            }else{
                List<MaterialSuggestion> materialSuggestions = new ArrayList<>();
                for (String tempCl : cl) {
                    materialSuggestions.add(new MaterialSuggestion(tempCl));
                }
                mSearchView.setSearchFocused(true);
                mSearchView.swapSuggestions(materialSuggestions);
            }
        }
        // TODO: 2016/9/22 传对象 
        String material_name = intent.getStringExtra("MATERIAL_NAME");
        Log.i(TAG, "onCreate: " + material_name);
        if (material_name != null && !material_name.equals("")){
            MaterialDataHelper.findMaterials(this, material_name, new MaterialDataHelper.onFindMaterialsListener() {
                @Override
                public void onResults(Material result, boolean status) {
                    if (status){
                        mDetailFragment = MaterialDetailFragment.newInstance(result);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.search_material_container,mDetailFragment).commit();
                    }else {
                        Log.i(TAG, "onResults: No such food");
                    }
                }
            });
        }
    }

    public void setupSearchView(){
        mSearchView = (FloatingSearchView)findViewById(R.id.mFloating_search);

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {
                    mSearchView.showProgress();
                    MaterialDataHelper.findSuggestions(getApplicationContext(), newQuery, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, new MaterialDataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<MaterialSuggestion> results) {
                                    mSearchView.swapSuggestions(results);
                                    mSearchView.hideProgress();
                                }
                            });
                }
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                MaterialSuggestion materialSuggestion = (MaterialSuggestion) searchSuggestion;
                MaterialDataHelper.findMaterials(getApplicationContext()
                        , materialSuggestion.getBody(), new MaterialDataHelper.onFindMaterialsListener() {
                            @Override
                            public void onResults(Material result, boolean status) {
                                if (status) {
                                    mDetailFragment = MaterialDetailFragment
                                            .newInstance(result);
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.search_material_container,mDetailFragment)
                                            .commit();
                                } else {
                                    Log.i(TAG, "onResults: No such food");
                                }
                            }
                        });
            }

            @Override
            public void onSearchAction(String currentQuery) {
                MaterialDataHelper.findMaterials(getApplicationContext()
                        , currentQuery, new MaterialDataHelper.onFindMaterialsListener() {
                            @Override
                            public void onResults(Material result, boolean status) {
                                if (status){
                                        mDetailFragment = MaterialDetailFragment
                                                .newInstance(result);
                                        getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.search_material_container,mDetailFragment)
                                                .commit();
                                        Log.i(TAG, "onResults: " + result);
                                }else {
                                    Log.i(TAG, "onResults: No such food");
                                }
                            }
                        });
            }
        });
    }
}
