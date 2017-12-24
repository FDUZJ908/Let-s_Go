package com.example.letsgo;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;

import model.Code;
import model.Register;
import model.responseRegister;
import okhttp3.Call;
import okhttp3.Response;
import util.Picker;

import static util.Check.isEmail;
import static util.Check.isMobileNO;
import static util.httpUtil.sendHttpPost;
import static util.httpUtil.sendHttpRequest;

public class RegisterActivity extends AppCompatActivity {

    private Button identify;
    private Button register;
    private EditText account;
    private EditText code;
    private EditText password;
    private EditText nickname;
    private Button gender;
    private EditText Tel;
    private Gson gson = new Gson();
    private Code responseCode;
    private responseRegister responseRegister;
    private Register myRegister;
    private Picker picker=new Picker();

    public static final int OK = 1;
    public static final int FAILURE = 0;
    public static final int TEST = -1;
    public static final int GETCODE = 2;
    public static final int GETREGISTER = 3;

    private String responseData;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GETCODE:
                    responseCode = gson.fromJson(msg.obj.toString(), Code.class);
                    if (responseCode.getStatus().equals("OK")) {

                    } else if (responseCode.getStatus().equals("ERROR")) {
                        identify.setClickable(true);
                        identify.setText("发送验证码");
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("注册错误")
                                .setMessage(responseCode.getMessage())
                                .setPositiveButton("确定", null)
                                .show();
                    }
                    break;
                case GETREGISTER:
                    if (responseRegister.getStatus().equals("OK")) {
                        MainActivity.myToken = responseRegister.getToken();
                        MainActivity.myUserid = myRegister.getUserid();
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("注册成功")
                                .setMessage("注册成功，将自动返回登录")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .show();

                    } else {
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("注册错误")
                                .setMessage(responseRegister.getMessage())
                                .setPositiveButton("确定", null)
                                .show();
                    }
                    break;
                case TEST:
                    break;
                /*case FAILURE:
                    new AlertDialog.Builder(getApplicationContext())
                            .setTitle("注册错误")
                            .setMessage("验证码输入错误")
                            .show();
                    break;
                case OK:
                    new AlertDialog.Builder(getApplicationContext())
                            .setTitle("注册成功")
                            .setMessage("请返回登录")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .show();
                    break;*/
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEmail(account.getText().toString())) {
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("注册错误")
                            .setMessage("请输入正确的邮箱")
                            .setPositiveButton("确定", null)
                            .show();
                    return;
                }
                identify.setClickable(false);
                identify.setText("已发送");
                Register register = new Register();
                register.setUserid(account.getText().toString());
                register.setType(0);
                sendHttpPost("https://shiftlin.top/cgi-bin/Verify.py", gson.toJson(register), new okhttp3.Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d("**TEST**", "Success");
                        Message message = new Message();
                        message.obj = response.body().string();
                        message.what = GETCODE;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("**TEST**", "Failure");
                        e.printStackTrace();
                    }

                });
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!code.getText().toString().equals(responseCode.getCode())) {
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("注册错误")
                            .setMessage("验证码输入错误")
                            .setPositiveButton("确定", null)
                            .show();
                    return;
                }
                if (isMobileNO(Tel.getText().toString())) {
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("注册错误")
                            .setMessage("请输入正确的电话号码")
                            .setPositiveButton("确定", null)
                            .show();
                    return;
                }
                if(password.getText().toString().length()<6){
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("注册错误")
                            .setMessage("密码长度太短")
                            .setPositiveButton("确定", null)
                            .show();
                    return;
                }
                if(nickname.getText().toString().length()>10){
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("注册错误")
                            .setMessage("昵称长度太长")
                            .setPositiveButton("确定", null)
                            .show();
                    return;
                }
                myRegister = new Register();
                myRegister.setUserid(account.getText().toString());
                //register.setCode(code.getText().toString());
                myRegister.setPassword(password.getText().toString());
                myRegister.setNickname(nickname.getText().toString());
                myRegister.setTel(Tel.getText().toString());
                if (gender.getText().toString().equals("男"))
                    myRegister.setGender(1);
                else if (gender.getText().toString().equals("女"))
                    myRegister.setGender(2);
                else myRegister.setGender(0);
                sendHttpPost("https://shiftlin.top/cgi-bin/Register", gson.toJson(myRegister), new okhttp3.Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d("**TEST**", "Success-Register");
                        responseRegister = gson.fromJson(response.body().string(), responseRegister.class);
                        Message message = new Message();
                        message.what = GETREGISTER;
                        message.obj = responseRegister;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });

    }

    private void initViews() {
        identify = (Button) findViewById(R.id.identify);
        register = (Button) findViewById(R.id.register_r);
        account = (EditText) findViewById(R.id.account_r);
        password = (EditText) findViewById(R.id.password_r);
        code = (EditText) findViewById(R.id.code);
        nickname = (EditText) findViewById(R.id.nickname_r);
        gender = (Button) findViewById(R.id.gender_r);
        Tel = (EditText) findViewById(R.id.Tel_r);
        gender.setOnClickListener(GenderListener);
    }

    protected View.OnClickListener GenderListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            picker.onConstellationPicker(RegisterActivity.this, 4);
        }
    };
}
