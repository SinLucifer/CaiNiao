package com.sin.cainiao.Fragment.login_register;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sin.cainiao.JavaBean.CaiNiaoUser;
import com.sin.cainiao.R;
import com.sin.cainiao.Utils.CustomApplication;
import com.sin.cainiao.Utils.View.TimeButton;
import com.sin.cainiao.Utils.Utils;
import com.sin.cainiao.Utils.View.BackHandledFragment;

import java.util.Map;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;


public class RegisterFragment extends BackHandledFragment {
    private static final String TAG = "RegisterFragment";

    private onRegisterCallback mCallback;
    private CustomApplication app;

    public static Map<String, Long> map;

    private EditText et_phone_number;
    private EditText et_pin;
    private EditText et_password;
    private TimeButton bn_send;
    private Button bn_register;
    private SwitchCompat sw_reg;

    private String phone_number;
    private String password;

    public RegisterFragment() {

    }


    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mView =  inflater.inflate(R.layout.fragment_register, container, false);

        app = (CustomApplication)getActivity().getApplication();

        initView(mView);

        return mView;
    }

    private void initView(View view){
        et_phone_number = (EditText)view.findViewById(R.id.et_phone_number);
        et_password = (EditText)view.findViewById(R.id.et_reg_password);
        et_pin = (EditText)view.findViewById(R.id.et_reg_pin);

        bn_send = (TimeButton)view.findViewById(R.id.bn_send);
        bn_register = (Button) view.findViewById(R.id.bn_register_submit);
        bn_register.setClickable(false);

        sw_reg = (SwitchCompat)view.findViewById(R.id.sw_reg);

        bn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone_number = et_phone_number.getText().toString();
                Log.i(TAG, "onClick: " + phone_number);
                if (!phone_number.equals("")){
                    bn_send.setStart(true);
                    BmobSMS.requestSMSCode(phone_number, "test1", new QueryListener<Integer>() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if (e == null){
                                Log.i(TAG, "短信id："+integer);//用于查询本次短信发送详情
                                Toast.makeText(getContext(),"验证码发送成功!",Toast.LENGTH_SHORT).show();
                                bn_register.setClickable(true);
                            }
                        }
                    });
                }else {
                    bn_send.setStart(false);
                    Toast.makeText(getContext(),"手机号码为空！",Toast.LENGTH_SHORT).show();
                }
            }
        });

        bn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bn_register.setClickable(false);
                String pin = et_pin.getText().toString();
                password = et_password.getText().toString();

                Log.i(TAG, "onClick: " + phone_number);

                if (pin.equals("") || password.equals("")){
                    Toast.makeText(getContext(),"验证码或密码为空，请查询后输入",Toast.LENGTH_SHORT).show();
                }else {
                    CaiNiaoUser user = new CaiNiaoUser();
                    if (!sw_reg.isChecked()){
                        user.setShopkeeper(false);
                    }else {
                        user.setShopkeeper(true);
                    }
                    user.setMobilePhoneNumber(phone_number);
                    user.setPassword(password);
                    user.signOrLogin(pin, new SaveListener<CaiNiaoUser>() {
                        @Override
                        public void done(CaiNiaoUser caiNiaoUser, BmobException e) {
                            if(e==null){
                                Log.i(TAG, "done: " + caiNiaoUser.getSessionToken());
                                Utils.toastShow(getContext(),"注册成功");
                                login();
                            }else{
                                Utils.toastShow(getContext(),"失败:" + e.getMessage());
                                bn_register.setClickable(true);
                            }
                        }
                    });

                }
            }
        });
    }

    private void login(){
        Log.i(TAG, "login: ");
        BmobUser.loginByAccount(phone_number, password, new LogInListener<CaiNiaoUser>() {
            @Override
            public void done(CaiNiaoUser caiNiaoUser, BmobException e) {
                if(caiNiaoUser!=null){
                    app.setUser(caiNiaoUser);
                    Log.i(TAG, "done: " + caiNiaoUser.getMobilePhoneNumber() + caiNiaoUser.getSessionToken());

                    if (mCallback != null){
                        mCallback.onCallBack("register_success");
                    }

                }else {
                    Utils.toastShow(getContext(),e.getMessage());
                    Log.i(TAG, "done: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onRegisterCallback) {
            mCallback = (onRegisterCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public interface onRegisterCallback{
        void onCallBack(String uri);
    }
}
