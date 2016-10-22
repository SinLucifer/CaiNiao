package com.sin.cainiao.fragment.login_register;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sin.cainiao.javaBean.CaiNiaoUser;
import com.sin.cainiao.R;
import com.sin.cainiao.utils.CustomApplication;
import com.sin.cainiao.utils.Utils;
import com.sin.cainiao.utils.View.BackHandledFragment;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class LoginFragment extends BackHandledFragment {
    private final static String TAG = "LoginFragment";

    private EditText et_login_name;
    private EditText et_login_password;
    private TextView tv_forget;

    private onLoginCallBack mCallBack;

    private CustomApplication app;


    public LoginFragment() {

    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView =  inflater.inflate(R.layout.fragment_login, container, false);

        app = (CustomApplication)getActivity().getApplication();

        setupView(mView);
        return mView;
    }

    private void setupView(View view){

        et_login_name = (EditText)view.findViewById(R.id.et_login_username);
        et_login_password = (EditText)view.findViewById(R.id.et_login_password);

        Button bn_login = (Button) view.findViewById(R.id.bn_login_submit);
        bn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_login_name.getText().toString();
                String password = et_login_password.getText().toString();

                if (username.equals("") || password.equals("")){
                    Utils.toastShow(getContext(),"请检查你的输入");
                }else {
                    BmobUser.loginByAccount(username, password, new LogInListener<CaiNiaoUser>() {
                        @Override
                        public void done(CaiNiaoUser caiNiaoUser, BmobException e) {
                            if(caiNiaoUser!=null){
                                Utils.toastShow(getContext(),"登陆成功");
                                app.setUser(caiNiaoUser);
                                Log.i(TAG, "done: " + caiNiaoUser.getMobilePhoneNumber() + caiNiaoUser.getSessionToken());

                                if (mCallBack != null){
                                    mCallBack.onCallBack("login_success");
                                }

                            }else {
                                Utils.toastShow(getContext(),e.getMessage());
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onLoginCallBack) {
            mCallBack = (onLoginCallBack) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public interface  onLoginCallBack{
        void onCallBack(String uri);
    }
}
