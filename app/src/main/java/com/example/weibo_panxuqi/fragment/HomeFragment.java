package com.example.weibo_panxuqi.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weibo_panxuqi.ApiService;
import com.example.weibo_panxuqi.R;
import com.example.weibo_panxuqi.WeiboAdapter;
import com.example.weibo_panxuqi.defclass.Page;
import com.example.weibo_panxuqi.defclass.WeiboInfo;
import com.example.weibo_panxuqi.response.UserInfoResponse;
import com.example.weibo_panxuqi.response.WeiboResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private WeiboAdapter weiboAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar loadingSpinner;
    private ConstraintLayout inLoading;
    private ConstraintLayout networkError;
    private TextView curNo;
    private boolean isLoadingMore = false;
    private boolean isFirstLoad = true;
    private List<WeiboInfo> weiboList = new ArrayList<>();
    private int currentPage = 1;  // 用于追踪当前加载的页码
    private boolean isFirstTime = true; // 是否是第一次进入应用
    private String token = "<your_token>"; // 存储用户的token

    private ConnectivityManager.NetworkCallback networkCallback;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        curNo = view.findViewById(R.id.cur_no_content);
        curNo.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        loadingSpinner = view.findViewById(R.id.loading_spinner);
        inLoading = view.findViewById(R.id.inloading);
        networkError = view.findViewById(R.id.networkerror);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        weiboAdapter = new WeiboAdapter(getContext(), weiboList);
        recyclerView.setAdapter(weiboAdapter);

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1) && dy > 0 && !isLoadingMore) {
                    loadMoreData();
                }
            }
        });

        // 初次加载数据
        refreshData();

        // 监听网络变化
        monitorNetworkChanges();

        return view;
    }

    private void refreshData() {
        currentPage = 1; // 重置页码
        fetchWeiboData(true);
    }

    private void loadMoreData() {
        if (!isLoadingMore) {
            isLoadingMore = true;
            fetchWeiboData(false);
        }
    }

    private void retryLoading() {
        inLoading.setVisibility(View.VISIBLE);
        networkError.setVisibility(View.GONE);
        fetchWeiboData(true);
    }

    private void fetchWeiboData(boolean isRefresh) {
        if (isFirstLoad || isFirstTime) {
            inLoading.setVisibility(View.VISIBLE);
            curNo.setVisibility(View.GONE);
            networkError.setVisibility(View.GONE);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://hotfix-service-prod.g.mi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<WeiboResponse> call = apiService.getHomePage(token, currentPage, 10);

        call.enqueue(new Callback<WeiboResponse>() {
            @Override
            public void onResponse(Call<WeiboResponse> call, retrofit2.Response<WeiboResponse> response) {
                isLoadingMore = false;
                swipeRefreshLayout.setRefreshing(false);
                inLoading.setVisibility(View.GONE);
                networkError.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    WeiboResponse weiboResponse = response.body();
                    Page<WeiboInfo> page = weiboResponse.getData();
                    if (page != null) {
                        List<WeiboInfo> newWeiboList = page.getRecords();
                        if (isRefresh) {
                            weiboList.clear();
                            weiboList.addAll(newWeiboList);
                            // 随机打乱顺序
                            Collections.shuffle(weiboList);
                            isFirstLoad = false;
                            isFirstTime = false; // 更新状态为非第一次加载
                        } else {
                            if (newWeiboList.isEmpty()) {
                                Toast.makeText(getContext(), "无更多内容", Toast.LENGTH_SHORT).show();
                            } else {
                                weiboList.addAll(newWeiboList);
                            }
                        }
                        weiboAdapter.notifyDataSetChanged();
                        currentPage++;  // 增加页码
                    } else {
                        if (!isRefresh) {
                            Toast.makeText(getContext(), "无更多内容", Toast.LENGTH_SHORT).show();
                        }
                    }
                    // 缓存数据到本地
                    // TODO: 实现缓存功能
                } else {
                    handleNetworkError(isRefresh);
                }
            }

            @Override
            public void onFailure(Call<WeiboResponse> call, Throwable t) {
                isLoadingMore = false;
                swipeRefreshLayout.setRefreshing(false);
                inLoading.setVisibility(View.GONE);
                handleNetworkError(isRefresh);
            }
        });
    }

    private void handleNetworkError(boolean isRefresh) {
        if (isRefresh) {
            if (weiboList.isEmpty()) {
                networkError.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getContext(), "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void monitorNetworkChanges() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder().build();

        connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                // 网络可用时刷新数据
                if (!weiboList.isEmpty()) {
                    fetchWeiboData(true);
                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                // 网络丢失时显示提示
                Toast.makeText(getContext(), "网络连接丢失", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }
}
