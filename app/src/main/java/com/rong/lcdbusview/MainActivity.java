package com.rong.lcdbusview;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.os.*;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.animation.*;
import android.widget.*;
import com.rong.lcdbusview.animations.FlipAnimation;
import com.rong.lcdbusview.datas.ActionType;
import com.rong.lcdbusview.datas.AdvertisementData;
import com.rong.lcdbusview.datas.PlayFile;
import com.rong.lcdbusview.link.StationMsg;
import com.rong.lcdbusview.service.MainService;
import com.rong.lcdbusview.tools.LogTools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private WebView mWebView;
	private MyBroadcastReceiver myBroadcast;
	private static final int WATH_SHOW_1 = 0x01;
	private static final int WATH_SHOW_2 = 0x02;
	private static final int WATH_SHOW_3 = 0x03;
	private static final int WATH_SHOW_4 = 0x04;
	private static final int WATH_SHOW_5 = 0x05;
	private static final String DEFAULT_FILE_PATH = Environment.getExternalStorageDirectory() + "/梦娃送吉祥送美德.mp4";
	private SurfaceView mVideoSurface;
	private MediaPlayer mCurrentMediaPlayer;
	private int mIndex = 0;
	public static int DURATION = 15000;
	private PlayFile playFile;
	private int playNum;
	private SurfaceHolder holder;
	private ImageView adImg;
	private Button tv_buton;
	private FrameLayout fl_ad;
	private LinearLayout ll_content;
	private ViewStub vs_ad;
	private RecyclerView rv_show_route;
	private TextView tv_next_station;
	private View v_link_station;
	private TextView tv_current_station;
	private RelativeLayout rl_station_content;
	private TextView tv_last_time;
	private TextView tv_first_time;
	private TextView tv_end_station;
	private TextView tv_start_station;
	private TextView tv_route_name;
	private StationsAdapter stationsAdapter;
	private LinearLayout ll_next_station;

	private MainService bindService = null;
	private List<StationMsg> currentRoute = null;
	private String routeName = null;
	private int currentStation = 0;
    private List<AdvertisementData> mAdvertisementDataList;
	private List<AdvertisementData> mOldAdvertisementDataList;
	private LinearLayoutManager linearLayoutManager;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			switch (what) {
				case WATH_SHOW_1:
//					mWebView.reload();
//					mWebView.loadUrl("file://" + "/mnt/sdcard/webView/" + "index.html");
                    rl_station_content.setBackgroundResource(R.color.gray_td);
                    ll_next_station.setVisibility(View.GONE);
                    v_link_station.setVisibility(View.GONE);
					break;
				case WATH_SHOW_2:
//					mWebView.reload();
//					mWebView.loadUrl("file://" + "/mnt/sdcard/webView/" + "page.html");
                    rl_station_content.setBackgroundResource(R.color.red_td);
                    ll_next_station.setVisibility(View.GONE);
                    v_link_station.setVisibility(View.GONE);
					break;
				case WATH_SHOW_3:
					tv_route_name.setText(routeName);
					if(currentRoute != null && !currentRoute.isEmpty()){
                        //设置布局管理器
						linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        rv_show_route.setLayoutManager(linearLayoutManager);
						stationsAdapter = new StationsAdapter(MainActivity.this,currentRoute);
						rv_show_route.setAdapter(stationsAdapter);
						stationsAdapter.notifyDataSetChanged();
						tv_current_station.setText(currentRoute.get(currentStation).getStationName());
						tv_start_station.setText(currentRoute.get(0).getStationName());
						tv_end_station.setText(currentRoute.get(currentRoute.size() -1).getStationName());
						if(currentRoute.size() > currentStation + 1) {
							tv_next_station.setText(currentRoute.get(currentStation + 1).getStationName());
						}
						if(currentRoute.size() > 60){
							hideAD();
						}else {
							showAD();
						}
						stationsAdapter.refreshStations();
					}
					break;
                case WATH_SHOW_4:
                    rl_station_content.setBackgroundResource(R.color.red_td);
                    ll_next_station.setVisibility(View.VISIBLE);
                    v_link_station.setVisibility(View.VISIBLE);
                    break;
				case WATH_SHOW_5:
					circleAnim();
					break;
			}
		}
	};


	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MainService.MyBinder binder = (MainService.MyBinder) service;
			bindService = binder.getService();
			Log.e(TAG,"onServiceConnected  name" + name.toShortString());
			if(bindService != null) {
				currentRoute = bindService.getCurrentRoute();
				routeName = bindService.getRoutename();
				currentStation = bindService.getCurrentStation();
//				mHandler.sendEmptyMessage(WATH_SHOW_5);
				mHandler.sendEmptyMessage(WATH_SHOW_3);
//				mHandler.sendEmptyMessageDelayed(WATH_SHOW_3,20);
			}
		}

		//client 和service连接意外丢失时，会调用该方法
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e(TAG,"onServiceDisconnected  name" + name.toShortString());
			if(bindService != null){
				bindService = null;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

			//checkAccouts();

			// 检查该权限是否已经获取
			int i = ContextCompat.checkSelfPermission(this, permissions[0]);
			// 权限是否已经 授权 GRANTED---授权  DINIED---拒绝

//			boolean isAllGranted = checkPermissionAllGranted(permissions);

			if (i != PackageManager.PERMISSION_GRANTED) {
				// 如果没有授予该权限，就去提示用户请求
				showDialogTipUserRequestPermission();
			}
		}
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		setContentView(R.layout_ad.activity_main);
		setContentView(R.layout.activity_main_zj);
        Intent intent = new Intent(this, MainService.class);
        startService(intent);
        bindService(intent,conn,BIND_AUTO_CREATE);
		initFindView();
		//init();
		initeReceiver();
		// openVideo();
		//initVideo();
