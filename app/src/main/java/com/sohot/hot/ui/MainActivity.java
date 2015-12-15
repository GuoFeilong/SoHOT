package com.sohot.hot.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mylibrary.base.BaseSkinActivity;
import com.sohot.R;
import com.sohot.hot.MyConstant;
import com.sohot.hot.jsoup.GetHotData;
import com.sohot.hot.model.Category;
import com.sohot.hot.model.CategoryForJson;
import com.sohot.hot.model.CategoryItem;
import com.sohot.hot.model.ChangeSkinModel;
import com.sohot.hot.ui.adapter.MyFragmentPagerAdapter;
import com.sohot.hot.ui.categoryfragments.BookFragment;
import com.sohot.hot.ui.categoryfragments.FilmFragment;
import com.sohot.hot.ui.categoryfragments.ZongHePicFragment;
import com.sohot.hot.ui.menupanel.MenuModulePanel;
import com.sohot.hot.ui.view.CircleImageViewGoogle;
import com.sohot.hot.ui.view.CustomShareBoard;
import com.sohot.hot.ui.view.ViewpagerIndicator;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.util.ArrayList;
import java.util.List;

import tools.FileUtils;
import tools.JsonHelper;
import tools.SpTools;
import tools.StatusBarCompat;
import tools.T;
import tools.TimeFormat;
import tools.Tools;
import tools.ViewUtils;


public class MainActivity extends BaseSkinActivity implements NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener, View.OnClickListener {
    private static final String TARGET_URL = "http://my.csdn.net/lmj623565791";
//    private static final String TARGET_URL = "http://www.t66y.com/";
    private static final int CHANGE_ICON = 333;
    public static final int TYPE_PIC = 1;
    public static final int TYPE_FILM = 2;
    public static final int TYPE_BOOK = 3;

    private static final long DELAY_MILLS = 0;

    /**
     * service中的type
     */
    private static final int MSG_SUM = 0x110;
    private static final int THREAD_FLAG = 56;

    private GetHotData.HasGetHotMenuDataListener hasGetL;
    private GetHotData getHotData;
    private ArrayList<Category> hotMenus;

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private ActionBarDrawerToggle toggle;
    //    private NavigationView navigationView;
    private ImageView mMenu;
    private CircleImageViewGoogle mMainIcon;
    private TextView mAppName;
    private TextView mMenuAD;
    private RelativeLayout.LayoutParams layoutParams;
    private int iconH;
    private int iconW;
    private int mSaveIconSize;
    private RelativeLayout.LayoutParams appNameLayoutParams;
    private int appNameH;
    private int appNameW;
    private ArrayList<Integer> allHotIcon;
    private int iconIndex;
    private int iconML;
    private LinearLayout hotMenuContainer;
    private Dialog mDialog;

    private ImageView mSerch;

