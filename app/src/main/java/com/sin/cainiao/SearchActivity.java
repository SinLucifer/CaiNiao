package com.sin.cainiao;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.sin.cainiao.Adapter.FoodSearchResultAdapter;
import com.sin.cainiao.JavaBean.Food;
import com.sin.cainiao.DataHelper.FoodDataHelper;
import com.sin.cainiao.JavaBean.FoodItem;
import com.sin.cainiao.JavaBean.Material;
import com.sin.cainiao.DataHelper.MaterialDataHelper;
import com.sin.cainiao.Adapter.MaterialSuggestion;

import java.util.List;


public class SearchActivity extends AppCompatActivity {
    private final static String TAG = "SearchActivity";
    public final static String POSITION = "Position";
    public final static int RESULT_SUCCESS = 1;
    public final static int RESULT_ERROR = 0;

    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;

    private FloatingSearchView mSearchView;
    private RecyclerView mRecyclerView;
    private FoodSearchResultAdapter mFoodSearchAdapter;
    private boolean search_type = true;// true = food;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "handleMessage: " + msg.what);
            if (msg.what == RESULT_SUCCESS){
                mFoodSearchAdapter.swapData((List<Food.ShowapiResBodyBean.CbListBean>)msg.obj);
                mRecyclerView.scrollToPosition(0);
            }else if (msg.what == RESULT_ERROR){
                Toast.makeText(getApplicationContext(),"暂时还没有这种美食哦~快来完善吧~",Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_activity);

        Intent intent = getIntent();
        if (intent.getExtras().getInt(POSITION) == 0){
            search_type = true;
        }else{
            search_type = false;
        }

        mRecyclerView = (RecyclerView)findViewById(R.id.search_results_list) ;
        setupSearchView();
        setupResultsList();
    }

    public void setupSearchView(){
        mSearchView = (FloatingSearchView)findViewById(R.id.mFloating_search);

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if (search_type){
                    //TODO
                }else{
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

                    Log.d(TAG, "onSearchTextChanged()");
                }
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                if (search_type){
                    // TODO: 2016/9/3
                }else {
                    MaterialSuggestion materialSuggestion = (MaterialSuggestion) searchSuggestion;
                    MaterialDataHelper.findMaterials(getApplicationContext()
                            , materialSuggestion.getBody(), new MaterialDataHelper.onFindMaterialsListener() {
                                @Override
                                public void onResults(List<Material> results, boolean status) {
                                    if (status) {
                                        for (Material material : results) {
                                            Log.i(TAG, material.getFood_Name() + "\n" +
                                                    material.getPrice() + "\n" +
                                                    material.getDesc() + "\n" +
                                                    material.getSkill());
                                        }
                                    } else {
                                        Log.i(TAG, "onResults: No such food");
                                    }
                                }
                            });
                }
            }

            @Override
            public void onSearchAction(String currentQuery) {
                if (search_type){
                    FoodDataHelper.findFoods(getApplicationContext(), currentQuery, new FoodDataHelper.onFindFoodsListener() {
                        @Override
                        public void onGroupResult(List<Food.ShowapiResBodyBean.CbListBean> foodList, boolean status) {
                            Message msg = new Message();
                            if (status){
                                for (Food.ShowapiResBodyBean.CbListBean food : foodList){
                                    Log.i(TAG, "onResult: " + food.getName()+"\n"+food.getCl()+"\n"
                                    +food.getZf());
                                }
                                msg.what = RESULT_SUCCESS;
                                msg.obj = foodList;
                                handler.sendMessage(msg);
                            }else {
                                msg.what = RESULT_ERROR;
                                handler.sendMessage(msg);
                            }
                        }

                        @Override
                        public void onSingleResult(FoodItem.ShowapiResBodyBean item, boolean status) {}
                    });
                }else{
                    MaterialDataHelper.findMaterials(getApplicationContext()
                            , currentQuery, new MaterialDataHelper.onFindMaterialsListener() {
                                @Override
                                public void onResults(List<Material> results, boolean status) {
                                    if (status){
                                        for (Material material : results) {
                                            Log.i(TAG, material.getFood_Name()+"\n"+
                                                    material.getPrice()+"\n"+
                                                    material.getDesc()+"\n"+
                                                    material.getSkill());
                                        }
                                    }else {
                                        Log.i(TAG, "onResults: No such food");
                                    }
                                }
                            });
                }
            }
        });
    }

    public void setupResultsList(){
        mFoodSearchAdapter = new FoodSearchResultAdapter(this);
        mRecyclerView.setAdapter(mFoodSearchAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mFoodSearchAdapter.setOnFoodItemClickListener(new FoodSearchResultAdapter.onFoodItemClickListener() {
            @Override
            public void onClick(Food.ShowapiResBodyBean.CbListBean food) {
                Intent intent = new Intent(SearchActivity.this,ShowDetailActivity.class);
                intent.putExtra("FOOD_ITEM_ID",food.getCbId());
                startActivity(intent);
            }
        });
    }
}
