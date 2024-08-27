package com.example.weibo_panxuqi;

import static androidx.core.content.ContextCompat.startActivity;
import static com.google.android.material.internal.ContextUtils.getActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.weibo_panxuqi.activity.ImageViewerActivity;
import com.example.weibo_panxuqi.activity.LoginActivity;
import com.example.weibo_panxuqi.defclass.ExpandableHeightGridView;
import com.example.weibo_panxuqi.defclass.UserInfo;
import com.example.weibo_panxuqi.defclass.WeiboInfo;
import com.example.weibo_panxuqi.request.LikeRequest;
import com.example.weibo_panxuqi.request.UnlikeRequest;
import com.example.weibo_panxuqi.response.LikeResponse;
import com.example.weibo_panxuqi.response.UnlikeResponse;
import com.example.weibo_panxuqi.response.UserInfoResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeiboAdapter extends RecyclerView.Adapter<WeiboAdapter.WeiboViewHolder> {

    private List<WeiboInfo> weiboList;
    private Context context;
    private WeiboViewHolder currentPlayingHolder;
    private String token = "<your_token>"; // 存储用户的token
    private Handler handler = new Handler(); // 用于更新进度条

    public WeiboAdapter(Context context, List<WeiboInfo> weiboList) {
        this.context = context;
        this.weiboList = weiboList;
    }

    @NonNull
    @Override
    public WeiboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weibo, parent, false);
        return new WeiboViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeiboViewHolder holder, int position) {
        WeiboInfo weibo = weiboList.get(position);

        holder.username.setText(weibo.getUsername());
        holder.title.setText(weibo.getTitle());
        holder.title.setMaxLines(6);

        // 加载头像
        Glide.with(context).load(weibo.getAvatar()).circleCrop().into(holder.avatar);

        // 判断并加载图片或视频
        if (weibo.getVideoUrl() != null && !weibo.getVideoUrl().isEmpty()) {
            // 视频模块
            holder.media.setVisibility(View.GONE);
            holder.gridView.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.VISIBLE);
            holder.playButton.setVisibility(View.VISIBLE);
            holder.seekBar.setVisibility(View.VISIBLE);

            Glide.with(context).load(weibo.getPoster()).into(holder.staticVideo);
            holder.staticVideo.setVisibility(View.VISIBLE);

            holder.videoView.setVideoPath(weibo.getVideoUrl());
            holder.videoView.setOnCompletionListener(mp -> mp.start());
            holder.videoView.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                mp.setVolume(0, 0);
                holder.seekBar.setMax(holder.videoView.getDuration());

                holder.playButton.setOnClickListener(v -> {
                    if (mp.isPlaying()) {
                        mp.pause();
                        holder.playButton.setVisibility(View.VISIBLE);
                        holder.staticVideo.setVisibility(View.VISIBLE);
                        handler.removeCallbacks(updateSeekBar);
                    } else {
                        // 如果有其他视频在播放，先暂停它
                        if (currentPlayingHolder != null && currentPlayingHolder != holder) {
                            currentPlayingHolder.videoView.pause();
                            currentPlayingHolder.playButton.setVisibility(View.VISIBLE);
                            currentPlayingHolder.staticVideo.setVisibility(View.VISIBLE);
                        }
                        mp.start();
                        holder.playButton.setVisibility(View.GONE);
                        holder.staticVideo.setVisibility(View.GONE);
                        currentPlayingHolder = holder;
                        handler.post(updateSeekBar);
                    }
                });

                holder.videoView.setOnClickListener(v -> {
                    if (mp.isPlaying()) {
                        mp.pause();
                        holder.playButton.setVisibility(View.VISIBLE);
                        holder.staticVideo.setVisibility(View.VISIBLE);
                        handler.removeCallbacks(updateSeekBar);
                    } else {
                        mp.start();
                        holder.playButton.setVisibility(View.GONE);
                        holder.staticVideo.setVisibility(View.GONE);
                        currentPlayingHolder = holder;
                        handler.post(updateSeekBar);
                    }
                });
            });

            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        holder.videoView.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

        } else {
            holder.videoView.setVisibility(View.GONE);
            holder.playButton.setVisibility(View.GONE);
            holder.staticVideo.setVisibility(View.GONE);
            holder.seekBar.setVisibility(View.GONE);

            if (weibo.getImages() != null && !weibo.getImages().isEmpty()) {
                // 图片模块
                holder.media.setVisibility(View.GONE);
                holder.gridView.setVisibility(View.VISIBLE);

                if (weibo.getImages().size() == 1) {
                    // 单张图片
                    holder.media.setVisibility(View.VISIBLE);
                    holder.gridView.setVisibility(View.GONE);
                    Glide.with(context)
                            .asBitmap()
                            .load(weibo.getImages().get(0))
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    holder.media.setImageBitmap(resource);
                                    if (resource.getWidth() > resource.getHeight()) {
                                        // 宽大于高，横图样式
                                        holder.media.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    } else {
                                        // 宽小于高，竖图样式
                                        holder.media.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    // 清除加载
                                }
                            });
                } else {
                    // 多张图片使用9宫格
                    holder.media.setVisibility(View.GONE);
                    holder.gridView.setVisibility(View.VISIBLE);
                    holder.gridView.setExpanded(true);
                    GridAdapter gridAdapter = new GridAdapter(context, weibo.getImages());
                    List<String> list = weibo.getImages();
                    Log.v("Images List", list.toString());
                    holder.gridView.setAdapter(gridAdapter);

                    gridAdapter.setOnItemClickListener((position2, imageUrl) -> {
                        Intent intent = new Intent(context, ImageViewerActivity.class);
                        intent.putStringArrayListExtra("images", new ArrayList<>(weibo.getImages()));
                        intent.putExtra("current_index", position2);
                        intent.putExtra("username", weibo.getUsername());
                        intent.putExtra("avatar_url", weibo.getAvatar());
                        context.startActivity(intent);
                    });
                }
            } else {
                // 普通文本
                holder.media.setVisibility(View.GONE);
                holder.gridView.setVisibility(View.GONE);
            }
        }

        holder.deleteButton.setOnClickListener(v -> {
            // 删除帖子操作
            int position1 = holder.getAdapterPosition();
            weiboList.remove(position1);
            notifyItemRemoved(position1);
            notifyItemRangeChanged(position1, weiboList.size());
            Toast.makeText(context, "删除帖子", Toast.LENGTH_SHORT).show();
        });

        holder.likeButton.setOnClickListener(v -> {
            // 点赞操作
            if (!isUserLoggedIn()) {
                // 跳转到登录页面
                // TODO: Implement login page navigation
                Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
                goToLogin();
                return;
            }

            if (weibo.isLikeFlag()) {
                // 取消点赞
                performUnlike(holder, weibo);
            } else {
                // 点赞
                performLike(holder, weibo);
            }
        });

        holder.commentButton.setOnClickListener(v -> {
            // 评论操作
            int position1 = holder.getAdapterPosition();
            Toast.makeText(context, "点击第" + (position1 + 1) + "条数据评论按钮", Toast.LENGTH_SHORT).show();
        });

        holder.media.setOnClickListener(v -> {
            Intent intent = new Intent(context, ImageViewerActivity.class);
            intent.putStringArrayListExtra("images", new ArrayList<>(weibo.getImages()));
            intent.putExtra("current_index", 0);
            intent.putExtra("username", weibo.getUsername());
            intent.putExtra("avatar_url", weibo.getAvatar());
            context.startActivity(intent);
        });

    }

    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (currentPlayingHolder != null && currentPlayingHolder.videoView.isPlaying()) {
                currentPlayingHolder.seekBar.setProgress(currentPlayingHolder.videoView.getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        }
    };

    private void goToLogin() {
        // 跳转到登录页面
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    private void performLike(WeiboViewHolder holder, WeiboInfo weibo) {
        // 点赞动画
        holder.likeButton.animate().scaleX(1.2f).scaleY(1.2f).rotationY(360).setDuration(1000).withEndAction(() -> {
            holder.likeButton.setScaleX(1.0f);
            holder.likeButton.setScaleY(1.0f);
        }).start();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://hotfix-service-prod.g.mi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<LikeResponse> call = apiService.like(token, new LikeRequest(weibo.getId()));

        call.enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, retrofit2.Response<LikeResponse> response) {
                if (response.isSuccessful()) {
                    weibo.setLikeFlag(true);
                    holder.likeButton.setImageResource(R.drawable.like_fill); // 设置为已点赞的图标
                    holder.dzCount.setText(String.valueOf(weibo.getLikeCount() + 1));
                    holder.dzCount.setTextColor(Color.parseColor("#EA512F"));
                } else {
                    Toast.makeText(context, "点赞失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) {
                Toast.makeText(context, "点赞失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performUnlike(WeiboViewHolder holder, WeiboInfo weibo) {
        // 取消点赞动画
        holder.likeButton.animate().scaleX(0.8f).scaleY(0.8f).setDuration(1000).withEndAction(() -> {
            holder.likeButton.setScaleX(1.0f);
            holder.likeButton.setScaleY(1.0f);
        }).start();
        holder.dzCount.setText("点赞");
        holder.dzCount.setTextColor(Color.BLACK);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://hotfix-service-prod.g.mi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<UnlikeResponse> call = apiService.unlike(token, new UnlikeRequest(weibo.getId()));

        call.enqueue(new Callback<UnlikeResponse>() {
            @Override
            public void onResponse(Call<UnlikeResponse> call, retrofit2.Response<UnlikeResponse> response) {
                if (response.isSuccessful()) {
                    weibo.setLikeFlag(false);
                    holder.likeButton.setImageResource(R.drawable.like_empty); // 设置为未点赞的图标
                } else {
                    Toast.makeText(context, "取消点赞失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UnlikeResponse> call, Throwable t) {
                Toast.makeText(context, "取消点赞失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null) != null;
    }

    @Override
    public int getItemCount() {
        return weiboList.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull WeiboViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.videoView.isPlaying()) {
            holder.videoView.pause();
            holder.playButton.setVisibility(View.VISIBLE);
            holder.staticVideo.setVisibility(View.VISIBLE);
            handler.removeCallbacks(updateSeekBar);
        }
    }

    static class WeiboViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar, media, playButton, staticVideo, likeButton, commentButton, deleteButton;
        VideoView videoView;
        ExpandableHeightGridView gridView;
        TextView username, title;
        TextView dzCount;
        SeekBar seekBar;

        WeiboViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            media = itemView.findViewById(R.id.media);
            videoView = itemView.findViewById(R.id.video_view);
            playButton = itemView.findViewById(R.id.play_button);
            staticVideo = itemView.findViewById(R.id.videostatic);
            gridView = itemView.findViewById(R.id.grid_view);
            username = itemView.findViewById(R.id.username);
            title = itemView.findViewById(R.id.title);
            dzCount = itemView.findViewById(R.id.woshidianzan);
            deleteButton = itemView.findViewById(R.id.delete_button);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
            seekBar = itemView.findViewById(R.id.video_seek_bar);
        }
    }

    // 更新数据方法
    public void updateData(List<WeiboInfo> newWeiboList) {
        if (newWeiboList != null) {
            this.weiboList.clear(); // 清空旧数据
            this.weiboList.addAll(newWeiboList); // 添加新数据
            notifyDataSetChanged();
        }
    }
}
