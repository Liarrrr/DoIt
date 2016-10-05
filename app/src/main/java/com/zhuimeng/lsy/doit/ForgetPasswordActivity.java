package com.zhuimeng.lsy.doit;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;

/**
 * Created by lsy on 16-5-1.
 */
public class ForgetPasswordActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button smsButton;
    private Button commitButton;
    private MyCountTimer timer;
    private EditText phoneMumber;
    private EditText codeMumber;
    private EditText password;
    private EditText confirmPassword;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);
        //Bmob.initialize(this, "ae39585e8e631ab8c67efb404afac2e6");
        toolbar = (Toolbar) findViewById(R.id.forget_toolbar);
        setSupportActionBar(toolbar);
        smsButton= (Button) findViewById(R.id.find_password_button);
        commitButton=(Button)findViewById(R.id.commit_passwrod);
        phoneMumber=(EditText)findViewById(R.id.find_password_phone);
        codeMumber=(EditText)findViewById(R.id.code);
        password=(EditText)findViewById(R.id.new_password);
        confirmPassword=(EditText)findViewById(R.id.confirm_password) ;
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(phoneMumber.getText().toString())){
                    phoneMumber.setError("手机号码不能为空");
                }else {
                    requestSMSCode(v);
                }
            }
        });
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(password.getText().toString())){
                    password.setError("密码不能为空");
                }else if (password.getText().toString().length()<6){
                    password.setError("密码不能少于6位");
                }else if(!TextUtils.isEmpty(password.getText().toString())
                &&TextUtils.isEmpty(confirmPassword.getText().toString())){
                    confirmPassword.setError("请再次确认您的密码");
                }else if(TextUtils.isEmpty(codeMumber.getText().toString())){
                    codeMumber.setError("请输入验证码");
                }
                else{
                    resetPwd(v);
                }
            }
        });
    }

    private void resetPwd(final View v) {
        final String code = codeMumber.getText().toString();
        final String pwd = password.getText().toString();
        final String confirmPwd=confirmPassword.getText().toString();
        if(!pwd.equals(confirmPwd)){
            Snackbar.make(v,"两次输入的密码不一致，请重新出入", Snackbar.LENGTH_SHORT)
                    .setAction("Action",null).show();
            password.setText("");
            confirmPassword.setText("");
            return;
        }

        final ProgressDialog progress = new ProgressDialog(ForgetPasswordActivity.this);
        progress.setMessage("正在重置密码...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        BmobUser.resetPasswordBySMSCode(this, code, pwd, new ResetPasswordByCodeListener() {

            @Override
            public void done(BmobException ex) {
                // TODO Auto-generated method stub
                progress.dismiss();
                if(ex==null){
                    Snackbar.make(v,"重置成功", Snackbar.LENGTH_SHORT)
                            .setAction("Action",null).show();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finish();
                }else{
                    Snackbar.make(v,"密码重置失败"+"，错误描述："+ex.getLocalizedMessage()
                    , Snackbar.LENGTH_LONG).setAction("Action",null).show();
                }
            }
        });
    }

    private void requestSMSCode(final View v) {
        String number = phoneMumber.getText().toString();
        if (!TextUtils.isEmpty(number)) {
            timer = new MyCountTimer(60000, 1000);
            timer.start();
            BmobSMS.requestSMSCode(this, number,"做了么--密码重置", new RequestSMSCodeListener() {

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
            phoneMumber.setError("输入的手机号码不存在");
        }
    }

    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            smsButton.setText((millisUntilFinished / 1000) +"秒后重发");
        }
        @Override
        public void onFinish() {
            smsButton.setText("重新发送验证码");
        }
    }
}
