package com.rong.lcdbusview.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rong.lcdbusview.MainApplication;
import com.rong.lcdbusview.analysis.Transmission;
import com.rong.lcdbusview.datas.ActionType;
import com.rong.lcdbusview.datas.AdvertisementData;
import com.rong.lcdbusview.datas.Feedback;
import com.rong.lcdbusview.datas.InOutStationReceive;
import com.rong.lcdbusview.datas.MessageType;
import com.rong.lcdbusview.datas.RouteInformation;
import com.rong.lcdbusview.datas.RouteMsg;
import com.rong.lcdbusview.datas.TextShow;
import com.rong.lcdbusview.datas.TimingReceive;
import com.rong.lcdbusview.html.StationData;
//import com.rong.lcdbusview.html.RealTimeHtml;
//import com.rong.lcdbusview.html.RouteHtml;
//import com.rong.lcdbusview.html.StationData;
import com.rong.lcdbusview.html.WebHtmlJsText;
import com.rong.lcdbusview.link.AnalysisDatasManage;
import com.rong.lcdbusview.link.NatifyStationMsg;
import com.rong.lcdbusview.link.RouteStationMsg;
import com.rong.lcdbusview.link.StationMsg;
import com.rong.lcdbusview.link.TransmissionCallback;
import com.rong.lcdbusview.tools.FileUtil;
import com.rong.lcdbusview.tools.LogTools;
import com.rong.lcdbusview.tools.StringUtils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import static com.rong.lcdbusview.datas.ActionType.ACTION_AD_CHANGE;

public class MainService extends Service implements Transmission, TransmissionCallback, ADDownloadManager.ADDownloadCallBack {

	private static final String TAG = "MainService";
	// private AnalysisDatasCom com;
	private AnalysisDatasManage datasManage;
	private static Thread serverStartThread;
	// private RouteServer http;
	private RouteInformation route;
	private TimingReceive timing;
	private RouteMsg mRouteMsg;
	private List<StationData> mstations;
	private List<String> upStations;
	private List<String> downStations;
	private String routename;
	private int lastdir;
	private MainService install;
	private MyBinder binder = new MyBinder();
	private ADDownloadManager adDownloadManager;
	private List<AdvertisementData> mAdvertisementDataList;

    public List<AdvertisementData> getmAdvertisementDataList() {
        return mAdvertisementDataList;
    }

    @Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

    @Override
    public void onChangeADCompleted(List<AdvertisementData> advertisementDataQueue) {
        LogTools.d(TAG,"onChangeADCompleted " + advertisementDataQueue.toString());
        if(advertisementDataQueue != null && !advertisementDataQueue.isEmpty()){
            mAdvertisementDataList.clear();
            final int size = advertisementDataQueue.size();
            int addSize = 0;
//            while (size == addSize){
                for(int i = 0 ; i < size; i ++){
                    AdvertisementData advertisementData = advertisementDataQueue.get(i);
//                    if((advertisementData.getOrderNo() - 1) == addSize
//                            && !mAdvertisementDataList.contains(advertisementData)){
                        mAdvertisementDataList.add(advertisementData);
//                        addSize ++;
//                    }
                }
//            }
            if(!mAdvertisementDataList.isEmpty()){
                sendBroadcast(new Intent(ACTION_AD_CHANGE));
            }
        }
    }


    public final class MyBinder extends Binder {
		public MainService getService(){
			return install;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		install = this;
		mAdvertisementDataList = new ArrayList<>();
		init();
		adDownloadManager = new ADDownloadManager();
		adDownloadManager.setADDownloadCallBack(this);
		try {
			adDownloadManager.login();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
            e.printStackTrace();
        }

    }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// num = MainApplication.getInstance().getRouteStation();
		return super.onStartCommand(intent, flags, startId);
	}

	private boolean isOpenFirst = false;
	