    private ViewpagerIndicator mIndicator;
    private ViewPager mMainContent;
    private ArrayList<Fragment> mTabContents;
    private ArrayList<String> mDatas;
    private MyFragmentPagerAdapter mAdapter;
    private HorizontalScrollView titleContainer;
    private int windowsWidth;
    private RelativeLayout rl;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHANGE_ICON:
                    mMainIcon.setImageResource(msg.arg1);
                    break;
                case StartActivity.OUT_OF_DATA:
                    unbindService(mConn);
                    mConn = null;
                    mServiceMessenger = null;
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    break;
                case THREAD_FLAG:
                    countDownTime--;
                    if (countDownTime > 0) {
                        freeLeftTime.setSubValue("剩余: ");
                        freeLeftTime.setValueColor(getResources().getColor(R.color.common_red_color));
                        freeLeftTime.setValue(TimeFormat.formatTime(countDownTime));
                    } else {
                        T.show(MainActivity.this, getResources().getString(R.string.out_of_data), 0);
                        FileUtils.initData(countDownTime + "", MyConstant.PHONE_SDCARD_PAHT, MyConstant.HOT_FLAG_FILE_NAME);
                        Message msg1 = handler.obtainMessage(StartActivity.OUT_OF_DATA);
                        handler.sendMessageDelayed(msg1, 2000);
                    }
                    break;
            }

        }
    };

    private Messenger mServiceMessenger;
    private boolean isConn;
    private long mCurrentLeftTime;
    private int mCurrentIndex;
    /**
     * 接收到服务器返回的数据
     */
    private Messenger mMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msgFromServer) {
            switch (msgFromServer.what) {
                case MSG_SUM:
                    // 刷新ui
                    mCurrentLeftTime = (long) msgFromServer.arg2;
                    if (mCurrentLeftTime > 0) {
                        freeLeftTime.setSubValue("剩余: ");
                        freeLeftTime.setValueColor(getResources().getColor(R.color.common_red_color));
                        freeLeftTime.setValue(TimeFormat.formatTime(mCurrentLeftTime));
                    } else {
                        T.show(MainActivity.this, getResources().getString(R.string.out_of_data), 0);
                        FileUtils.initData(mCurrentLeftTime + "", MyConstant.PHONE_SDCARD_PAHT, MyConstant.HOT_FLAG_FILE_NAME);
                        Message msg = handler.obtainMessage(StartActivity.OUT_OF_DATA);
                        handler.sendMessageDelayed(msg, 2000);
                    }
                    break;
            }
            super.handleMessage(msgFromServer);
        }
    });


    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceMessenger = new Messenger(service);
            isConn = true;
            sendMSG();
            Log.e("状态>>>>", "主页ServiceConnection:" + isConn);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceMessenger = null;
            isConn = false;
//            bindServiceInvoked();
            Log.e("状态>>>>", "主页ServiceConnection:" + isConn);
        }
    };
    private MenuModulePanel.SumOption freeLeftTime;
    private String textConent;
    private long countDownTime;
    private PopupWindow popupWindow;
    private LinearLayout moreShare;
    private LinearLayout moreShake;
    private LinearLayout moreAbout;
    private View mToolBarContainer;


    /**
     * 友盟组件相关
     */
    private final UMSocialService mController = UMServiceFactory.getUMSocialService(MyConstant.DESCRIPTOR);
    private SHARE_MEDIA mPlatform = SHARE_MEDIA.SINA;
    public static final int SHAKE_REQUEST_CODE = 44;

    /**
     * 绑定服务
     */
    private void bindServiceInvoked() {
        Intent intent = new Intent();
        intent.setAction("com.so.hot.count");
        intent.setPackage(getPackageName());
        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
        Log.e("主页绑定服务>>>", "执行了bind方法");
    }

    @Override
    protected void onDestroy() {
//        FileUtils.initData(mCurrentLeftTime + "", MyConstant.PHONE_SDCARD_PAHT, MyConstant.HOT_FLAG_FILE_NAME);
//        unbindService(mConn);
//        mConn = null;
//        mServiceMessenger = null;
//        Log.e("退出>>>>>>>>>", "onDestroy: 解绑了countdown服务" + mCurrentLeftTime);


        FileUtils.initData(countDownTime + "", MyConstant.PHONE_SDCARD_PAHT, MyConstant.HOT_FLAG_FILE_NAME);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChangeSkinModel mCurrentChangeSkinModel = (ChangeSkinModel) Tools.readObject(this, ChangeSkinActivity.CURRENT_SKIN_DATA);
        if (mCurrentChangeSkinModel != null) {
            StatusBarCompat.compat(this, mCurrentChangeSkinModel.getCardColor());
        } else {
            StatusBarCompat.compat(this, getResources().getColor(R.color.skin_colorPrimary));
        }

        initData();
        initView();
        initMenuConsole();
        initDicator();
        initEvent();
//        bindServiceInvoked();
        countDownThread();

        // 配置需要分享的相关平台
        configPlatforms();
    }


    /**
     * 配置分享平台参数</br>
     */
    private void configPlatforms() {
        // 添加新浪SSO授权
        SinaSsoHandler sinaSsoHandler = new SinaSsoHandler();
        mController.getConfig().setSsoHandler(sinaSsoHandler);
        mController.getConfig().closeToast();

        // 添加QQ、QZone平台
        addQQQZonePlatform();

        // 添加微信、微信朋友圈平台
        addWXPlatform();
    }


    /**
     * @return
     * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
     * image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
     * 要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
     * : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
     */
    private void addQQQZonePlatform() {
        String appId = "1104998082";
        String appKey = "0F2I9sPNgyDUQHOJ";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, appId, appKey);
        qqSsoHandler.setTargetUrl(TARGET_URL);
        qqSsoHandler.setTitle(MyConstant.SOCIAL_TITLE+MyConstant.SOCIAL_CONTENT);
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, appId, appKey);
        qZoneSsoHandler.setTargetUrl(TARGET_URL);
        qZoneSsoHandler.addToSocialSDK();
    }

    /**
     * @return
     * @功能描述 : 添加微信平台分享
     */
    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        String appId = "wx38bfe121c6813538";
        String appSecret = "d4624c36b6795d1d99dcf0547af5443d";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(this, appId, appSecret);
