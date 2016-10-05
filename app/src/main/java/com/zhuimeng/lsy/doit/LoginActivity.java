package com.zhuimeng.lsy.doit;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.zhuimeng.lsy.doit.bmob.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    private static final int LOGIN_SUCCESS = 1;
    private static final int LOGIN_FAIL = 2;
    private static final int BUTTON_REST=3;
    private static final int BUTTON_SUCCESS=4;
    private EditText loginAccount;
    private EditText mPasswordView;
    private CircularProgressButton mEmailSignInButton;
    private TextView forgetPasswrod;
    private TextView register;
    private String returnUserName;
    private String objectId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        Bmob.initialize(this, "ae39585e8e631ab8c67efb404afac2e6");
        // Set up the login form.
        loginAccount = (EditText) findViewById(R.id.account);
        register=(TextView)findViewById(R.id.register);
        forgetPasswrod=(TextView)findViewById(R.id.forget_password_view);
        mPasswordView = (EditText) findViewById(R.id.password);


        mEmailSignInButton = (CircularProgressButton) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(view);
            }
        });

        forgetPasswrod.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    private void attemptLogin(View view) {


        // Reset errors.
        loginAccount.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = loginAccount.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            loginAccount.setError(getString(R.string.error_field_required));
            focusView = loginAccount;
            cancel = true;
        } else if (!isPhoneValid(email)) {
            loginAccount.setError(getString(R.string.error_invalid_email));
            focusView = loginAccount;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress();
            Login(email, password,view);
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BUTTON_REST:
                    mEmailSignInButton.setProgress(0);
                    break;
                case BUTTON_SUCCESS:
                    mEmailSignInButton.setProgress(100);
                    break;
                case LOGIN_SUCCESS:
                    mEmailSignInButton.setProgress(100);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finish();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user_name", returnUserName);
                    intent.putExtra("user_object_id",objectId);
                    intent.putExtra("isFristLogin",true);
                    startActivity(intent);
                    break;
                case LOGIN_FAIL:
                    mEmailSignInButton.setProgress(-1);
                    break;
                default:
                    break;
            }
        }
    };

    private void Login(String email, String password,final View v) {
        BmobUser.loginByAccount(LoginActivity.this, email, password, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    returnUserName =user.getUsername();
                    objectId=user.getObjectId();
                    mHandler.sendEmptyMessage(BUTTON_SUCCESS);
                    mHandler.sendEmptyMessageDelayed(LOGIN_SUCCESS,500);
                } else {
                    Snackbar.make(v,"错误描述"+e.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                    mHandler.sendEmptyMessage(LOGIN_FAIL);
                    mHandler.sendEmptyMessageDelayed(BUTTON_REST,1500);
                }
            }
        });

    }

    private boolean isPhoneValid(String phone) {
        //TODO: Replace this with your own logic
        Log.e("length of email", phone.length() + " ");
        return phone.length()==11;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress() {
        if (mEmailSignInButton.getProgress() == 0) {
            simulateSuccessProgress(mEmailSignInButton);
        } else {
            mEmailSignInButton.setProgress(0);
        }

    }

    private void simulateSuccessProgress(final CircularProgressButton button) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1,99);
        widthAnimation.setDuration(1000);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                button.setProgress(value);
            }
        });
        widthAnimation.start();
    }

}

