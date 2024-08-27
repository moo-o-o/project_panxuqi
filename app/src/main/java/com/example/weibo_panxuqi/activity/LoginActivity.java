package com.example.weibo_panxuqi.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weibo_panxuqi.ApiService;
import com.example.weibo_panxuqi.R;
import com.example.weibo_panxuqi.defclass.UserInfo;
import com.example.weibo_panxuqi.request.LoginRequest;
import com.example.weibo_panxuqi.request.PhoneRequest;
import com.example.weibo_panxuqi.response.ApiResponse;
import com.example.weibo_panxuqi.response.LoginResponse;
import com.example.weibo_panxuqi.response.UserInfoResponse;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private ApiService apiService;
    private EditText phoneEditText;
    private EditText codeEditText;
    private TextView getCodeButton;
    private TextView loginButton;
    private Button btnBack;
    private TextView title;
    private SharedPreferences sharedPreferences;
    private CountDownTimer countDownTimer;

    private static final String ENDPOINT = "https://hotfix-service-prod.g.mi.com";
    private boolean isCodeRequested = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        title = findViewById(R.id.title);
        title.setText("登录账号");

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        phoneEditText = findViewById(R.id.phone);
        codeEditText = findViewById(R.id.code);
        getCodeButton = findViewById(R.id.get_code_button);
        loginButton = findViewById(R.id.login_button);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        getCodeButton.setOnClickListener(v -> getCode());
        loginButton.setOnClickListener(v -> login());

        phoneEditText.addTextChangedListener(loginTextWatcher);
        codeEditText.addTextChangedListener(loginTextWatcher);
    }

    private void getCode() {
        String phone = phoneEditText.getText().toString();
        if (phone.length() != 11) {
            Toast.makeText(this, "请输入完整手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneRequest phoneRequest = new PhoneRequest(phone);
        apiService.sendCode(phoneRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isData()) {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                        startCountdownTimer();
                        getCodeButton.setEnabled(false);
                    });
                    isCodeRequested = true;
                } else {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "发送验证码失败", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "发送验证码失败", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void startCountdownTimer() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                getCodeButton.setText(String.format(Locale.getDefault(), "%ds", millisUntilFinished / 1000));
            }

            public void onFinish() {
                getCodeButton.setText("获取验证码");
                getCodeButton.setEnabled(true);
            }
        }.start();
    }

    private void login() {
        String phone = phoneEditText.getText().toString();
        String code = codeEditText.getText().toString();

        if (phone.length() != 11 || code.length() != 6) {
            Toast.makeText(this, "请输入正确的手机号和验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isCodeRequested) {
            Toast.makeText(this, "请先获取验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(phone, code);
        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    int code = response.body().getCode();
                    String msg = response.body().getMsg();

                    if (code == 200) {
                        saveToken(loginResponse.getData());
                        String token = response.body().getData();
                        /*SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", token);
                        editor.apply();*/
                        fetchUserInfo(token); // 获取用户信息

                    } else {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "登录失败: " + msg, Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void fetchUserInfo(String token) {
        apiService.getUserInfo(token).enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserInfo userInfo = response.body().getData();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong("user_id", userInfo.getId());
                    editor.putString("username", userInfo.getUsername());
                    editor.putString("phone", userInfo.getPhone());
                    editor.putString("avatar", userInfo.getAvatar());
                    editor.putBoolean("login_status", userInfo.isLoginStatus());
                    editor.apply();


                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("login_status", true);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String phoneInput = phoneEditText.getText().toString().trim();
            String codeInput = codeEditText.getText().toString().trim();

            loginButton.setEnabled(phoneInput.length() == 11 && codeInput.length() == 6);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void saveToken(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