//        wxHandler.setTargetUrl("https://www.baidu.com/");
        wxHandler.showCompressToast(true);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(this, appId, appSecret);
//        wxCircleHandler.setTargetUrl("https://www.baidu.com/");
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }


    @Override
    protected void onResume() {
        super.onResume();
        ChangeSkinModel mCurrentChangeSkinModel = (ChangeSkinModel) Tools.readObject(this, ChangeSkinActivity.CURRENT_SKIN_DATA);
        if (mCurrentChangeSkinModel != null) {
            StatusBarCompat.compat(this, mCurrentChangeSkinModel.getCardColor());
            if (mIndicator != null) {
                mIndicator.setTabItemTitles(mDatas, windowsWidth, titleContainer, mCurrentChangeSkinModel);
                mIndicator.setCurrentColor(mCurrentIndex);
                fab.setBackgroundTintList(new ColorStateList(new int[][]{new int[0]}, new int[]{mCurrentChangeSkinModel.getCardColor()}));
                fab.setRippleColor(getResources().getColor(R.color.skin_colorPrimary));
//                fab.setRippleColor(mCurrentChangeSkinModel.getCardColor());
            }
        }

    }

    /**
     * 用线程实现
     */
    private void countDownThread() {
        textConent = FileUtils.ReadTxtFile(MyConstant.PHONE_SDCARD_PAHT + "/" + MyConstant.HOT_FLAG_FILE_NAME);
        Log.e("COUNT_DOWN_SERVIECE_没转换", "读取CountDownService: >>>>>>" + "[" + textConent.trim() + "]");
        String trim = textConent.trim();
        countDownTime = Long.parseLong(trim);


        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (countDownTime <= 0) {
                        break;
                    } else {
                        try {
                            Thread.sleep(1000);
                            Message msg = handler.obtainMessage(THREAD_FLAG);
                            msg.obj = countDownTime;
                            handler.sendMessageDelayed(msg, 0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }.start();


    }


    private void sendMSG() {
        Message msgFromClient = Message.obtain(null, MSG_SUM);
        msgFromClient.replyTo = mMessenger;
        if (isConn) {
            //往服务端发送消息
            try {
                mServiceMessenger.send(msgFromClient);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    public void killAll(Context context) {
        //获取一个ActivityManager 对象
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        //获取系统中所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        //获取当前activity所在的进程
        String currentProcess = context.getApplicationInfo().processName;
        //对系统中所有正在运行的进程进行迭代，如果进程名不是当前进程，则Kill掉
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            String processName = appProcessInfo.processName;
//            if (!processName.equals(currentProcess)) {
//                System.out.println("ApplicationInfo-->" + processName);
//                activityManager.killBackgroundProcesses(processName);
//                System.out.println("Killed -->PID:" + appProcessInfo.pid + "--ProcessName:" + processName);
//            }
            activityManager.killBackgroundProcesses(processName);
        }
    }


    /**
     * 初始化指引条i
     */
    private void initDicator() {
        // 设置Tab上的标题
        windowsWidth = getWindowsWidth(this);

        ChangeSkinModel mCurrentChangeSkinModel = (ChangeSkinModel) Tools.readObject(this, ChangeSkinActivity.CURRENT_SKIN_DATA);
        if (mCurrentChangeSkinModel != null) {
            mIndicator.setTabItemTitles(mDatas, windowsWidth, titleContainer, mCurrentChangeSkinModel);
        } else {
            mIndicator.setTabItemTitles(mDatas, windowsWidth, titleContainer);
        }
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mTabContents);
        mAdapter.setFragments(mTabContents);
        mMainContent.setAdapter(mAdapter);
        // 设置关联的ViewPager
        mIndicator.setViewPager(mMainContent, 0);

        /**
         * 指引条的适配器
         */
        mIndicator.setOnPageChangeListener(new ViewpagerIndicator.PageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 获取屏幕的宽度
     */
    public final static int getWindowsWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 初始化侧边菜单栏
     */
    private void initMenuConsole() {
        initFirstModulePanel();
        init2ModulePanel();
        init3thModulePanel();
        init5thModulePanel();
        init6thModulePanel();
        init4thModulePanel();
    }


    private LinearLayout.LayoutParams getCommondParams() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = 0;
        return layoutParams;
    }

    private void init4thModulePanel() {
        final MenuModulePanel s4 = new MenuModulePanel(this);

        final MenuModulePanel.SumOption versionUpdate = new MenuModulePanel.SumOption();
        versionUpdate.setName("退出应用");
        versionUpdate.setIconViewSRC(R.mipmap.topmenu_exit);
        versionUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.anim_goaway, R.anim.anim_goaway);
//                FileUtils.initData(mCurrentLeftTime + "", MyConstant.PHONE_SDCARD_PAHT, MyConstant.HOT_FLAG_FILE_NAME);
//                unbindService(mConn);
//                mConn = null;
//                mServiceMessenger = null;
//                finish();
//                killAll(MainActivity.this);
//                android.os.Process.killProcess(android.os.Process.myPid());


                FileUtils.initData(countDownTime + "", MyConstant.PHONE_SDCARD_PAHT, MyConstant.HOT_FLAG_FILE_NAME);
                finish();

            }
        });
        s4.addOption(versionUpdate);
        hotMenuContainer.addView(s4.getView(), getCommondParams());
    }

    private void init5thModulePanel() {
        final MenuModulePanel s5 = new MenuModulePanel(this);

        freeLeftTime = new MenuModulePanel.SumOption();
        freeLeftTime.setName("免费体验");
        freeLeftTime.setIconViewSRC(R.mipmap.bg_left_time);
        freeLeftTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerOpenOrClose();
            }
        });
        s5.addOption(freeLeftTime);
        hotMenuContainer.addView(s5.getView(), getCommondParams());
    }


    private void init3thModulePanel() {


        final MenuModulePanel s3 = new MenuModulePanel(this);
        MenuModulePanel.TextOptionForBudget budget = new MenuModulePanel.TextOptionForBudget(false);
        budget.setName("夜间模式");
        budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerOpenOrClose();
            }
        });
        MenuModulePanel.OnSwitchCompatLisnter switchCompatLisnter = new MenuModulePanel.OnSwitchCompatLisnter() {
            @Override
            public void hasSwitchCompatClick(SwitchCompat switchCompat) {
                //TODO: 15/10/22   开关事情
            }
        };
        budget.setSwitchCompatLisnter(switchCompatLisnter);

        final MenuModulePanel.SumOption versionUpdate = new MenuModulePanel.SumOption();
        versionUpdate.setName(hotMenus.get(2).getCategoryTitle());
        versionUpdate.setIconViewSRC(R.mipmap.topmenu_icn_book);
        versionUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerOpenOrClose();
                changeContentData(hotMenus.get(2), TYPE_BOOK);
                initDicator();

            }
        });
