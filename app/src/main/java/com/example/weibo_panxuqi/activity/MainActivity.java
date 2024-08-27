package com.example.weibo_panxuqi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.weibo_panxuqi.R;
import com.example.weibo_panxuqi.TextClickableSpan;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREFS_KEY_SHOW_PRIVACY = "ShowPrivacy";
    private static final String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout privacyLayout = findViewById(R.id.privacy);
        TextView agreeTextView = findViewById(R.id.privacy_agree_text);
        TextView disagreeTextView = findViewById(R.id.privacy_disagree_text);
        TextView privacyContentTextView = findViewById(R.id.privacy_content_text_);
        String privacyContent = privacyContentTextView.getText().toString();
        SpannableString spannableString = new SpannableString(privacyContent);

        //创建用户协议的ClickableTextSpan
        TextClickableSpan userAgreementSpan = new TextClickableSpan("《用户协议》", "查看用户协议", privacyContentTextView);
        spannableString.setSpan(userAgreementSpan, privacyContent.indexOf("《用户协议》"), privacyContent.indexOf("《用户协议》") + 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //创建隐私协议的ClickableTextSpan
        TextClickableSpan privacyPolicySpan = new TextClickableSpan("《隐私政策》", "查看隐私政策", privacyContentTextView);
        spannableString.setSpan(privacyPolicySpan, privacyContent.indexOf("《隐私政策》"), privacyContent.indexOf("《隐私政策》") + 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        privacyContentTextView.setText(spannableString);
        privacyContentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        privacyContentTextView.setHighlightColor(Color.TRANSPARENT);

        //检查SharedPreferences中的状态
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean showPrivacy = sharedPreferences.getBoolean(PREFS_KEY_SHOW_PRIVACY, true);

        // 根据状态显示或隐藏隐私内容
        if (showPrivacy) {
            // 显示隐私内容
            privacyLayout.setVisibility(View.VISIBLE);
            agreeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 更新状态为同意并保存到SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(PREFS_KEY_SHOW_PRIVACY, false);
                    editor.apply();
                    // 执行进入主页的操作
                    goToHome();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });

            disagreeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //退出
                    exitApp();
                }
            });
        } else {
            // 隐藏隐私内容
            privacyLayout.setVisibility(View.GONE);
            // 创建一个新的Handler
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    goToHome();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }, 2000); // 延迟2秒
        }


    }
    //关闭Activity
    private void exitApp() {
        finish();
    }

    //关闭Activity进入主页
    private void goToHome(){
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }

}