package com.sin.cainiao.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.sin.cainiao.Fragment.MaterialDetailFragment;
import com.sin.cainiao.JavaBean.CaiNiaoUser;
import com.sin.cainiao.JavaBean.Shop;
import com.sin.cainiao.R;
import com.sin.cainiao.Utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.bmob.v3.listener.UploadFileListener;

public class ShopRegisterActivity extends AppCompatActivity implements AMapLocationListener,LocationSource,
        PoiSearch.OnPoiSearchListener,AMap.OnMarkerClickListener,AMap.OnMapClickListener,AMap.InfoWindowAdapter
        , AMap.OnInfoWindowClickListener {
    private static final String TAG = "ShopRegisterActivity";
    private final static int SUCCESS = 1;
    private MapView mMapView;
    private AMap mAMap;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private PoiSearch mPoiSearch;
    private PoiSearch.Query query;
    private PoiResult mPoiResult;
    private LatLonPoint lp;
    private List<PoiItem> poiItems;
    private myPoiOverlay poiOverlay;
    private Marker locationMarker; // 选择的点
    private Marker detailMarker;
    private Marker mlastMarker;
    private boolean first = true;

    private RelativeLayout mPoiDetail;
    private TextView mPoiName, mPoiAddress;

    private Bundle savedInstanceState;

    private LocationSource.OnLocationChangedListener mLocationChangeListener = null;

    private EditText et_shop_name;
    private EditText et_shop_add;
    private EditText et_shop_desc;
    private ImageView img_cover;

    private Bitmap bitmap;

    private Shop shop;
    private CaiNiaoUser user;

    private String poiId;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SUCCESS){
                AMapLocation aMapLocation = (AMapLocation)msg.obj;
                initPOI(aMapLocation);
                doSearchQuery(aMapLocation.getCity());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.savedInstanceState = savedInstanceState;

        initView();
        user = BmobUser.getCurrentUser(CaiNiaoUser.class);
        shop = new Shop();
    }

    private void initView(){
        img_cover = (ImageView)findViewById(R.id.img_cover);
        et_shop_add = (EditText)findViewById(R.id.et_shop_add);
        et_shop_name = (EditText)findViewById(R.id.et_shop_name);
        et_shop_desc = (EditText)findViewById(R.id.et_shop_desc);

        img_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.PICK");
                intent.setType("image/*");
                startActivityForResult(intent, 100);
            }
        });

        Button bn_register = (Button)findViewById(R.id.bn_register);
        bn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setClickable(false);

                if (!(img_cover.getTag() instanceof String)){
                    Utils.toastShow(getApplicationContext(),"请选择一张封面！");
                    v.setClickable(true);
                    return;
                }

                if (et_shop_name.getText().toString().equals("") ||
                        et_shop_name.getText().toString().equals("") ||
                        et_shop_desc.getText().toString().equals("")){
                    Utils.toastShow(getApplicationContext(),"请检查你的信息!");
                    v.setClickable(true);
                } else {
                    final BmobFile bmobFile = new BmobFile(new File((String)img_cover.getTag()));
                    bmobFile.uploadblock(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e==null){
                                saveShop(bmobFile.getFileUrl());
                            }
                        }
                    });

                    BmobFile.uploadBatch(new String[]{(String) img_cover.getTag()}, new UploadBatchListener() {
                        @Override
                        public void onSuccess(List<BmobFile> list, List<String> list1) {
                            saveShop(list1.get(0));
                        }

                        @Override
                        public void onProgress(int i, int i1, int i2, int i3) {

                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });




                }
            }
        });
    }

    private void saveShop(String url){
        shop.setOwner(user);
        shop.setDesc(et_shop_desc.getText().toString());
        shop.setPoiId(poiId);
        shop.setName(et_shop_name.getText().toString());
        shop.setAdd(et_shop_add.getText().toString());
        shop.setShop_cover(url);
        shop.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    updateUser();
                    finish();
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null){
            bitmap = data.getParcelableExtra("data");
            if (bitmap == null){
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }

            img_cover.setImageBitmap(bitmap);
            img_cover.setTag(Utils.getUri(data.getData(),getContentResolver()));
        }
    }

    private void updateUser(){
        user.setShop(shop);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Utils.toastShow(getApplicationContext(),"商店创建成功!");
                }else{
                    Utils.toastShow(getApplicationContext(),"商店创建失败");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initMapView(savedInstanceState);
    }

    private void initMapView(Bundle savedInstanceState){
        mMapView = (MapView)findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        initMap();
        mPoiDetail = (RelativeLayout) findViewById(R.id.poi_detail);
        mPoiName = (TextView) findViewById(R.id.poi_name);
        mPoiAddress = (TextView) findViewById(R.id.poi_address);
    }

    private void initMap(){
        if (mAMap == null){
            mAMap = mMapView.getMap();
            initMyLocation();
        }
    }

    private void initLocation() {
        mLocationClient = new AMapLocationClient(this);
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setWifiActiveScan(true);
        mLocationOption.setMockEnable(false);
        mLocationOption.setInterval(2000);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(10));
    }

    private void initMyLocation() {
        mAMap.setLocationSource(this);
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);
        mAMap.setMyLocationEnabled(true);
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        mAMap.getUiSettings().setLogoPosition(
                AMapOptions.LOGO_POSITION_BOTTOM_LEFT);// logo位置
        mAMap.getUiSettings().setScaleControlsEnabled(true);// 标尺开关
        mAMap.getUiSettings().setCompassEnabled(true);// 指南针开关
        Log.d(TAG,
                "max = " + mAMap.getMaxZoomLevel() + "min = "
                        + mAMap.getMinZoomLevel());
    }

    private void doSearchQuery(String city){
        query = new PoiSearch.Query("市场","",city);
        query.setPageSize(20);
        query.setPageNum(0);

        if (lp != null){
            mPoiSearch = new PoiSearch(this,query);
            mPoiSearch.setOnPoiSearchListener(this);
            mPoiSearch.setBound(new PoiSearch.SearchBound(lp, 2500, true));//
            // 设置搜索区域为以lp点为圆心，其周围2500米范围
            mPoiSearch.searchPOIAsyn();// 异步搜索
        }

    }

    private void initPOI(AMapLocation aMapLocation){
        lp = new LatLonPoint(aMapLocation.getLatitude(),aMapLocation.getLongitude());
        mAMap.setOnMapClickListener(this);
        mAMap.setOnMarkerClickListener(this);
        mAMap.setOnInfoWindowClickListener(this);
        mAMap.setInfoWindowAdapter(this);
        locationMarker = mAMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.point4)))
                .position(new LatLng(lp.getLatitude(), lp.getLongitude())));
        locationMarker.showInfoWindow();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                if (first){
                    Message msg = new Message();
                    msg.what = SUCCESS;
                    msg.obj = aMapLocation;
                    mHandler.sendMessage(msg);
                    first = false;
                }
                mLocationChangeListener.onLocationChanged(aMapLocation);
            }else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mLocationChangeListener = onLocationChangedListener;
        if (mLocationClient == null){
            initLocation();
        }
    }

    @Override
    public void deactivate() {
        mLocationChangeListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (i == 1000) {
            if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                if (poiResult.getQuery().equals(query)) {// 是否是同一条
                    mPoiResult = poiResult;
                    poiItems = mPoiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始

                    if (poiItems != null && poiItems.size() > 0) {
                        //清除POI信息显示
                        whetherToShowDetailInfo(false);
                        //并还原点击marker样式
                        if (mlastMarker != null) {
                            resetLastMarker();
                        }
                        //清理之前搜索结果的marker
                        if (poiOverlay !=null) {
                            poiOverlay.removeFromMap();
                        }
                        mAMap.clear();
                        poiOverlay = new myPoiOverlay(mAMap, poiItems);
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();

                        mAMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory
                                        .fromBitmap(BitmapFactory.decodeResource(
                                                getResources(), R.drawable.point4)))
                                .position(new LatLng(lp.getLatitude(), lp.getLongitude())));

                        mAMap.addCircle(new CircleOptions()
                                .center(new LatLng(lp.getLatitude(),
                                        lp.getLongitude())).radius(2500)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.argb(50, 1, 1, 1))
                                .strokeWidth(2));
                    } else {
                        Utils.toastShow(this,
                                "对不起，没有搜索到相关数据！");
                    }
                }
            } else {
                Utils.toastShow(this, "对不起，没有搜索到相关数据！");
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        whetherToShowDetailInfo(false);
        if (mlastMarker != null) {
            resetLastMarker();
        }
    }

    private int[] markers = {R.drawable.poi_marker_1,
            R.drawable.poi_marker_2,
            R.drawable.poi_marker_3,
            R.drawable.poi_marker_4,
            R.drawable.poi_marker_5,
            R.drawable.poi_marker_6,
            R.drawable.poi_marker_7,
            R.drawable.poi_marker_8,
            R.drawable.poi_marker_9,
            R.drawable.poi_marker_10
    };

    private void whetherToShowDetailInfo(boolean isToShow) {
        if (isToShow) {
            mPoiDetail.setVisibility(View.VISIBLE);

        } else {
            mPoiDetail.setVisibility(View.GONE);
        }
    }

    private void resetLastMarker() {
        int index = poiOverlay.getPoiIndex(mlastMarker);
        if (index < 10) {
            mlastMarker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(
                            getResources(),
                            markers[index])));
        }else {
            mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight)));
        }
        mlastMarker = null;

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.i(TAG, "onInfoWindowClick: click" );
    }

    private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
        mPoiName.setText(mCurrentPoi.getTitle());
        Log.i(TAG, "setPoiItemDisplayContent: " + mCurrentPoi.getTitle() + mCurrentPoi.getSnippet());
        mPoiAddress.setText(mCurrentPoi.getSnippet());
        et_shop_name.setText(mCurrentPoi.getTitle());
        et_shop_add.setText(mCurrentPoi.getSnippet());
        poiId = mCurrentPoi.getPoiId();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getObject() != null) {
            whetherToShowDetailInfo(true);
            try {
                PoiItem mCurrentPoi = (PoiItem) marker.getObject();
                if (mlastMarker == null) {
                    mlastMarker = marker;
                } else {
                    // 将之前被点击的marker置为原来的状态
                    resetLastMarker();
                    mlastMarker = marker;
                }
                detailMarker = marker;
                detailMarker.setIcon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                getResources(),
                                R.drawable.poi_marker_pressed)));

                setPoiItemDisplayContent(mCurrentPoi);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            whetherToShowDetailInfo(false);
            resetLastMarker();
        }

        return true;
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }



    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        if(mLocationClient!=null){
            mLocationClient.stopLocation();
        }
        deactivate();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mLocationClient!=null){
            mLocationClient.startLocation();
        }
        mMapView.onResume();
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        whetherToShowDetailInfo(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mLocationClient!=null){
            mLocationClient.onDestroy();
        }
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private class myPoiOverlay {
        private AMap mamap;
        private List<PoiItem> mPois;
        private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
        public myPoiOverlay(AMap amap ,List<PoiItem> pois) {
            mamap = amap;
            mPois = pois;
        }

        /**
         * 添加Marker到地图中。
         * @since V2.1.0
         */
        public void addToMap() {
            for (int i = 0; i < mPois.size(); i++) {
                Marker marker = mamap.addMarker(getMarkerOptions(i));
                PoiItem item = mPois.get(i);
                marker.setObject(item);
                mPoiMarks.add(marker);
            }
        }

        /**
         * 去掉PoiOverlay上所有的Marker。
         *
         * @since V2.1.0
         */
        public void removeFromMap() {
            for (Marker mark : mPoiMarks) {
                mark.remove();
            }
        }

        /**
         * 移动镜头到当前的视角。
         * @since V2.1.0
         */
        public void zoomToSpan() {
            if (mPois != null && mPois.size() > 0) {
                if (mamap == null)
                    return;
                LatLngBounds bounds = getLatLngBounds();
                mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }

        private LatLngBounds getLatLngBounds() {
            LatLngBounds.Builder b = LatLngBounds.builder();
            for (int i = 0; i < mPois.size(); i++) {
                b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
                        mPois.get(i).getLatLonPoint().getLongitude()));
            }
            return b.build();
        }

        private MarkerOptions getMarkerOptions(int index) {
            return new MarkerOptions()
                    .position(
                            new LatLng(mPois.get(index).getLatLonPoint()
                                    .getLatitude(), mPois.get(index)
                                    .getLatLonPoint().getLongitude()))
                    .title(getTitle(index)).snippet(getSnippet(index))
                    .icon(getBitmapDescriptor(index));
        }

        protected String getTitle(int index) {
            return mPois.get(index).getTitle();
        }

        protected String getSnippet(int index) {
            return mPois.get(index).getSnippet();
        }

        /**
         * 从marker中得到poi在list的位置。
         *
         * @param marker 一个标记的对象。
         * @return 返回该marker对应的poi在list的位置。
         * @since V2.1.0
         */
        public int getPoiIndex(Marker marker) {
            for (int i = 0; i < mPoiMarks.size(); i++) {
                if (mPoiMarks.get(i).equals(marker)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 返回第index的poi的信息。
         * @param index 第几个poi。
         * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
         * @since V2.1.0
         */
        public PoiItem getPoiItem(int index) {
            if (index < 0 || index >= mPois.size()) {
                return null;
            }
            return mPois.get(index);
        }

        protected BitmapDescriptor getBitmapDescriptor(int arg0) {
            if (arg0 < 10) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), markers[arg0]));
                return icon;
            }else {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight));
                return icon;
            }
        }
    }

}
