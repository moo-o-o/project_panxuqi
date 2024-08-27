# 小米实践课程说明

## **第一天**
## 演示视频在videos文件夹下，为video_1
声明与条款

![Day2](images/11.png)

用户协议

![Day2](images/12.png)

隐私政策

![Day2](images/13.png)

我的

![Day2](images/14.png)

登录账号

![Day2](images/15.png)

验证码倒计时

![Day2](images/16.png)

错误的验证码

![Day2](images/17.png)

登录成功

![Day2](images/18.png)

个人信息页面

![Day2](images/19.png)


## **第二天** 

## 演示视频在videos文件夹下，为video_2

加载中

![Day2](images/21.png)

推荐页面

![Day2](images/22.png)

视频进度条，可能有点不明显

![Day2](images/23.png)

大图

![Day2](images/24.png)

下拉刷新

![Day2](images/25.png)

九宫格图

![Day2](images/26.png)

无更多内容

![Day2](images/27.png)

点赞

![Day2](images/28.png)

评论

![Day2](images/29.png)

删除帖子

![Day2](images/210.png)

进度条2

![Day2](images/211.png)

网络请求失败

![Day2](images/212.png)


## 第三天  

## 演示视频在videos文件夹下，为video_3，由于模拟器无法完成图片下载，所以图片下载部分用了手机截图和录制。

删除帖子

![Day3](images/31.png)

第一条帖子的评论

![Day3](images/32.png)

第二条帖子的评论

![Day3](images/33.png)

点赞演示

![Day3](images/34.png)

单张图片的大图页面

![Day3](images/35.png)

多张图片的大图页面2/9

![Day3](images/36.png)

多张图片的大图页面3/9

![Day3](images/37.png)

大图页面顶部显示分页、头像、昵称、下载

![Day3](images/38.png)

下载完成

![Day3](images/39.jpg)



## 项目整体联调
### 功能联调完成后，进⾏布局优化，优化后单独提交
优化后的布局已经只有少部分的过度绘制存在，效果还可以
![Overdraw](images/over1.jpg)
![Overdraw](images/over2.jpg)
![Overdraw](images/over3.jpg)
![Overdraw](images/over4.jpg)

### 功能联调完成后，检测内存泄漏并进⾏优化，优化后单独提交
--1--
 1 APPLICATION LEAKS
                                                                                                    
    References underlined with "~~~" are likely causes.
    Learn more at https://squ.re/leaks.
    
    2339756 bytes retained by leaking objects
    Displaying only 1 leak trace out of 3 with the same signature
    Signature: 5b8aa14110be8ce47391f276a3045d805591d2e3
    ┬───
    │ GC Root: System class
    │
    ├─ android.net.ConnectivityManager class
    │    Leaking: NO (a class is never leaking)
    │    ↓ static ConnectivityManager.sCallbacks
    │                                 ~~~~~~~~~~
    ├─ java.util.HashMap instance
    │    Leaking: UNKNOWN
    │    Retaining 24.1 MB in 84767 objects
    │    ↓ HashMap[instance @344713088 of android.net.NetworkRequest]
    │             ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ├─ com.example.weibo_panxuqi.fragment.HomeFragment$3 instance
    │    Leaking: UNKNOWN
    │    Retaining 1.0 MB in 8461 objects
    │    Anonymous subclass of android.net.ConnectivityManager$NetworkCallback
    │    ↓ HomeFragment$3.this$0
    │                     ~~~~~~
    ╰→ com.example.weibo_panxuqi.fragment.HomeFragment instance
    ​     Leaking: YES (ObjectWatcher was watching this because com.example.weibo_panxuqi.fragment.HomeFragment received
    ​     Fragment#onDestroy() callback. Conflicts with Fragment.mLifecycleRegistry.state is INITIALIZED)
    ​     Retaining 1.0 MB in 8460 objects
    ​     key = 1a31dc8b-acae-48eb-9af8-5ba2c605743e
    ​     watchDurationMillis = 55745
    ​     retainedDurationMillis = 50744
泄漏发生在 HomeFragment 中注册的 ConnectivityManager.NetworkCallback 对象没有在 HomeFragment 销毁时取消注册。在 HomeFragment 的 onDestroy() 方法中取消网络回调的注册。


--2--
![Day3](images/leak1.png)
LoginActivity中的CountDownTimer在Activity被销毁后仍然持有对Activity的引用。导致Activity无法被垃圾回收，从而引发内存泄漏。
在LoginActivity的onDestroy()方法中取消CountDownTimer。