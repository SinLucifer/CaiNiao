package com.sin.cainiao.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sin.cainiao.utils.View.BackHandledFragment;
import com.sin.cainiao.utils.View.BackHandledInterface;
import com.sin.cainiao.fragment.login_register.LoginFragment;
import com.sin.cainiao.fragment.login_register.RegisterFragment;
import com.sin.cainiao.fragment.login_register.WelcomeFragment;
import com.sin.cainiao.R;


public class Login_RegisterActivity extends AppCompatActivity implements BackHandledInterface
        ,WelcomeFragment.onWelcomeCallback,LoginFragment.onLoginCallBack,RegisterFragment.onRegisterCallback{

    private final static String TAG = "Login_RegisterActivity";
    private BackHandledFragment currentFragment;
    private BackHandledFragment lastFragment;
    private boolean hadIntercept;
    private int count;

    private WelcomeFragment welcomeFragment;
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        if (savedInstanceState == null){
            welcomeFragment = WelcomeFragment.newInstance();
            loginFragment = LoginFragment.newInstance();
            registerFragment = RegisterFragment.newInstance();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.register_container,welcomeFragment)
                .commit();
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.currentFragment = selectedFragment;
        Log.i(TAG, "currentFragment: " + currentFragment);
    }


    @Override
    public void onBackPressed() {

        if(currentFragment == null || !currentFragment.onBackPressed()){
            if(count == 0){
                setResult(0);
                super.onBackPressed();
            }else{
                BackHandledFragment temp = currentFragment;
                getSupportFragmentManager().beginTransaction().hide(currentFragment)
                        .show(lastFragment).commit();
                currentFragment = lastFragment;
                lastFragment = temp;
                count--;
            }
        }
        Log.i(TAG, "currentFragment: " + currentFragment);
    }

    private void changeFragment(Fragment fragment){
        if (!fragment.isAdded()){
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(currentFragment)
                    .add(R.id.register_container, fragment)
                    .commit();
        }else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(currentFragment)
                    .show(fragment)
                    .commit();
            lastFragment = currentFragment;
            currentFragment = (BackHandledFragment) fragment;
        }
    }

    @Override
    public void onCallBack(String uri) {
        lastFragment = currentFragment;
        count++;
        switch (uri) {
            case "login":
                changeFragment(loginFragment);
                break;
            case "register":
                changeFragment(registerFragment);
                break;
            case "register_success":
            case "login_success":
                setResult(1);
                finish();
                break;
        }

        Log.i(TAG, "currentFragment: " + currentFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }
}
