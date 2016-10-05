package com.zhuimeng.lsy.doit;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;

import com.dd.CircularProgressButton;
import com.zhuimeng.lsy.doit.bmob.User;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

/**
 * Created by lsy on 16-5-2.
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText userName;
    private EditText password;
    private EditText confirmPassword;
    private EditText phoneNumber;
    private EditText smsCode;
    private MyCountTimer timer;
    private Button SMSSend;
    private CircularProgressButton registerButton;
    private static final int REGISTER_SUCCESS = 1;
    private static final int REGISTER_FAIL = 2;
    private static final int BUTTON_REST=3;
    private Toolbar toolbar;
    private static final int BUTTON_SUCCESS=4;
    private String returnUserName;
    private String objectId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user);
        toolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        smsCode=(EditText)findViewById(R.id.register_SMS_code);
        SMSSend=(Button)findViewById(R.id.register_send_SMS);
        userName= (EditText) findViewById(R.id.register_user_name);
        password=(EditText)findViewById(R.id.register_password);
        confirmPassword=(EditText)findViewById(R.id.register_confirm_password);
        phoneNumber=(EditText)findViewById(R.id.register_phone);
        registerButton=(CircularProgressButton)findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(TextUtils.isEmpty(userName.getText().toString())){
                    userName.setError("用户名不能为空");
                }else if(TextUtils.isEmpty(phoneNumber.getText().toString())){
                    phoneNumber.setError("手机号码不能为空");
                }else if(TextUtils.isEmpty(password.getText().toString())){
                    password.setError("密码不能为空");
                }else if(TextUtils.isEmpty(confirmPassword.getText().toString())){
                    confirmPassword.setError("请确认您的密码");
                } else if(password.getText().toString().length()<6){
                    password.setError("密码不能少于6位");
                }else if(TextUtils.isEmpty(smsCode.getText().toString())){
                    smsCode.setError("请输入验证码");
                }else{
                    showProgress();
                    BmobSMS.verifySmsCode(RegisterActivity.this, phoneNumber.getText().toString(), smsCode.getText().toString(), new VerifySMSCodeListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                registerUser(v);
                            }else{
                                Snackbar.make(v,"验证码错误",Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        SMSSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(phoneNumber.getText().toString())){
                    phoneNumber.setError("手机号码不能为空");
                }else {
                    registerButton.setEnabled(true);
                    requestSMSCode(v);
                }
            }
        });

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BUTTON_REST:
                    registerButton.setProgress(0);
                    break;
                case BUTTON_SUCCESS:
                    registerButton.setProgress(100);
                    break;
                case REGISTER_SUCCESS:
                    registerButton.setProgress(100);
                    finish();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.putExtra("user_name",returnUserName);
                    intent.putExtra("user_object_id",objectId);
                    intent.putExtra("isFristLogin",true);
                    startActivity(intent);
                    break;
                case REGISTER_FAIL:
                    registerButton.setProgress(-1);
                    break;
                default:
                    break;
            }
        }
    };

    private void requestSMSCode(final View v) {
        String number = phoneNumber.getText().toString();
        if (!TextUtils.isEmpty(number)) {
            timer = new MyCountTimer(60000, 1000);
            timer.start();
            BmobSMS.requestSMSCode(this, number,"做了么", new RequestSMSCodeListener() {

                @Override
                public void done(Integer smsId, BmobException ex) {
                    // TODO Auto-generated method stub
                    if (ex == null) {// 验证码发送成功
                        Snackbar.make(v,"发送成功，请注意查收", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();// 用于查询本次短信发送详情
                    }else{
                        timer.cancel();
                    }
                }
            });
        } else {
            phoneNumber.setError("输入的手机号码不存在");
        }
    }

    private void showProgress() {
        if (registerButton.getProgress() == 0) {
            simulateSuccessProgress(registerButton);
        } else {
            registerButton.setProgress(0);
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

    private void registerUser(final View v) {
        final String account = userName.getText().toString();
        String pwd = password.getText().toString();
        String pwdAgin = confirmPassword.getText().toString();
        String phone=phoneNumber.getText().toString();

        if(!pwd.equals(pwdAgin)){
            Snackbar.make(v,"两次密码不一致，请重新输入", Snackbar.LENGTH_LONG)
                    .setAction("Aciton",null).show();
            password.setText("");
            confirmPassword.setText("");
            return;
        }
        final User user = new User();
        user.setUsername(account);
        user.setPassword(pwd);
        user.setMobilePhoneNumber(phone);
        user.setMobilePhoneNumberVerified(true);
        user.signUp(this, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                returnUserName=account;
                objectId=user.getObjectId();
                mHandler.sendEmptyMessage(BUTTON_SUCCESS);
                mHandler.sendEmptyMessageDelayed(REGISTER_SUCCESS,500);
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                Snackbar.make(v,"注册失败，错误描述"+arg1, Snackbar.LENGTH_LONG)
                        .setAction("Aciton",null).show();
                mHandler.sendEmptyMessage(REGISTER_FAIL);
                mHandler.sendEmptyMessageDelayed(BUTTON_REST,1500);
            }
        });
    }

    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            SMSSend.setText((millisUntilFinished / 1000) +"秒后重发");
        }
        @Override
        public void onFinish() {
            SMSSend.setText("重新发送验证码");
        }
    }
}
