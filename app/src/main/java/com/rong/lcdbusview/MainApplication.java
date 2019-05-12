package com.rong.lcdbusview;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.rong.lcdbusview.datas.ActionType;
import com.rong.lcdbusview.datas.PlayFile;
import com.rong.lcdbusview.html.StationData;
import com.rong.lcdbusview.html.WebHtmlJsText;
import com.rong.lcdbusview.service.RouteServer;
import com.rong.lcdbusview.tools.CrashHandler;
import com.rong.lcdbusview.tools.FileUtil;
import com.rong.lcdbusview.tools.FileUtils;
import com.rong.lcdbusview.tools.LogTools;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract.Constants;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class MainApplication extends Application {
	private static final String TAG = "MainApplication";
	private static final String usb1Path = "mnt/usbhost1/webView";
	private static final String usb1Path_2 = "mnt/usbhost1/8_4/webView";
	private static final String usb2Path = "mnt/usbhost2/webView";
	private static final String usb2Path_2 = "mnt/usbhost2/8_4/webView";
	private static final String usb3Path = "mnt/usbhost3/webView";
	private static final String usb3Path_2 = "mnt/usbhost3/8_4/webView";
	private static final String usb4Path = "mnt/usbhost4/webView";
	private static final String usb4Path_2 = "mnt/usbhost4/8_4/webView";
	public static final String sdcard = "mnt/sdcard/webView";
	private static final String configuration = "mnt/sdcard/webView/configuration.json";
	public static final String route = sdcard + "/data";
	public static final String videoFile = "mnt/sdcard/Video";
	private static final String usb1Path_V = "mnt/usbhost1/Video";
	private static final String usb1Path_V2 = "mnt/usbhost1/8_4/Video";
	private static final String usb2Path_v = "mnt/usbhost2/Video";
	private static final String usb2Path_v2 = "mnt/usbhost2/8_4/Video";
	private static final String usb3Path_v = "mnt/usbhost3/Video";
	private static final String usb3Path_v2 = "mnt/usbhost3/8_4/Video";
	private static final String usb4Path_v = "mnt/usbhost4/Video";
	private static final String usb4Path_v2 = "mnt/usbhost4/8_4/Video";
	//
	private static volatile MainApplication instance;
	public static int width = 1920;
	public static int height = 540;
	public static int buadrate = 19200;
	public static int ADProportion = 30;
	public static boolean openView = true;
	public static String times = "10";
	public static String interval = "10";
	public static String stationTypeface = "30";
	public static String arriveTypeface = "150";
	public String line = "7";

	public String startTime = "6:50";
	public String endTime = "9:30";
	public List<StationData> s_route = null;
	public List<StationData> x_route = null;
	public String routeName;
	public List<PlayFile> playFiles = null;

	private volatile boolean isfinishView = false;
	private volatile boolean isfinishViode = false;
	public volatile boolean isFinish = false;
	private Timer mTimer;
	private SharedPreferences mSharedPreferences;
	private static final String Sp_route = "route";
	private static final String Sp_routeName = "routeName";
	private static final String Sp_routeDirection = "routeDirection";
	private static final String Sp_routeStation = "routeStation";

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x01:
				colseDialog();
				if (isfinishView && isfinishViode) {
					openDialog(1, 0);
					isfinishView = false;
					isfinishViode = false;
					isFinish = true;
					Toast.makeText(getApplicationContext(), "数据更新完成！！", Toast.LENGTH_LONG).show();
					if (mTimer == null) {
						mTimer = new Timer();
					}
					mTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							mHandler.sendEmptyMessage(0x03);
						}
					}, 15 * 1000);
				}
				sendBroadcast(new Intent(ActionType.ACTION_ROUTE));
				break;
			case 0x02:
				colseDialog();
				openDialog(0, 0);
				break;
			case 0x03:
				if (mTimer != null) {
					mTimer.cancel();
					mTimer = null;
				}
				colseDialog();
				break;
			case 0x04:
				colseDialog();
				openDialog(2, 1);
				if (mTimer == null) {
					mTimer = new Timer();
				}
				mTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						mHandler.sendEmptyMessage(0x05);
					}
				}, 15 * 1000);
				break;
			case 0x05:
				if (mTimer != null) {
					mTimer.cancel();
					mTimer = null;
				}
				colseDialog();
				if (!isFinish) {
					mHandler.sendEmptyMessage(0x02);
					copyFile();
				}
				break;
			}
		};
	};

	public static MainApplication getInstance() {

		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		mSharedPreferences = getSharedPreferences(Sp_route, Context.MODE_PRIVATE);
		String processName = getProcessName(this, android.os.Process.myPid());
		Log.e(TAG, processName);
		if (processName != null) {
			boolean defaultProcess = processName.equals("com.rong.lcdbusview");
			if (defaultProcess) {
				CrashHandler handler = CrashHandler.getInstance();
				handler.init(getApplicationContext());
				init();
			} else if (processName.contains(":webbrowser")) {
			} else if (processName.contains(":wallet")) {

			}
		}

	}

	private synchronized void copyFile() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String paString = getPath();
				if (!TextUtils.isEmpty(paString)) {
					isfinishView = true;
					LogTools.d(TAG, "path:" + paString);
					File file_sd = new File(sdcard);
					if (!file_sd.exists()) {
						file_sd.mkdirs();
					} else {
						FileUtils.deleteFile(sdcard);
						file_sd.mkdirs();
					}
					FileUtil.copyFolder(paString, sdcard);
				} else {
					LogTools.d(TAG, "u盘中没有该文件");
				}
				String paString1 = getPathV();
				if (!TextUtils.isEmpty(paString1)) {
					if (!isfinishView) {
						isfinishView = true;
					}
					LogTools.d(TAG, "path:" + paString1);
					File file_sd = new File(videoFile);
					if (!file_sd.exists()) {
						file_sd.mkdirs();
					} else {
						FileUtils.deleteFile(videoFile);
						file_sd.mkdirs();
					}
					FileUtil.copyFolder(paString1, videoFile);

				} else {
					LogTools.d(TAG, "u盘中没有该文件");
				}
				getVideoFile();
				init();
				initRoute();
				// initHtml();
				getTestHtml();
				isfinishViode = true;
				mHandler.sendEmptyMessage(0x01);
			}
		}).start();
	}

	private void init() {
		File file = new File(configuration);
		if (file.exists()) {
			String json = FileUtil.readString(configuration, "GBK");
			try {
				JSONObject jsonObject = new JSONObject(json);
				JSONObject jsonObject2 = jsonObject.getJSONObject("show");
				width = jsonObject2.getInt("width");
				height = jsonObject2.getInt("height");
				buadrate = jsonObject.getInt("buadrate");
				openView = jsonObject.getBoolean("openView");
				// ADProportion = jsonObject.getInt("AD");
				LogTools.e(TAG,
						"width:" + width + ",height:" + height + ",buadrate:" + buadrate + ",openView:" + openView);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!TextUtils.isEmpty(getPath()) || !TextUtils.isEmpty(getPathV())) {
			if (!isFinish) {
				mHandler.sendEmptyMessage(0x04);
			}
		} else {
			if (!isFinish) {
				isFinish = true;
				getVideoFile();
				init();
				initRoute();
				// initHtml();
				getTestHtml();
				sendBroadcast(new Intent(ActionType.ACTION_ROUTE));
			}
		}
	}

	// private void initHtml() {
	// FileUtil.writeString(sdcard + "/1.html", RouteHtml.onceHtml(), "GBK");
	// FileUtil.writeString(sdcard + "/2.html", RouteHtml.twoHtml(), "GBK");
	// FileUtil.writeString(sdcard + "/text.html", RouteHtml.textHtml(), "GBK");
	// FileUtil.writeString(sdcard + "/disweb.html", RouteHtml.diswebHtml(),
	// "GBK");
	// }

	public String getPath() {
		String path = null;
		File file1 = new File(usb1Path);
		File file2 = new File(usb1Path_2);
		File file3 = new File(usb2Path);
		File file4 = new File(usb2Path_2);
		File file5 = new File(usb3Path);
		File file6 = new File(usb3Path_2);
		File file7 = new File(usb4Path);
		File file8 = new File(usb4Path_2);
		List<File> files = new ArrayList<File>();
		files.add(file1);
		files.add(file2);
		files.add(file3);
		files.add(file4);
		files.add(file5);
		files.add(file6);
		files.add(file7);
		files.add(file8);
		for (File file : files) {
			if (file.exists()) {
				path = file.getAbsolutePath();
				break;
			}
		}
		return path;
	}

	public String getPathV() {
		String path = null;
		File file1 = new File(usb1Path_V);
		File file2 = new File(usb1Path_V2);
		File file3 = new File(usb2Path_v);
		File file4 = new File(usb2Path_v2);
		File file5 = new File(usb3Path_v);
		File file6 = new File(usb3Path_v2);
		File file7 = new File(usb4Path_v);
		File file8 = new File(usb4Path_v2);
		List<File> files = new ArrayList<File>();
		files.add(file1);
		files.add(file2);
		files.add(file3);
		files.add(file4);
		files.add(file5);
		files.add(file6);
		files.add(file7);
		files.add(file8);
		for (File file : files) {
			if (file.exists()) {
				path = file.getAbsolutePath();
				break;
			}
		}
		return path;
	}

	public void initRoute() {
		File file = new File(route + "/line.csv");
		if (file.exists()) {
			String s = FileUtil.readString(route + "/line.csv", "GBK");
			LogTools.d(TAG, s);
			String[] src = s.split("\\n");
			if (src != null && src.length >= 2) {
				for (String sw : src) {
					routeName = getStation(sw);
					if (!TextUtils.isEmpty(routeName)) {
						LogTools.d(TAG, routeName);
						File file_route = new File(route + "/" + routeName);
						if (file_route.exists()) {
							File[] files = file_route.listFiles();
							if (files != null && files.length > 0) {
								for (File file2 : files) {
									if ((file2.getName()).contains("S")) {
										LogTools.d(TAG, "上行");
										String route_stations = FileUtil.readString(file2.getAbsolutePath(), "GBK");
										// LogTools.d(TAG, route_stations);
										String[] stations = route_stations.split("\\n");
										if (stations != null && stations.length >= 2) {
											s_route = new ArrayList<>();
											for (int i = 1; i < stations.length; i++) {
												String[] sr2 = stations[i].split(",");
												if (sr2 != null && sr2.length >= 4) {
													StationData station = new StationData();
													station.setStationNum(i);
													station.setName(sr2[1]);
													station.setDeputy(sr2[2]);
													station.setActive(false);
													station.setHasError(false);
													s_route.add(station);
												}
											}
										}
										LogTools.d(TAG, s_route.toString());
									} else if ((file2.getName()).contains("X")) {
										LogTools.d(TAG, "下行");
										String route_stations = FileUtil.readString(file2.getAbsolutePath(), "GBK");
										// LogTools.d(TAG, route_stations);
										String[] stations = route_stations.split("\\n");
										if (stations != null && stations.length >= 2) {
											x_route = new ArrayList<>();
											for (int i = 1; i < stations.length; i++) {
												String[] sr2 = stations[i].split(",");
												if (sr2 != null && sr2.length >= 4) {
													StationData station = new StationData();
													station.setStationNum(i);
													station.setName(sr2[1]);
													station.setDeputy(sr2[2]);
													station.setActive(false);
													station.setHasError(false);
													x_route.add(station);
												}
											}
										}
										LogTools.d(TAG, x_route.toString());
									}

								}
							}
						}
						break;
					}
				}
			}
		}
	}

	private String getStation(String s) {
		String station = null;
		if (!TextUtils.isEmpty(s)) {
			String[] sr = s.split(",");
			if (sr != null && sr.length >= 8) {
				String routename = getRouteName();
				if (routename == null || TextUtils.isEmpty(routename)) {
					if (sr[2].contains("是")) {
						station = sr[1];
						setRouteName(station);
						startTime = sr[3];
						endTime = sr[4];
						times = sr[5];
						interval = sr[6];
						// stationTypeface = sr[6];
						// arriveTypeface = sr[7];
					}
				}else{
					if(sr[1].equals(routename)){
						station = sr[1];
						startTime = sr[3];
						endTime = sr[4];
						times = sr[5];
						interval = sr[6];
					}
				}
			}
		}
		return station;
	}

	/**
	 * 获取线路方向
	 * 
	 * @return 0：上行 1：下行
	 */
	public int getRouteDirection() {
		int diretion = mSharedPreferences.getInt(Sp_routeDirection, -1);
		if (diretion == -1) {
			mSharedPreferences.edit().putInt(Sp_routeDirection, 0).commit();
			diretion = 0;
		}
		return diretion;
	}

	/**
	 * 设置线路方向
	 * 
	 * @param diretion
	 *            0：上行 1：下行
	 */
	public void setRouteDirection(int diretion) {
		mSharedPreferences.edit().putInt(Sp_routeDirection, diretion).commit();
	}


//	/**
//	 * 获取线路当前站点序号
//	 * 
//	 * @return 当前站点序号
//	 */
//	public int getRouteStation() {
//		int diretion = mSharedPreferences.getInt(Sp_routeStation, -1);
//		if (diretion == -1) {
//			mSharedPreferences.edit().putInt(Sp_routeStation, 0).commit();
//			diretion = 0;
//		}
//		return diretion;
//	}
//
//	/**
//	 * 设置线路方向
//	 * 
//	 * @param 当前站点序号
//	 */
//	public void setRouteStation(int station) {
//		mSharedPreferences.edit().putInt(Sp_routeStation, station).commit();
//	}

	
	/**
	 * 获取线路名称
	 * 
	 * @return
	 */
	public String getRouteName() {
		String name = mSharedPreferences.getString(Sp_routeName, null);
		return name;
	}

	/**
	 * 设置线路名称
	 */
	public void setRouteName(String routeName) {
		mSharedPreferences.edit().putString(Sp_routeName, routeName).commit();
	}

	private void getTestHtml() {
		int direction = getRouteDirection();
		if(direction == 0){
			if (s_route != null && !s_route.isEmpty()) {
				StringBuilder buffer_top = new StringBuilder();
				StringBuilder buffer_buttom = new StringBuilder();
				int size = s_route.size(),withtype = 0;
				if(s_route.size() <= 30)
				{
					withtype = 0;
					for (int i = 0; i < size; i++) {
						if (i == 0) {
							StationData stationData = s_route.get(i);
							stationData.setActive(true);
							buffer_top.append(WebHtmlJsText.getTotalTop(stationData, 1));
						} else {
							if (15 > i) {
								buffer_top.append(WebHtmlJsText.getTotalTop(s_route.get(i), 2));
							} else {
								buffer_buttom.append(WebHtmlJsText.getTotalButtom(s_route.get(i), 2));
							}
						}
					}
				}
				else if(s_route.size() <= 40)
				{
					withtype = 1;
					for (int i = 0; i < size; i++) {
						if (i == 0) {
							StationData stationData = s_route.get(i);
							stationData.setActive(true);
							buffer_top.append(WebHtmlJsText.getTotalTop(stationData, 1));
						} else {
							if (20 > i) {
								buffer_top.append(WebHtmlJsText.getTotalTop(s_route.get(i), 2));
							} else {
								buffer_buttom.append(WebHtmlJsText.getTotalButtom(s_route.get(i), 2));
							}
						}
					}
				}
				else
				{
					withtype = 2;
					for (int i = 0; i < size; i++) {
						if (i == 0) {
							StationData stationData = s_route.get(i);
							stationData.setActive(true);
							buffer_top.append(WebHtmlJsText.getTotalTop(stationData, 1));
						} else {
							if (32 > i) {
								buffer_top.append(WebHtmlJsText.getTotalTop(s_route.get(i), 2));
							} else {
								buffer_buttom.append(WebHtmlJsText.getTotalButtom(s_route.get(i), 2));
							}
						}
					}
				}


				String html = WebHtmlJsText.getRouteHtml(routeName, startTime, endTime, buffer_top.toString(),
						buffer_buttom.toString(), times,withtype);
				// Log.e(TAG, html);
				FileUtil.writeString(RouteServer.path + "/index.html", html, "UTF-8");
				String html1 = WebHtmlJsText.getPageHtml(s_route.get(0), s_route.get(0), s_route.get(1),
						s_route.get(s_route.size() - 1));
				LogTools.d(TAG, html1);
				FileUtil.writeString(RouteServer.path + "/page.html", html1, "UTF-8");
			}
		}else{
			if (x_route != null && !x_route.isEmpty()) {
				StringBuilder buffer_top = new StringBuilder();
				StringBuilder buffer_buttom = new StringBuilder();
				int size = s_route.size(),withtype = 0;
				if(s_route.size() <= 30)
				{
					withtype = 0;
					for (int i = 0; i < size; i++) {
						if (i == 0) {
							StationData stationData = s_route.get(i);
							stationData.setActive(true);
							buffer_top.append(WebHtmlJsText.getTotalTop(stationData, 1));
						} else {
							if (15 > i) {
								buffer_top.append(WebHtmlJsText.getTotalTop(s_route.get(i), 2));
							} else {
								buffer_buttom.append(WebHtmlJsText.getTotalButtom(s_route.get(i), 2));
							}
						}
					}
				}
				else if(s_route.size() <= 40)
				{
					withtype = 1;
					for (int i = 0; i < size; i++) {
						if (i == 0) {
							StationData stationData = s_route.get(i);
							stationData.setActive(true);
							buffer_top.append(WebHtmlJsText.getTotalTop(stationData, 1));
						} else {
							if (20 > i) {
								buffer_top.append(WebHtmlJsText.getTotalTop(s_route.get(i), 2));
							} else {
								buffer_buttom.append(WebHtmlJsText.getTotalButtom(s_route.get(i), 2));
							}
						}
					}
				}
				else
				{
					withtype = 2;
					for (int i = 0; i < size; i++) {
						if (i == 0) {
							StationData stationData = s_route.get(i);
							stationData.setActive(true);
							buffer_top.append(WebHtmlJsText.getTotalTop(stationData, 1));
						} else {
							if (32 > i) {
								buffer_top.append(WebHtmlJsText.getTotalTop(s_route.get(i), 2));
							} else {
								buffer_buttom.append(WebHtmlJsText.getTotalButtom(s_route.get(i), 2));
							}
						}
					}
				}

				String html = WebHtmlJsText.getRouteHtml(routeName, startTime, endTime, buffer_top.toString(),
						buffer_buttom.toString(), times,withtype);
				// Log.e(TAG, html);
				FileUtil.writeString(RouteServer.path + "/index.html", html, "UTF-8");
				String html1 = WebHtmlJsText.getPageHtml(x_route.get(0), x_route.get(0), x_route.get(1),
						x_route.get(x_route.size() - 1));
				LogTools.d(TAG, html1);
				FileUtil.writeString(RouteServer.path + "/page.html", html1, "UTF-8");
			}
		}
	}

	private void getVideoFile() {
		File file = new File(videoFile);
		if (file.exists()) {
			LogTools.d(TAG, "存在视频");
			File[] files = file.listFiles();
			File list = new File(videoFile + "/video.csv");
			if (list.exists()) {
				playFiles = new ArrayList<>();
				String route_stations = FileUtil.readString(list.getAbsolutePath(), "GBK");
				LogTools.d(TAG, route_stations);
				String[] stations = route_stations.split("\\n");
				if (stations != null && stations.length >= 2) {
					for (int i = 1; i < stations.length; i++) {
						String[] sr2 = stations[i].split(",");
						if (sr2 != null && sr2.length >= 4) {
							for (File file2 : files) {
								if (file2.getName().equals(sr2[1])) {
									PlayFile playFile = new PlayFile();
									playFile.setName(sr2[1]);
									playFile.setPath(file2.getAbsolutePath());
									;
									playFile.setStopTime(Integer.parseInt(sr2[2].trim()));
									playFile.setTimes(Integer.parseInt(sr2[3].trim()));
									playFiles.add(playFile);
								}
							}
						}
					}
				}
			}
		}
	}

	private Builder builder = null;
	private AlertDialog dialog = null;

	private void openDialog(int type, int state) {
		// if(!isFinish){
		if (builder == null) {
			builder = new Builder(getApplicationContext());
			// }
			builder.setCancelable(true); // 设置按钮是否可以按返回键取消,false则不可以取消
			// ImageView iv = new ImageView(getApplicationContext());
			if (type == 1) {
				builder.setMessage("数据拷贝完成，请拔出U盘，重启系统！");
			} else if (type == 0) {
				builder.setMessage("资源文件导入中，请稍后。。。");
			} else {
				builder.setMessage("检测到U盘有资源文件，是否导入数据");
			}

			if (state == 1) {
				builder.setPositiveButton("是", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mTimer != null) {
							mTimer.cancel();
							mTimer = null;
						}
						dialog.cancel();
						colseDialog();
						mHandler.sendEmptyMessage(0x02);
						copyFile();
					}
				});
				builder.setNegativeButton("否", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mTimer != null) {
							mTimer.cancel();
							mTimer = null;
						}
						dialog.cancel();
						isFinish = true;
						getVideoFile();
						// init();
						initRoute();
						// initHtml();
						getTestHtml();
						sendBroadcast(new Intent(ActionType.ACTION_ROUTE));
					}
				});
			} else {
				builder.setPositiveButton(null, null);
				builder.setNegativeButton(null, null);
			}
			// builder.setView(iv);
			// if(dialog != null){
			dialog = builder.create();
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			// }
			dialog.show();
		}

	}

	private void colseDialog() {
		if (dialog != null && builder != null) {
			dialog.cancel();
			dialog = null;
			builder = null;
		}
	}

	public static String getProcessName(Context cxt, int pid) {
		ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
		if (runningApps == null) {
			return null;
		}
		for (RunningAppProcessInfo procInfo : runningApps) {
			if (procInfo.pid == pid) {
				return procInfo.processName;
			}
		}
		return null;
	}
}