//		initButun();
		vs_ad.setVisibility(View.VISIBLE);
//		startInStation();
//		fl_ad = findViewById(R.id.fl_ad);
//		ll_content = findViewById(R.id.ll_content);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	private void hideAD(){
		vs_ad.setVisibility(View.GONE);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.weight = 96;
		ll_content.setLayoutParams(layoutParams);
	}
	private void showAD(){
		vs_ad.setVisibility(View.VISIBLE);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.weight = 73;
		ll_content.setLayoutParams(layoutParams);
		initVideo();
	}

	private void initFindView() {
		ll_content = (LinearLayout) findViewById(R.id.ll_content);
		rv_show_route = (RecyclerView) findViewById(R.id.rv_show_route);
		tv_next_station = (TextView) findViewById(R.id.tv_next_station);
		v_link_station = findViewById(R.id.v_link_station);
		tv_current_station = (TextView) findViewById(R.id.tv_current_station);
		rl_station_content = (RelativeLayout) findViewById(R.id.rl_station_content);
		tv_last_time = (TextView) findViewById(R.id.tv_last_time);
		tv_first_time = (TextView) findViewById(R.id.tv_first_time);
		tv_end_station = (TextView) findViewById(R.id.tv_end_station);
		tv_start_station = (TextView) findViewById(R.id.tv_start_station);
		tv_route_name = (TextView) findViewById(R.id.tv_route_name);
		vs_ad = (ViewStub) findViewById(R.id.vs_ad);
        ll_next_station = (LinearLayout) findViewById(R.id.ll_next_station);
	}

	private void initButun() {
		tv_buton = (Button) vs_ad.findViewById(R.id.button1);
		tv_buton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LogTools.d(TAG,"按钮事件");
				//AlertDialog.Builder builder = new Builder(MainActivity.this);
				AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("版本号");
				//builder.setIcon(R.drawable.ic_launcher);
				//builder.setIcon("@drawable/mmexport");
				builder.setMessage("VH288HX-TP190218-03");
				builder.show();
			}
		});
	}