	private void init() {
		datasManage = AnalysisDatasManage.getInstance(this);
		// ttyS3
//		datasManage.open("/dev/ttyS3", MainApplication.buadrate, 8, (char) 0, 1, 0, 1024);
//		datasManage.startRead();
		/*
		int direction = MainApplication.getInstance().getRouteDirection();
		if (direction == 0) {
			List<StationData> s_route = MainApplication.getInstance().s_route;
			if (s_route != null && !s_route.isEmpty()) {
				mstations = s_route;
			}
		} else {
			List<StationData> x_route = MainApplication.getInstance().x_route;
			if (x_route != null && !x_route.isEmpty()) {
				mstations = x_route;
			}
		}
		*/
		lastdir = 0;
		currentRoute = readRouteJson(0);
		routename = readRoutenameJson();
		if(routename != null)
		{
			LogTools.e(TAG, routename);
			LogTools.e(TAG, currentRoute.toString());
		}
		if(currentRoute != null){
//			changeRoute(currentRoute, 0);
//			changeStation(currentRoute,0);
			currentStation = 0;
			isOpenFirst = true;
		}
	}

	private void sendMsg(byte[] datas, int type) {
		// datasManage.sendMessage(datas, type);
	}

	@Override
	public void appearTimingMsg(TimingReceive timingReceive) {
		if (timingReceive != null) {
			this.timing = timingReceive;
			if (route == null) {
				Feedback feedback = new Feedback(1, new byte[3]);
				sendMsg(feedback.getData(), MessageType.TimingBackMsg);
			} else {
				Feedback feedback = new Feedback(0, new byte[3]);
				sendMsg(feedback.getData(), MessageType.TimingBackMsg);
			}
		} else {
			Feedback feedback = new Feedback(0, new byte[3]);
			sendMsg(feedback.getData(), MessageType.TimingBackMsg);
		}
//		sendBroadcast(new Intent(ActionType.ACTION_RELOAD_IN));
	}

	@Override
	public void appearRouteMsg(RouteInformation route) {
		if (route != null && MainApplication.getInstance().isFinish) {
			Feedback feedback = new Feedback(1, new byte[3]);

			sendMsg(feedback.getData(), MessageType.RouteBackMsg);
			this.route = route;
			if (timing.getDirection() == 0) {
				upStations = route.getUpStations();
				if (upStations != null && !upStations.isEmpty()) {
					RouteMsg routeMsg = new RouteMsg();
					routeMsg.setRouteNamber(route.getRouteName());
					routeMsg.setStartStation(upStations.get(0));
					routeMsg.setEndStation(upStations.get(upStations.size() - 1));
				}
			} else if (timing.getDirection() == 1) {
				downStations = route.getDownStations();
				if (downStations != null && !downStations.isEmpty()) {
					RouteMsg routeMsg = new RouteMsg();
					routeMsg.setRouteNamber(route.getRouteName());
					routeMsg.setStartStation(downStations.get(0));
					routeMsg.setEndStation(downStations.get(downStations.size() - 1));
				}
			}
		} else {
			Feedback feedback = new Feedback(0, new byte[3]);
			sendMsg(feedback.getData(), MessageType.RouteBackMsg);
		}
//		sendBroadcast(new Intent(ActionType.ACTION_RELOAD_IN));
	}

	private InOutStationReceive station;

	@Override
	public void appearInOutStation(InOutStationReceive station) {
		if (station != null) {
			Feedback feedback = new Feedback(1, new byte[3]);
			sendMsg(feedback.getData(), MessageType.InOutStationBack);
		} else {
			Feedback feedback = new Feedback(0, new byte[3]);
			sendMsg(feedback.getData(), MessageType.InOutStationBack);
		}
		this.station = station;
//		sendBroadcast(new Intent(ActionType.ACTION_RELOAD_IN));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		datasManage.close();
		// http.stop();
	}

	private volatile String content = null;
	// private volatile int num = -1;
	private volatile StationData cStationData;
	private List<String> top = new LinkedList<>();
	private List<String> buttom = new LinkedList<>();
	private volatile boolean isSelect = false;

	@Override
	public void appearText(final TextShow textShow) {
		content = textShow.getContent();
		new Thread(new Runnable() {

			@Override
			public void run() {
				//changeStation(textShow.getContent());
			}
		}).start();
	}

	/**
	 * 站点到站判断
	 * 
	 * @param stationData
	 *            站点信息
	 * @param content
	 *            到站信息
	 * @return
	 */

	private boolean judgmentStation(StationData stationData, String content) {
		if (TextUtils.isEmpty(stationData.getDeputy())) {
			if ((content.trim()).contains(stationData.getName().trim())) {
				return true;
			}
		} else {
			if ((content.trim()).contains(stationData.getName().trim())
					&& (content.trim()).contains(stationData.getDeputy().trim()) && (content.trim()).contains("（")) {
				return true;
			} else {
				if ((content.trim()).contains(stationData.getName().trim())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void appearRoute(final RouteMsg routeMsg) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				//changeRoute(routeMsg);
			}
		}).start();
	}