//        s3.addOption(budget);
        s3.addOption(versionUpdate);
        hotMenuContainer.addView(s3.getView(), getCommondParams());

    }

    /**
     * 设置彩色文字
     */
    private SpannableString setVipText() {
        //改变字体颜色
        //先构造SpannableString
        SpannableString spanString = new SpannableString(getResources().getString(R.string.vip_title));
        //再构造一个改变字体颜色的Span
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.skin_colorAccent));
        //将这个Span应用于指定范围的字体
        spanString.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        span = new ForegroundColorSpan(getResources().getColor(R.color.common_text_color));
        //将这个Span应用于指定范围的字体
        spanString.setSpan(span, 1, 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        span = new ForegroundColorSpan(getResources().getColor(R.color.color_orange));
        //将这个Span应用于指定范围的字体
        spanString.setSpan(span, 2, 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        span = new ForegroundColorSpan(getResources().getColor(R.color.color_purple));
        //将这个Span应用于指定范围的字体
        spanString.setSpan(span, 3, 4, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置给EditText显示出来
        return spanString;
    }


    private void init2ModulePanel() {
        final MenuModulePanel s2 = new MenuModulePanel(this);

        final MenuModulePanel.SumOption versionUpdate = new MenuModulePanel.SumOption();
        versionUpdate.setVShow();
        versionUpdate.setValue(setVipText());
        versionUpdate.getView().setBackgroundColor(getResources().getColor(R.color.half_tran));
        versionUpdate.setName(hotMenus.get(1).getCategoryTitle());
        versionUpdate.setIconViewSRC(R.mipmap.topmenu_icn_film);
        versionUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerOpenOrClose();
                changeContentData(hotMenus.get(1), TYPE_FILM);
                initDicator();
            }
        });
        s2.addOption(versionUpdate);
        hotMenuContainer.addView(s2.getView(), getCommondParams());

    }

    /**
     * 初始化第一组菜单栏
     */
    private void initFirstModulePanel() {
        final MenuModulePanel s1 = new MenuModulePanel(this);

        final MenuModulePanel.SumOption versionUpdate = new MenuModulePanel.SumOption();
        versionUpdate.setName(hotMenus.get(0).getCategoryTitle());
        versionUpdate.setIconViewSRC(R.mipmap.topmenu_icn_zonghetuqu);
        versionUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerOpenOrClose();
                changeContentData(hotMenus.get(0), TYPE_PIC);
                initDicator();
            }
        });
        s1.addOption(versionUpdate);
        hotMenuContainer.addView(s1.getView(), getCommondParams());
    }


    /**
     * 初始化第一组菜单栏
     */
    private void init6thModulePanel() {
        final MenuModulePanel s6 = new MenuModulePanel(this);

        final MenuModulePanel.SumOption versionUpdate = new MenuModulePanel.SumOption();
        versionUpdate.setName(getResources().getString(R.string.changeskin));
        versionUpdate.setIconViewSRC(R.mipmap.topmenu_icn_changeskin);
        versionUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                drawerOpenOrClose();
//                SkinManager.getInstance().changeSkin(MySkinConstant.SKIN_RED);
                ViewUtils.changeActivity(MainActivity.this, ChangeSkinActivity.class);
            }
        });
        s6.addOption(versionUpdate);
        hotMenuContainer.addView(s6.getView(), getCommondParams());
    }


    private void initEvent() {
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.notice_important), Snackbar.LENGTH_LONG)
                        .setAction("Notice", null).show();
            }
        });

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
        drawer.setDrawerListener(this);
        toggle.syncState();
