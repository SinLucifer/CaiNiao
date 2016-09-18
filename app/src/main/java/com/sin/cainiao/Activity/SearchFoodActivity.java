package com.sin.cainiao.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.sin.cainiao.Adapter.FoodSearchResultAdapter;
import com.sin.cainiao.JavaBean.Food;
import com.sin.cainiao.DataHelper.FoodDataHelper;
import com.sin.cainiao.JavaBean.FoodItem;
import com.sin.cainiao.JavaBean.ProcessedFood;
import com.sin.cainiao.R;

import java.util.List;


public class SearchFoodActivity extends AppCompatActivity {
    private final static String TAG = "SearchFoodActivity";
    public final static int RESULT_SUCCESS = 1;
    public final static int BMOB_RESULT_SUCCESS = 2;
    public final static int RESULT_ERROR = 0;
    private boolean search_flag = true; //true == food

    private FloatingSearchView mSearchView;
    private RecyclerView mRecyclerView;
    private FoodSearchResultAdapter mFoodSearchAdapter;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "handleMessage: " + msg.what);
            if (msg.what == RESULT_SUCCESS){
                mFoodSearchAdapter.swapOldData((List<Food.ShowapiResBodyBean.CbListBean>) msg.obj);
                mRecyclerView.scrollToPosition(0);
            }else if (msg.what == BMOB_RESULT_SUCCESS){
                List<ProcessedFood> list = (List<ProcessedFood>)msg.obj;
                Log.i(TAG, "handleMessage: " + list.size());
                mFoodSearchAdapter.swapProcessedData((List<ProcessedFood>)msg.obj);
                mRecyclerView.scrollToPosition(0);
            }else if(msg.what == RESULT_ERROR){
                Toast.makeText(getApplicationContext(),"暂时还没有这种美食哦~快来完善吧~",Toast.LENGTH_SHORT).show();
            }

            mSearchView.hideProgress();
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_food_activity);

        mRecyclerView = (RecyclerView)findViewById(R.id.search_results_list) ;
        setupSearchView();
        setupResultsList();
    }

    public void setupSearchView(){
        mSearchView = (FloatingSearchView)findViewById(R.id.mFloating_search);


        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
            }

            @Override
            public void onSearchAction(String currentQuery) {
                mSearchView.showProgress();
                if (search_flag){
                    Log.i(TAG, "onSearchAction: Old search");
                    FoodDataHelper.findFoods(getApplicationContext(), currentQuery, new FoodDataHelper.onFindFoodsListener() {
                        @Override
                        public void onGroupResult(List<Food.ShowapiResBodyBean.CbListBean> foodList, boolean status) {
                            Message msg = new Message();
                            if (status) {
                                for (Food.ShowapiResBodyBean.CbListBean food : foodList) {
                                    Log.i(TAG, "onResult: " + food.getName() + "\n" + food.getCl() + "\n"
                                            + food.getZf());
                                }
                                msg.what = RESULT_SUCCESS;
                                msg.obj = foodList;
                                handler.sendMessage(msg);
                            } else {
                                msg.what = RESULT_ERROR;
                                handler.sendMessage(msg);
                            }
                        }

                        @Override
                        public void onSingleResult(FoodItem.ShowapiResBodyBean item, boolean status) {                      }
                    });
                }else{
                    Log.i(TAG, "onSearchAction: new Search");
                    FoodDataHelper.findProcessFoodByMaterial(currentQuery, new FoodDataHelper.onFindProcessFoodListener() {
                        @Override
                        public void onSingleResult(ProcessedFood item, boolean status) {

                        }

                        @Override
                        public void onGroupResult(List<ProcessedFood> foodList, boolean status) {
                            Message msg = new Message();
                            if (status) {
                                msg.what = BMOB_RESULT_SUCCESS;
                                msg.obj = foodList;
                                handler.sendMessage(msg);
                            } else {
                                msg.what = RESULT_ERROR;
                                handler.sendMessage(msg);
                            }
                        }
                    });
                }
            }
        });

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_food){
                    search_flag = true;
                }else if (id == R.id.action_material){
                    search_flag = false;
                }
            }
        });
    }

    public void setupResultsList(){
        mFoodSearchAdapter = new FoodSearchResultAdapter(this);
        mRecyclerView.setAdapter(mFoodSearchAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mFoodSearchAdapter.setOnOldFoodItemClickListener(new FoodSearchResultAdapter.onOldFoodItemClickListener() {
            @Override
            public void onClick(Food.ShowapiResBodyBean.CbListBean food) {
                Intent intent = new Intent(SearchFoodActivity.this,ShowDetailActivity.class);
                intent.putExtra("FOOD_ITEM_ID",food.getCbId());
                startActivity(intent);
            }
        });

        mFoodSearchAdapter.setOnNewFoodItemClickListener(new FoodSearchResultAdapter.onNewFoodItemClickListener() {
            @Override
            public void onClick(ProcessedFood food) {
                Intent intent = new Intent(SearchFoodActivity.this,ShowProcessFoodDetailActivity.class);
                Integer number = food.getNumber();
                intent.putExtra("FOOD_ITEM_ID",number.toString());
                startActivity(intent);
            }
        });

    }
}