	@Override
	public void onNatifyStationInOut(NatifyStationMsg natifyStationMsg) {
		if(natifyStationMsg != null){
			if(natifyStationMsg.getStationSumNum() >= currentRoute.size()){
//				changeRoute(currentRoute, natifyStationMsg.getCurrentStationNum());
//				changeStation(currentRoute,natifyStationMsg.getCurrentStationNum());
				currentStation = natifyStationMsg.getCurrentStationNum();
				if(natifyStationMsg.getInoutState() == 0) {
                    sendBroadcast(new Intent(ActionType.ACTION_RELOAD_IN));
                }else {
                    sendBroadcast(new Intent(ActionType.ACTION_RELOAD_OUT));
                }
			}else{
				Log.e(TAG, "站点信息有误！或者方向不对！");
			}
		}
	}
	@Override
	public void onNatifyRoutename(String name) {
		if(currentRoute != null && name != null){
				routename = name;
				saveRoutename(name);
				Log.e("onNatifyRoutename", name);
//				changeRoute(currentRoute, 0);
//				changeStation(currentRoute,0);
				sendBroadcast(new Intent(ActionType.ACTION_ROUTE));
		}
	}
	private int stationAllNum = 0 ;
	
	@Override
	public void onStationMsg(RouteStationMsg stationMsg,int dir) {
		if(stationMsg != null){
			LogTools.d(TAG, stationMsg.toString());
			stationAllNum = stationMsg.getStationAll();
			if(currentRoute != null || isOpenFirst == true){
				if(stationMsg.getStations() != null && !stationMsg.getStations().isEmpty()){
					currentRoute.clear();
					for(StationMsg msg : stationMsg.getStations()){
						currentRoute.add(msg);
					}
					sortingStation(currentRoute);
					saveRoute(currentRoute,dir);
					lastdir = dir;
					//	changeRoute(currentRoute, 0);
				//	changeStation(currentRoute,0);
					sendBroadcast(new Intent(ActionType.ACTION_ROUTE));
				}
			} else {
				LogTools.e("onStationMsg", "currentRoute NULL build file");
				currentRoute = new ArrayList<>();
				for(StationMsg msg : stationMsg.getStations()){
					currentRoute.add(msg);
				}
				if(stationMsg.getStations() != null && !stationMsg.getStations().isEmpty()){
					currentRoute.clear();
					for(StationMsg msg : stationMsg.getStations()){
						currentRoute.add(msg);
					}
					sortingStation(currentRoute);
					saveRoute(currentRoute, dir);
					lastdir = dir;
					//	changeRoute(currentRoute, 0);
				//	changeStation(currentRoute,0);
					sendBroadcast(new Intent(ActionType.ACTION_ROUTE));
				}
			}
		}
	}

	@Override
	public void onNatifyDirstatus( int dir,int status,int cur){
		//有更新过上下行数据
		Log.e(TAG, "onNatifyDirstatus");
		if (lastdir != dir) {
			lastdir = dir;
			currentRoute = readRouteJson(lastdir);
			int nowcur = 0;
			if (dir == 0) {
				if (status == 0) {
					nowcur = cur;
				} else {
					nowcur = cur;
				}
			} else {
				if (status == 0) {
					nowcur = currentRoute.size() - cur - 1;
				} else {
					nowcur = currentRoute.size() - cur - 1;
				}
			}
			//changeRoute(currentRoute, nowcur);
//			if(status == 0)
//			{
//				changeStation(currentRoute,nowcur);
//			}
//			else
//			{
//				changeStation(currentRoute,nowcur - 1);
//			}
			currentStation = nowcur;
//			sendBroadcast(new Intent(ActionType.ACTION_RELOAD_IN));
			Log.e(TAG, "读取站点:" + lastdir);
			Log.e(TAG, currentRoute.toString());
		} else {
			int nowcur = 0;
			if (dir == 0) {
				if (status == 0) {
					nowcur = cur;
				} else {
					nowcur = cur;
				}
			} else {
				if (status == 0) {
					nowcur = currentRoute.size() - cur - 1;
				} else {
					nowcur = currentRoute.size() - cur - 1;
				}
			}
//			changeRoute(currentRoute, nowcur);
//			if(status == 0)
//			{
//				changeStation(currentRoute,nowcur);
//			}
//			else
//			{
//				changeStation(currentRoute,nowcur - 1);
//			}
			currentStation = nowcur;
//			sendBroadcast(new Intent(ActionType.ACTION_RELOAD_IN));
		}
	}


