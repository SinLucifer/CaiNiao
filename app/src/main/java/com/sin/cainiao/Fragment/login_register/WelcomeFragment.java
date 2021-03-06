package com.sin.cainiao.fragment.login_register;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sin.cainiao.R;
import com.sin.cainiao.utils.View.BackHandledFragment;

public class WelcomeFragment extends BackHandledFragment {
    private static final String TAG = "WelcomeFragment";

    private onWelcomeCallback callback;

    public interface onWelcomeCallback{
        void onCallBack(String uri);
    }


    public WelcomeFragment() {

    }

    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_welcome, container, false);
        Button login = (Button)view.findViewById(R.id.bn_login);
        Button register = (Button)view.findViewById(R.id.bn_register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCallBack("login");
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCallBack("register");
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onWelcomeCallback) {
            callback = (onWelcomeCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }
}
