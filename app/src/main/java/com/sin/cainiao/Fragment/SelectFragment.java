package com.sin.cainiao.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

public class SelectFragment extends DialogFragment{
    private ListAdapter typeAdapter;
    private String[] types = {"家常菜","快手菜","下饭菜"};

    private onClickListener mListener;

    public interface onClickListener{
        void onClick(String type);
    }

    public static SelectFragment newInstance() {

        Bundle args = new Bundle();

        SelectFragment fragment = new SelectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,
                types);
        if (getActivity() instanceof onClickListener){
            mListener = (onClickListener) getActivity();
        }else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement onClickListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("菜品类型")
                .setAdapter(typeAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String type = null;
                        switch (which){
                            case 0:
                                type = "jxc";
                                break;
                            case 1:
                                type = "ksc";
                                break;
                            case 2:
                                type = "xfc";
                                break;
                        }
                        mListener.onClick(type);
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}