//        navigationView.setNavigationItemSelectedListener(this);
        mMenu.setOnClickListener(this);
        setColorFulText();
        mMainIcon.setOnClickListener(this);
        mSerch.setOnClickListener(this);


        ChangeSkinModel mCurrentChangeSkinModel = (ChangeSkinModel) Tools.readObject(this, ChangeSkinActivity.CURRENT_SKIN_DATA);
        if (mCurrentChangeSkinModel != null) {
//            fab.setRippleColor(mCurrentChangeSkinModel.getCardColor());
            fab.setBackgroundTintList(new ColorStateList(new int[][]{new int[0]}, new int[]{mCurrentChangeSkinModel.getCardColor()}));
            fab.setRippleColor(getResources().getColor(R.color.skin_colorPrimary));
        } else {
            fab.setRippleColor(getResources().getColor(R.color.skin_colorPrimary));
        }

    }


    /**
     * 设置彩色文字
     */
    private void setColorFulText() {
        //改变字体颜色
        //先构造SpannableString
        SpannableString spanString = new SpannableString(getResources().getString(R.string.app_name));
        //再构造一个改变字体颜色的Span
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.skin_colorAccent));
        //将这个Span应用于指定范围的字体
        spanString.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        span = new ForegroundColorSpan(getResources().getColor(R.color.common_text_color));
        //将这个Span应用于指定范围的字体
        spanString.setSpan(span, 1, 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        span = new ForegroundColorSpan(getResources().getColor(R.color.color_orange));
        //将这个Span应用于指定范围的字体
        spanString.setSpan(span, 2, 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        span = new ForegroundColorSpan(getResources().getColor(R.color.color_purple));
        //将这个Span应用于指定范围的字体
        spanString.setSpan(span, 3, 4, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置给EditText显示出来
        mAppName.setText(spanString);
        mMenuAD.setText(spanString);
    }


    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mMenu = (ImageView) findViewById(R.id.iv_actionbar_menu);
        mMainIcon = (CircleImageViewGoogle) findViewById(R.id.civ_main_icon);
        mAppName = (TextView) findViewById(R.id.tv_app_name);
        hotMenuContainer = (LinearLayout) findViewById(R.id.ll_menu_container);
        mMenuAD = (TextView) findViewById(R.id.tv_menu_title);
        mIndicator = (ViewpagerIndicator) findViewById(R.id.vi_title_container);
        titleContainer = (HorizontalScrollView) findViewById(R.id.hsv_titles);
        mMainContent = (ViewPager) findViewById(R.id.vp_content);
        mSerch = (ImageView) findViewById(R.id.iv_actionbar_serch);
        mToolBarContainer = findViewById(R.id.toolbar_container);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        getHotData = new GetHotData();
        hotMenus = JsonHelper.getHelper().genBeanByJson(SpTools.getInstance(getApplicationContext()).readCurrentLanguage(), CategoryForJson.class).getCategories();

        allHotIcon = new ArrayList<>();
        allHotIcon.add(R.mipmap.a12);
        allHotIcon.add(R.mipmap.a13);
        allHotIcon.add(R.mipmap.a14);
        allHotIcon.add(R.mipmap.a15);
        allHotIcon.add(R.mipmap.a16);

        mDatas = new ArrayList<>();
        mTabContents = new ArrayList<>();
        changeContentData(hotMenus.get(0), TYPE_PIC);
//        changeContentData(hotMenus.get(1), TYPE_FILM);
//        changeContentData(hotMenus.get(2), TYPE_BOOK);

        testGetHotData();
    }

    /**
     * 测试方法,用来测试获取数据
     */
    private void testGetHotData() {
//        getHotData.getCategoryTableBookItem(hotMenus.get(2).getCategoryItems().get(0));
//        getHotData.getCategoryTableFilmItem(hotMenus.get(1).getCategoryItems().get(0));
//        getHotData.getCategoryTablePicItem(hotMenus.get(0).getCategoryItems().get(0));
    }

    /**
     * 修改content数据
     *
     * @param category
     */
    private void changeContentData(Category category, int fragmentType) {
        mDatas.clear();
        mTabContents.clear();

        ArrayList<CategoryItem> picItems = category.getCategoryItems();
        for (int i = 0; i < picItems.size() - 3; i++) {
            mDatas.add(picItems.get(i).getCategoryItemTitle());
            if (fragmentType == TYPE_PIC) {
                ZongHePicFragment temp = new ZongHePicFragment(picItems.get(i));
                mTabContents.add(temp);
            } else if (fragmentType == TYPE_FILM) {
                FilmFragment filmFragment = new FilmFragment(picItems.get(i));
                mTabContents.add(filmFragment);
            } else if (fragmentType == TYPE_BOOK) {
                BookFragment bookFragment = new BookFragment(picItems.get(i));
                mTabContents.add(bookFragment);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Tools.doublePressExit(this);
            FileUtils.initData(countDownTime + "", MyConstant.PHONE_SDCARD_PAHT, MyConstant.HOT_FLAG_FILE_NAME);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        layoutParams = (RelativeLayout.LayoutParams) mMainIcon.getLayoutParams();
        iconH = layoutParams.height;
        iconW = layoutParams.width;
        iconML = layoutParams.leftMargin;

        mSaveIconSize = iconW;

        appNameLayoutParams = (RelativeLayout.LayoutParams) mAppName.getLayoutParams();
        appNameH = appNameLayoutParams.height;
        appNameW = appNameLayoutParams.width;


        Log.i("APP_NAME", "onWindowFocusChanged: " + appNameH + "-------" + appNameW);

    }

    /**
     * 主页icon动画
     *
     * @param slideOffset
     */
    private void startMainIconAnim(float slideOffset) {
        if (slideOffset > 0) {
            mAppName.setVisibility(View.VISIBLE);
        } else {
            mAppName.setVisibility(View.GONE);
        }
        layoutParams.height = (int) (iconH * (1 - slideOffset));
        layoutParams.width = (int) (iconW * (1 - slideOffset));
        layoutParams.leftMargin = (int) (iconML * (1 + slideOffset));
        mMainIcon.setLayoutParams(layoutParams);
        mMainIcon.setAlpha(1 - slideOffset);

        appNameLayoutParams.height = (int) (appNameH * slideOffset);
        appNameLayoutParams.width = (int) (appNameW * slideOffset);
        mAppName.setLayoutParams(appNameLayoutParams);
        mAppName.setAlpha(slideOffset);

    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
//        Log.i("抽屉的偏移量", "onDrawerSlide: >>>>>>>" + slideOffset);
        startMainIconAnim(slideOffset);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        StatusBarCompat.compat(this, getResources().getColor(R.color.statusbar_start_color));
        // 因为在drawerslide中执行的动画有精度损失所以为了避免看不到每次状态打开的适合给他恢复到原始值
        appNameLayoutParams.width = 240;
        appNameLayoutParams.height = 120;
        mAppName.setLayoutParams(appNameLayoutParams);
        changeMainIcon();
    }

    private void changeMainIcon() {
        Message msg = handler.obtainMessage();
        msg.what = CHANGE_ICON;
        msg.arg1 = allHotIcon.get(iconIndex % allHotIcon.size());
        handler.sendMessageDelayed(msg, DELAY_MILLS);
        iconIndex++;
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        ChangeSkinModel mCurrentChangeSkinModel = (ChangeSkinModel) Tools.readObject(this, ChangeSkinActivity.CURRENT_SKIN_DATA);
        if (mCurrentChangeSkinModel != null) {
            StatusBarCompat.compat(this, mCurrentChangeSkinModel.getCardColor());
        } else {
            StatusBarCompat.compat(this, getResources().getColor(R.color.skin_colorPrimary));
        }
        // 因为在drawerslide中执行的动画有精度损失所以为了避免看不到每次状态打开的适合给他恢复到原始值
        layoutParams.width = 150;
        layoutParams.height = 150;
        mMainIcon.setLayoutParams(layoutParams);
        Log.e("DRAWER_CLOSED", "onDrawerClosed: >>>>>>我已经关闭了<<<<<" + layoutParams.height);

    }

    @Override
    public void onDrawerStateChanged(int newState) {
        Log.e(newState + "====", "onDrawerStateChanged: " + newState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_actionbar_menu:
                drawerOpenOrClose();
                break;
            case R.id.civ_main_icon:
//                showCustomDialog();
                break;
            case R.id.ibt_dialog_close:
                mDialog.dismiss();
                break;
            case R.id.iv_actionbar_serch:
                showMorePopWindow();
                break;
            case R.id.ll_about:
                popupWindow.dismiss();
                showCustomDialog();
                break;
            case R.id.ll_shake:
                popupWindow.dismiss();
                Intent intent = new Intent(this, ShakeTimeActivity.class);
                startActivityForResult(intent, SHAKE_REQUEST_CODE);
//                ViewUtils.changeActivity(this,ShakeTimeActivity.class);
                break;
            case R.id.ll_share:
                popupWindow.dismiss();
                setMyShareContent();
                // 自定义分享面板
                postShare();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SHAKE_REQUEST_CODE:
                if (resultCode == ShakeTimeActivity.SHAKE_RESULT_CODE) {
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        int shakeTime = bundle.getInt(ShakeTimeActivity.SHAKE_TIME_KEY);
                        countDownTime = countDownTime + shakeTime;
                    }
                }
                break;
        }
    }

    /**
     * 自定义分享面板
     */
    private void postShare() {
        CustomShareBoard shareBoard = new CustomShareBoard(this);
        shareBoard.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    private void setMyShareContent() {

        String description = "";
//        String targetUrl = "https://www.baidu.com/";

        String picUrl = "";
        String title = "";


        // 配置SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        UMImage urlImage = new UMImage(this, picUrl);


        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(MyConstant.SOCIAL_CONTENT);
        weixinContent.setTitle(MyConstant.SOCIAL_TITLE);
        weixinContent.setTargetUrl(TARGET_URL);
        weixinContent.setShareMedia(new UMImage(this, ViewUtils.getScreeShort(this)));
//        weixinContent.setShareMedia(urlImage);
        mController.setShareMedia(weixinContent);

        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(MyConstant.SOCIAL_CONTENT);
        circleMedia.setTitle(MyConstant.SOCIAL_TITLE);
        circleMedia.setShareMedia(new UMImage(this, ViewUtils.getScreeShort(this)));
//        circleMedia.setShareMedia(urlImage);
        circleMedia.setTargetUrl(TARGET_URL);
        mController.setShareMedia(circleMedia);

        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setShareContent(MyConstant.SOCIAL_CONTENT);
        sinaContent.setShareMedia(new UMImage(this, ViewUtils.getScreeShort(this)));
        sinaContent.setTitle(MyConstant.SOCIAL_TITLE);
        sinaContent.setTargetUrl(TARGET_URL);
        mController.setShareMedia(sinaContent);

    }

    /**
     * 显示更多的popwindow
     */
    private void showMorePopWindow() {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(this);
        }
        View view = LayoutInflater.from(this).inflate(R.layout.popwindow_more, null, false);

        moreShare = (LinearLayout) view.findViewById(R.id.ll_share);
        moreShake = (LinearLayout) view.findViewById(R.id.ll_shake);
        moreAbout = (LinearLayout) view.findViewById(R.id.ll_about);

        moreAbout.setOnClickListener(this);
        moreShake.setOnClickListener(this);
        moreShare.setOnClickListener(this);

        popupWindow.setContentView(view);
        popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        ColorDrawable dw = new ColorDrawable(0x00000000);
//        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(mToolBarContainer, Gravity.RIGHT | Gravity.TOP, 50, 210);
    }

    /**
     * 抽屉的开关
     */
    private void drawerOpenOrClose() {
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }

        }
    }


    /**
     * 显示自定义dialog,在window中加动画
     */
    private void showCustomDialog() {
        if (mDialog == null) {
            mDialog = new Dialog(this, R.style.myDialogTheme);
        }
        mDialog.setContentView(R.layout.dialog_usercenter_shared);

        mDialog.setCancelable(false);
        rl = (RelativeLayout) mDialog.findViewById(R.id.rl_container);
        ImageView ibt_dialog_close = (ImageView) mDialog.findViewById(R.id.ibt_dialog_close);
        ibt_dialog_close.setOnClickListener(this);

        Window window = mDialog.getWindow();
        window.setWindowAnimations(R.style.dialogAnimationStyle);

        mDialog.show();

        LayoutAnimationController controller = new LayoutAnimationController(AnimationUtils.loadAnimation(this, R.anim.custom_item_anim));
        controller.setDelay(0.8f);
        rl.setLayoutAnimation(controller);

    }

}
