package com.example.weibo_panxuqi.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.weibo_panxuqi.ImagePagerAdapter;
import com.example.weibo_panxuqi.R;

import java.util.List;

public class ImageViewerActivity extends AppCompatActivity {

    private List<String> images;
    private int currentIndex;
    private String username;
    private String avatarUrl;
    private TextView pageInfo;
    private DownloadManager downloadManager;
    private long downloadId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.activity_image_viewer);

        ViewPager viewPager = findViewById(R.id.view_pager);
        TextView usernameTextView = findViewById(R.id.username);
        ImageView avatarImageView = findViewById(R.id.avatar);
        TextView downloadButton = findViewById(R.id.download_button);
        pageInfo = findViewById(R.id.page_info);

        images = getIntent().getStringArrayListExtra("images");
        currentIndex = getIntent().getIntExtra("current_index", 0);
        username = getIntent().getStringExtra("username");
        avatarUrl = getIntent().getStringExtra("avatar_url");

        if (images == null || images.isEmpty()) {
            Toast.makeText(this, "No images to display", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ImagePagerAdapter adapter = new ImagePagerAdapter(this, images);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentIndex);
        updatePageInfo(currentIndex);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updatePageInfo(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        usernameTextView.setText(username);
        Glide.with(this).load(avatarUrl).circleCrop().into(avatarImageView);

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        downloadButton.setOnClickListener(v -> {
            String currentImageUrl = images.get(viewPager.getCurrentItem());
            downloadImage(currentImageUrl);
        });

        registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downloadReceiver);
    }

    private void updatePageInfo(int position) {
        pageInfo.setText((position + 1) + "/" + images.size());
    }

    private void downloadImage(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "downloaded_image.jpg");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Downloading Image");
        request.setDescription("Downloading image...");

        downloadId = downloadManager.enqueue(request);
    }

    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadId == id) {
                Toast.makeText(context, "图⽚下载完成，请相册查看", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
