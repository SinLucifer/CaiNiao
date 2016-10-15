package com.sin.cainiao.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.sin.cainiao.R;

import java.util.ArrayList;
import java.util.List;

public class EditMaterialItemActivity extends AppCompatActivity {
    private static final String TAG = "EditFoodActivity";
    private List<ViewHolder> ls_item;
    private List<View> ls_childView;
    private View childView;
    private LayoutInflater inflater;
    private LinearLayout ll_material_container;
    int mark = 0;

    private ArrayList<String> nameList;
    private ArrayList<String> unitList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_material);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList<String> nameList;
        ArrayList<String> unitList;

        ls_item = new ArrayList<>();
        ls_childView = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null){
            nameList = intent.getStringArrayListExtra("name");
            unitList = intent.getStringArrayListExtra("unit");
        }else {
            nameList = new ArrayList<>();
            unitList = new ArrayList<>();
        }

        ll_material_container = (LinearLayout)findViewById(R.id.ll_material_input_container);
        inflater = LayoutInflater.from(getApplicationContext());

        if (nameList.size() == 0){
            for (int i = 0; i < 3; i++) {
                childView = inflater.inflate(R.layout.edit_material_item,null);
                childView.setId(mark);
                getViewInstance(childView);
                ll_material_container.addView(childView);
                mark++;
            }
        }else {
            for (int i = 0; i < nameList.size(); i++) {
                childView = inflater.inflate(R.layout.edit_material_item,null);
                childView.setId(mark);
                getViewInstanceByString(childView,nameList.get(i),unitList.get(i));
                ll_material_container.addView(childView);
                mark++;
            }
        }


        Button bn_material_add = (Button)findViewById(R.id.bn_material_add);
        Button bn_material_remove = (Button)findViewById(R.id.bn_material_remove);
        Button bn_material_save = (Button)findViewById(R.id.bn_material_save);

        bn_material_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mark++;
                childView = inflater.inflate(R.layout.edit_material_item,null);
                childView.setId(mark);
                ll_material_container.addView(childView,mark-1);
                getViewInstance(childView);
            }
        });

        bn_material_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll_material_container.getChildCount() > 1){
                    mark--;
                    ll_material_container.removeView(ls_childView.get(ls_childView.size()-1));
                    ls_item.remove(ls_item.size()-1);
                    ls_childView.remove(ls_childView.size()-1);
                }
            }
        });

        bn_material_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> nameList = new ArrayList<String>();
                ArrayList<String> unitList = new ArrayList<String>();
                for (int i = 0; i < ll_material_container.getChildCount(); i++) {
                    ViewHolder vh = ls_item.get(i);
                    if (!vh.name.getText().toString().equals("")){
                        nameList.add(vh.name.getText().toString());
                        unitList.add(vh.unit.getText().toString());
                    }
                }

                Log.i(TAG, "onClick: " + nameList);

                Intent intent = getIntent();
                intent.putStringArrayListExtra("name",nameList);
                intent.putStringArrayListExtra("unit",unitList);
                setResult(1,intent);
                finish();
            }
        });


    }

    private void getViewInstance(final View childView){
        ViewHolder vh = new ViewHolder();
        vh.id = childView.getId();
        vh.name = (EditText) childView.findViewById(R.id.et_name);
        vh.unit = (EditText) childView.findViewById(R.id.et_unit);

        ls_item.add(vh);
        ls_childView.add(childView);
    }

    private void getViewInstanceByString(View childView,String name,String unit){
        ViewHolder vh = new ViewHolder();
        vh.id = childView.getId();
        vh.name = (EditText) childView.findViewById(R.id.et_name);
        vh.unit = (EditText) childView.findViewById(R.id.et_unit);

        vh.name.setText(name);
        vh.unit.setText(unit);

        ls_item.add(vh);
        ls_childView.add(childView);
    }

    public class ViewHolder {
        public int id;
        public  EditText name;
        public  EditText unit;
    }

}
