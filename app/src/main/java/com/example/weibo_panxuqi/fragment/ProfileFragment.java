package com.example.weibo_panxuqi.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.weibo_panxuqi.R;
import com.example.weibo_panxuqi.activity.LoginActivity;
import com.example.weibo_panxuqi.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @auther panxuqi
 * @date 2024/6/13
 * @time 10:11
 */
public class ProfileFragment extends Fragment {

    private RelativeLayout loginContainer;
    private RelativeLayout userInfoContainer;
    private ImageView profileImage;
    private TextView loginPrompt;
    private TextView clickToLogin;
    private ImageView userProfileImage;
    private TextView userName;
    private TextView userFans;
    private TextView logSee;
    private TextView noMore;
    private Button logoutButton;
    private TextView title;

    private SharedPreferences sharedPreferences;
    private OkHttpClient client = new OkHttpClient();
    private static final String ENDPOINT = "https://hotfix-service-prod.g.mi.com";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        title = view.findViewById(R.id.title);
        title.setText("我的");

        logoutButton = view.findViewById(R.id.btn_logout);
        logoutButton.setVisibility(View.GONE);
        logoutButton.setOnClickListener(v -> logout());

        loginContainer = view.findViewById(R.id.login_container);
        userInfoContainer = view.findViewById(R.id.user_info_container);
        profileImage = view.findViewById(R.id.profile_image);
        loginPrompt = view.findViewById(R.id.login_prompt);
        clickToLogin = view.findViewById(R.id.click_to_login);
        userProfileImage = view.findViewById(R.id.user_profile_image);
        userName = view.findViewById(R.id.user_name);
        userFans = view.findViewById(R.id.user_fans);
        logSee = view.findViewById(R.id.login_to_see);
        noMore = view.findViewById(R.id.nomore);

        sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        updateUI();

        profileImage.setOnClickListener(v -> goToLogin());
        clickToLogin.setOnClickListener(v -> goToLogin());

        return view;
    }

    private void goToLogin() {
        // 跳转到登录页面
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    private void logout() {
        // 清除本地缓存的 token
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // 更新为未登录状态
        updateUI();
    }

    private void updateUI() {
        String token = sharedPreferences.getString("token", null);
        if (token == null) {
            // 未登录
            loginContainer.setVisibility(View.VISIBLE);
            logSee.setVisibility(View.VISIBLE);
            userInfoContainer.setVisibility(View.GONE);
            noMore.setVisibility(View.GONE);
            title.setText("我的");
            logoutButton.setVisibility(View.GONE);
        } else {
            // 已登录，获取用户信息
            loginContainer.setVisibility(View.GONE);
            logSee.setVisibility(View.GONE);
            userInfoContainer.setVisibility(View.VISIBLE);
            noMore.setVisibility(View.VISIBLE);
            fetchUserInfo(token);
            title.setText("我的");
            logoutButton.setVisibility(View.VISIBLE);
        }
    }

    private void fetchUserInfo(String token) {
        Request request = new Request.Builder()
                .url(ENDPOINT + "/weibo/api/user/info")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "获取用户信息失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONObject data = jsonResponse.getJSONObject("data");

                        String username = data.getString("username");
                        String avatar = data.getString("avatar");
                        String fans = data.optString("fans", "");

                        getActivity().runOnUiThread(() -> {
                            userName.setText(username);
                            if (!fans.isEmpty()) {
                                userFans.setText("粉丝 " + fans);
                                userFans.setVisibility(View.VISIBLE);
                            } else {
                                userFans.setVisibility(View.GONE);
                            }
                            Glide.with(getActivity()).load(avatar).into(userProfileImage);
                            title.setText("我的");
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "获取用户信息失败", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}