/*
	private void initButun() {
		String IDnum = "";
		tv_buton = (Button) findViewById(R.id.button1);
		tv_buton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LogTools.d(TAG,"按钮事件");
				//AlertDialog.Builder builder = new Builder(MainActivity.this);
				AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("请输入ID号");
				//builder.setIcon(R.drawable.ic_launcher);
				//builder.setIcon("@drawable/mmexport");
				builder.setView(new EditText(MainActivity.this));

				builder.setPositiveButton("保存" ,  new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						LogTools.d(TAG,"保存配置文件");
						dialog.cancel();
					}
				});
				builder.setNegativeButton("取消", null);
				builder.show();
			}
		});
	}
*/
	@SuppressLint("SetJavaScriptEnabled")
	private void init() {
		mWebView = (WebView) findViewById(R.id.webView1);
		// mWebView.loadUrl("file://"+"/storage/emulated/0/webView/"+"disweb.html");
		// holder.mWebView.loadUrl("http://www.baidu.com/");
		// holder.addCallback(new MyCallBack());
		WebSettings settings = mWebView.getSettings();
		// settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		// settings.setAppCacheEnabled(true);
		// 允许JavaScript执行
		settings.setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;// false 显示frameset, true 不显示Frameset
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// 有页面跳转时被回调
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// 页面跳转结束后被回调
				// view.loadUrl(url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

			}
		});
		mWebView.loadUrl("file://" + "/mnt/sdcard/webView/" + "index.html");
	}

	private void initVideo() {
		mIndex = 0;
		if (MainApplication.getInstance().playFiles != null && !MainApplication.getInstance().playFiles.isEmpty()) {
			playNum = MainApplication.getInstance().playFiles.size();
			playFile = MainApplication.getInstance().playFiles.get(mIndex);
		}
		mCurrentMediaPlayer = new MediaPlayer();
		adImg = (ImageView) findViewById(R.id.imageView1);
		mVideoSurface = (SurfaceView) findViewById(R.id.sv_next);
		adImg.setImageResource(R.drawable.adver);
		adImg.setVisibility(View.VISIBLE);
		holder = mVideoSurface.getHolder();
		holder.addCallback(new MyCallBack());
		// mHandler.postDelayed(mPlayRun, 2000);
		mHandler.postDelayed(mPlayRun, 1000);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unintReceiver();
		unbindService(conn);
	}

	private void initeReceiver() {
		if (myBroadcast == null) {
			myBroadcast = new MyBroadcastReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(ActionType.ACTION_RELOAD_IN);
            filter.addAction(ActionType.ACTION_RELOAD_OUT);
			filter.addAction(ActionType.ACTION_ROUTE);
			filter.addAction(ActionType.ACTION_AD_CHANGE);
			registerReceiver(myBroadcast, filter);
		}
	}

	/**
	 * 旋转动画
	 */
	private void circleAnim() {
//		Animation circle_anim = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.anim_round_rotate);
//		LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
//		circle_anim.setInterpolator(interpolator);
//		if (circle_anim != null) {
//			rv_show_route.startAnimation(circle_anim);  //开始动画
//		}
//		ObjectAnimator animator1 = ObjectAnimator.ofFloat(rv_show_route, "rotationX", 0, 170,260,359);
//		LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
//		animator1.setInterpolator(interpolator);
//		animator1.setDuration(1000).start();
		FlipAnimation flipAinm = new FlipAnimation(rv_show_route.getWidth(),rv_show_route.getHeight(),-180f, 0f);
		flipAinm.setFillAfter(true);
		flipAinm.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
//				view.setImageResource(R.drawable.image2);
//				playFlip2Anim();
				rv_show_route.clearAnimation();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		flipAinm.setInterpolator(new AccelerateInterpolator());
		flipAinm.setDuration(1500);
		rv_show_route.clearAnimation();
		rv_show_route.startAnimation(flipAinm);
	}

	/*private void playFlip2Anim(){
		FlipAnimation flipAinm = new FlipAnimation(80f, 0f);
		flipAinm.setFillAfter(true);
		flipAinm.setInterpolator(new AccelerateInterpolator());
		flipAinm.setDuration(2000);
		rv_show_route.clearAnimation();
		rv_show_route.startAnimation(flipAinm);
	}*/




	private void unintReceiver() {
		if (myBroadcast != null) {
			unregisterReceiver(myBroadcast);
			myBroadcast = null;
		}
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			LogTools.d(TAG, "收到广播" + action);

			switch (action) {
                case ActionType.ACTION_RELOAD_IN:
                    LogTools.d(TAG, "重新加载html");
                    if(bindService != null) {
                        currentRoute = bindService.getCurrentRoute();
                        routeName = bindService.getRoutename();
                        currentStation = bindService.getCurrentStation();
                        mHandler.sendEmptyMessage(WATH_SHOW_3);
                    }
                    startInStation();
//                    mHandler.sendEmptyMessage(WATH_SHOW_2);
//                    showStation();
                    break;
                case ActionType.ACTION_RELOAD_OUT:
                    stopInStation();
                    mHandler.sendEmptyMessage(WATH_SHOW_4);
                    break;
                case ActionType.ACTION_AD_CHANGE:
                    if(bindService != null) {
                        mAdvertisementDataList = bindService.getmAdvertisementDataList();
                        LogTools.d(TAG,"ACTION_AD_CHANGE" + mAdvertisementDataList.toString());
                        clearADMessageCache();
						mHandler.postDelayed(mPlayRun, 1000);
                    }
                    break;
                case ActionType.ACTION_ROUTE:
//                    mHandler.sendEmptyMessage(WATH_SHOW_1);
                    if(bindService != null) {
                        currentRoute = bindService.getCurrentRoute();
                        routeName = bindService.getRoutename();
                        currentStation = bindService.getCurrentStation();
                        mHandler.sendEmptyMessage(WATH_SHOW_5);
                        mHandler.sendEmptyMessageDelayed(WATH_SHOW_3,20);
                    }
                    break;

                default:
                    break;
			}
		}

	}

	private void clearADMessageCache(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(mOldAdvertisementDataList != null && !mOldAdvertisementDataList.isEmpty()){
					for (AdvertisementData advertisementData : mOldAdvertisementDataList){
						if(advertisementData != null){
							final String path = advertisementData.getSavePath();
							if(!TextUtils.isEmpty(path)){
								File file = new File(path);
								if(file.exists()){
									file.delete();
								}
							}
						}
					}
				}
				mOldAdvertisementDataList = mAdvertisementDataList;
			}
		}).start();
	}

	private Timer showtimer;
	private TimerTask showtimertask;
	private void showStation() {
		closeShow();
		if(showtimer == null)
		{
			showtimer = new Timer();
			showtimertask = new TimerTask() {
				@Override
				public void run() {
					mHandler.sendEmptyMessage(WATH_SHOW_1);
				}
			};
			showtimer.schedule(showtimertask, 10*1000);
		}
	}

	private void closeShow() {
		if (showtimer != null && showtimertask != null) {
			showtimer.cancel();
			showtimertask.cancel();
			showtimer = null;
			showtimertask = null;
		}
	}


	/*
	private class ShowStation extends Thread {
		@Override
		public void run() {
			super.run();
			if (interrupted()) {
				return;
			}
			try {
				mHandler.sendEmptyMessage(WATH_SHOW_2);
				Thread.sleep(1000 * 10);// 设置显示时间
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				mHandler.sendEmptyMessage(WATH_SHOW_1);
			}
		}
	}
*/
	Runnable mPlayRun = new Runnable() {

		@Override
		public void run() {
			if (mAdvertisementDataList != null) {
				AdvertisementData advertisementData = null;
                final int size = mAdvertisementDataList.size();
                if (mIndex < size) {
                    advertisementData = mAdvertisementDataList.get(mIndex);
                    mIndex++;
                } else {
                    mIndex = 0;
                    advertisementData = mAdvertisementDataList.get(mIndex);
                }
                if(advertisementData != null) {
                    final String path = advertisementData.getSavePath();
                    final int type = advertisementData.getType();
                    if ( type == 1) {
                        Log.d(TAG, "显示视频：" + advertisementData.getName());
                        mVideoSurface.setVisibility(View.VISIBLE);
                        adImg.setVisibility(View.GONE);
                        try {
                            mCurrentMediaPlayer.reset();
                            mCurrentMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mCurrentMediaPlayer.setDataSource(path);
                            mCurrentMediaPlayer.prepare();
                            mCurrentMediaPlayer.start();
                            mCurrentMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mCurrentMediaPlayer.stop();
                                    // mCurrentMediaPlayer.
                                    mHandler.postDelayed(mPlayRun, 100);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if(type == 2) {
                        // mNextSurface.setBackground(Drawable.createFromPath(path));
                        Log.d(TAG, "显示图片：" + advertisementData.getName());
                        mVideoSurface.setVisibility(View.GONE);
                        adImg.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(path)) {
                            adImg.setImageDrawable(Drawable.createFromPath(path));
                        }
                        mHandler.postDelayed(mPlayRun, 1000 * playFile.getStopTime());
                    }

                }
			}
		}
	};

	private class MyCallBack implements SurfaceHolder.Callback {
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			mCurrentMediaPlayer.setDisplay(holder);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {

		}
	}

	public class StationsAdapter extends
			RecyclerView.Adapter<StationsAdapter.ViewHolder> {
		private LayoutInflater mInflater;
		private List<StationMsg> mRoute;
		private static final int OUT_STATION = 0;
		private static final int CURRENT_STATION = 1;
		private static final int IN_STATION = 2;
		private int animatorItem = 0;
		private Animation animation;


		public StationsAdapter(Context context,List<StationMsg> route) {
			mInflater = LayoutInflater.from(context);
			this.mRoute = route;
		}

		public class ViewHolder extends RecyclerView.ViewHolder {
			public ViewHolder(View arg0) {
				super(arg0);
			}
			ImageView bus;
			TextView stationName;
			View left;
			View right;
		}

		@Override
		public int getItemCount() {
			return mRoute.size();
		}

		@Override
		public int getItemViewType(int position) {
			if(position > currentStation){
				return IN_STATION;
			}else if(position < currentStation){
				return OUT_STATION;
			}else if (position == currentStation){
				return CURRENT_STATION;
			}
			return position;
		}


		/**
		 * 创建ViewHolder
		 */
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
			final int vg_width = viewGroup.getWidth();
			final int size = mRoute.size();
			final int item_width = vg_width/size;
			final int s = vg_width%size;
			final int n = s/2;
			Log.d("vr", "onCreateViewHolder vg_width = " +vg_width +",item_width ="+ item_width  +",s =" + s+",i =" + i);
			View view = mInflater.inflate(R.layout.item_station_view,
					viewGroup, false);
			view.getLayoutParams().width = item_width;
			ViewHolder viewHolder = new ViewHolder(view);
			viewHolder.bus = (ImageView) view
					.findViewById(R.id.iv_stations_bus);
			viewHolder.bus.setMaxWidth(item_width/2);
			viewHolder.stationName = (TextView) view
					.findViewById(R.id.tv_stations_station_name);
			viewHolder.left = view.findViewById(R.id.v_left_link);
			viewHolder.right = view.findViewById(R.id.v_right_link);
			return viewHolder;
		}

		/**
		 * 设置值
		 */
		@Override
		public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
			if(mRoute != null && !mRoute.isEmpty()){
				final StationMsg stationMsg = mRoute.get(i);
				final int type = getItemViewType(i);
				switch (type){
					case OUT_STATION:
						viewHolder.left.setBackgroundResource(R.color.gray_td);
						viewHolder.right.setBackgroundResource(R.color.gray_td);
						viewHolder.stationName.setText(stationMsg.getStationName());
						viewHolder.bus.setImageResource(R.drawable.arrow_lead);
						break;
					case CURRENT_STATION:
						viewHolder.left.setBackgroundResource(R.color.green_td);
						viewHolder.right.setBackgroundResource(R.color.green_td);
						viewHolder.stationName.setText(stationMsg.getStationName());
						viewHolder.stationName.setTextColor(Color.WHITE);
						viewHolder.stationName.setBackgroundResource(R.color.red_td);
						viewHolder.bus.setImageResource(R.drawable.arrows_f);
						break;
					case IN_STATION:
						viewHolder.left.setBackgroundResource(R.color.green_td);
						viewHolder.right.setBackgroundResource(R.color.green_td);
						viewHolder.stationName.setText(stationMsg.getStationName());
						viewHolder.bus.setImageResource(R.drawable.arrows_f);
						break;
				}
			}
		}

		@Override
		public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