	private List<StationMsg> currentRoute;
	private int currentStation;

	public int getCurrentStation() {
		return currentStation;
	}

	public List<StationMsg> getCurrentRoute() {
		return currentRoute;
	}

	public String getRoutename() {
		return routename;
	}

	private void sortingStation(List<StationMsg> stations) {
		int size = stations.size();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size - 1; j++) {
				if (stations.get(i).getStationNum() < stations.get(j).getStationNum()) {
					StationMsg station = stations.get(i);
					stations.set(i, stations.get(j));
					stations.set(j, station);
				}
			}
		}
	}



	private static final String stationNum = "stationNum";
	private static final String stationName = "stationName";
	private static final String routeJson = "mnt/sdcard/webView/routeJson.json";
	private static final String routeJsons = "mnt/sdcard/webView/routeJsons.json";
	private static final String routeJsonx = "mnt/sdcard/webView/routeJsonx.json";

	private void saveRoute(List<StationMsg> route,int dir) {
		try {
			if (route != null && !route.isEmpty()) {
				JSONArray array = new JSONArray();
				for (StationMsg station : route) {
					JSONObject object = new JSONObject();
					object.put(stationName, station.getStationName());
					object.put(stationNum, station.getStationNum());
					array.put(object);
				}
				LogTools.d(TAG, "saveRoute"+array.toString());
				String filename;
				if(dir == 0)
				{
					filename = routeJsons;
				}
				else
				{
					filename = routeJsonx;
				}
				File file = new File(filename);
				if (!file.exists()) {
					file.createNewFile();
				} else {
					file.delete();
					file.createNewFile();
				}
				FileUtil.writeString(filename, array.toString(), "GBK");
			}
		} catch (JSONException e) {
			LogTools.e(TAG, "json文本格式有误！！");
			e.printStackTrace();
		} catch (IOException e) {
			LogTools.e(TAG, "文件写入有误！！");
			e.printStackTrace();
		}
	}

	private static final String lineName = "lineName";
	private static final String routenameJson = "mnt/sdcard/webView/routenameJson.json";
	private void saveRoutename(String linename) {
		try {
			if (linename != null) {
				JSONArray array = new JSONArray();
				JSONObject object = new JSONObject();
				object.put(lineName, linename);
				array.put(object);

				LogTools.d(TAG, "saveRoutename"+array.toString());

				File file = new File(routenameJson);
				if (!file.exists()) {
					file.createNewFile();
				} else {
					file.delete();
					file.createNewFile();
				}
				FileUtil.writeString(routenameJson, array.toString(), "GBK");
			}
		} catch (JSONException e) {
			LogTools.e(TAG, "json文本格式有误！！");
			e.printStackTrace();
		} catch (IOException e) {
			LogTools.e(TAG, "文件写入有误！！");
			e.printStackTrace();
		}
	}
	private List<StationMsg> readRouteJson(int dir) {
		List<StationMsg> route = null;
		String filename;
		if(dir == 0)
		{
			filename = routeJsons;
		} else {
			filename = routeJsonx;
		}
		File file = new File(filename);
		if (!file.exists()) {
			return null;
		}
		String routeJsonStr = FileUtil.readString(filename, "GBK");
		try {
			route = new ArrayList<StationMsg>();
			JSONArray array = new JSONArray(routeJsonStr);
			if (array != null) {
				int length = array.length();
				for (int i = 0; i < length; i++) {
					JSONObject jsonObject = array.getJSONObject(i);
					StationMsg station = new StationMsg();
					station.setStationName(jsonObject.getString(stationName));
					station.setStationNum(jsonObject.getInt(stationNum));
					route.add(station);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return route;
	}
	private String readRoutenameJson() {
		File file = new File(routenameJson);
		String rtname = null;
		if (!file.exists()) {
			return null;
		}
		String routeJsonStr = FileUtil.readString(routenameJson, "GBK");
		try {
			JSONArray array = new JSONArray(routeJsonStr);
			if (array != null) {
				JSONObject jsonObject = array.getJSONObject(0);
				rtname = jsonObject.getString(lineName);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return rtname;
	}
}