//            super.onBindViewHolder(holder, position, payloads);
			if (payloads.isEmpty()) {
				Log.d(TAG, "onBindViewHolder position =" + position);
				//onBindViewHolder(holder, position);
				super.onBindViewHolder(holder, position, payloads);
			} else {
				Log.d(TAG, "onBindViewHolder position =" + position);
			}
		}


		public void refreshStations(){
			if(animation != null && animation.hasStarted()){
				animation.cancel();
				animation = null;
			}
			if(animatorItem == 0){
				animatorItem = currentStation;
			}
//			LogTools.d(TAG,"animatorScale animatorItem =" + animatorItem);
//			int firstItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
//			View view = rv_show_route.getChildAt(animatorItem - firstItemPosition + 1);
//			LogTools.d(TAG,"animatorScale findViewByPosition firstItemPosition =" + firstItemPosition);
//			if(view != null) {
				ViewHolder viewHolder = (ViewHolder) rv_show_route.findViewHolderForLayoutPosition(animatorItem);
				LogTools.d(TAG,"animatorScale getChildViewHolder animatorItem =" + animatorItem);
				if(viewHolder != null){
					LogTools.d(TAG,"animatorScale animatorScale animatorItem =" + animatorItem);
					animatorScale(viewHolder.bus);
				}
//			}
		}


		private void animatorScale(View view){
			if(animation == null || animation.hasEnded()) {
				LogTools.d(TAG,"animatorScale");
				animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f);
				animation.setDuration(1000);//动画时间
				animation.setRepeatCount(1);//动画的重复次数
				animation.setFillAfter(true);//设置为true，动画转化结束后被应用
				animation.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						animation.cancel();
						StationsAdapter.this.animation = null;
						final int size = getItemCount();
						if(animatorItem < size){
							animatorItem ++;
							refreshStations();
						}
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}
				});
				view.startAnimation(animation);//开始动画
			}
		}
	}

	private Timer inStationTimer;
	private InStationTimer inStationTimerTask;

	private void startInStation(){
	    if(inStationTimer == null){
	        inStationTimer = new Timer();
	        inStationTimerTask = new InStationTimer();
	        inStationTimer.schedule(inStationTimerTask,10,1000);
        }
    }

    private void stopInStation(){
	    if(inStationTimer != null){
	        inStationTimerTask.cancel();
	        inStationTimer.cancel();
	        inStationTimerTask = null;
	        inStationTimer = null;
        }
    }

	private class InStationTimer extends TimerTask{

	    private boolean isRed = true;

        @Override
        public void run() {
            if(isRed){
                mHandler.sendEmptyMessage(WATH_SHOW_1);
                isRed = false;
            }else {
                mHandler.sendEmptyMessage(WATH_SHOW_2);
                isRed = true;
            }
        }
    }


	private static final int MY_PERMISSION_REQUEST_CODE = 10000;
	private String[] permissions = {
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_CONTACTS,
			Manifest.permission.INTERNET,
			Manifest.permission.READ_EXTERNAL_STORAGE
//			Manifest.permission.RECEIVE_BOOT_COMPLETED,
//			Manifest.permission.ACCESS_FINE_LOCATION,
//			Manifest.permission.MODIFY_AUDIO_SETTINGS,
//			Manifest.permission.RECORD_AUDIO,
//			Manifest.permission.WAKE_LOCK,
//			Manifest.permission.SYSTEM_ALERT_WINDOW,
//
			};

	private void startRequestPermission(){
		/**
		 * 第 1 步: 检查是否有相应的权限
		 */
		boolean isAllGranted = checkPermissionAllGranted(
				permissions
		);
		// 如果这3个权限全都拥有, 则直接执行备份代码
		if (isAllGranted) {
//            doBackup();
			return;
		}

		/**
		 * 第 2 步: 请求权限
		 */
		// 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
		ActivityCompat.requestPermissions(
				this,
				permissions,
				MY_PERMISSION_REQUEST_CODE
		);
	}

	/**
	 * 检查是否拥有指定的所有权限
	 */
	private boolean checkPermissionAllGranted(String[] permissions) {
		for (String permission : permissions) {
			if (ContextCompat.checkSelfPermission(MainApplication.getInstance(), permission) != PackageManager.PERMISSION_GRANTED) {
				// 只要有一个权限没有被授予, 则直接返回 false
				return false;
			}
		}
		return true;
	}

	/**
	 * 第 3 步: 申请权限结果返回处理
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == MY_PERMISSION_REQUEST_CODE) {
			boolean isAllGranted = false;

			// 判断是否所有的权限都已经授予了
			for (int grant : grantResults) {
				if (grant != PackageManager.PERMISSION_GRANTED) {
					isAllGranted = false;
					break;
				}
			}

			if (isAllGranted) {
				// 如果所有的权限都授予了, 则执行备份代码
				doBackup();

			} else {
				// 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
				openAppDetails();
			}
		}
	}

	/**
	 * 第 4 步: 备份通讯录操作
	 */
	private void doBackup() {
		// 本文主旨是讲解如果动态申请权限, 具体备份代码不再展示, 就假装备份一下
		Toast.makeText(this, "所有的权限都授予成功", Toast.LENGTH_SHORT).show();
	}


	/**
	 * 打开 APP 的详情设置
	 */
	private void openAppDetails() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("需要访问 “外部存储器”，请到 “应用信息 -> 权限” 中授予！");
		builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.setData(Uri.parse("package:" + getPackageName()));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				startActivity(intent);
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	// 提示用户去应用设置界面手动开启权限

	private AlertDialog dialog;

	private void showDialogTipUserGoToAppSettting() {

			dialog = new AlertDialog.Builder(this)
				.setTitle("存储权限不可用")
				.setMessage("请在-应用设置-权限-中，允许支付宝使用存储权限来保存用户数据")
				.setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 跳转到应用设置界面
						goToAppSetting();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).setCancelable(false).show();
	}

	// 跳转到当前应用的设置界面
	private void goToAppSetting() {
		Intent intent = new Intent();

		intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", getPackageName(), null);
		intent.setData(uri);

		startActivityForResult(intent, 123);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 123) {

			if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				// 检查该权限是否已经获取
				int i = ContextCompat.checkSelfPermission(this, permissions[0]);
				// 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
				if (i != PackageManager.PERMISSION_GRANTED) {
					// 提示用户应该去应用设置界面手动开启权限
					showDialogTipUserGoToAppSettting();
				} else {
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}


	// 提示用户该请求权限的弹出框

	private void showDialogTipUserRequestPermission() {

		new AlertDialog.Builder(this)
				.setTitle("存储权限不可用")
				.setMessage("由于需要获取存储空间，为你存储个人信息；\n否则，您将无法正常使用")
				.setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startRequestPermission();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).setCancelable(false).show();
	}


}
