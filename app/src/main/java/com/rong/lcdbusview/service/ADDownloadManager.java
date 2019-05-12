package com.rong.lcdbusview.service;

import android.text.TextUtils;

import com.rong.lcdbusview.MainApplication;
import com.rong.lcdbusview.datas.AdvertisementData;
import com.rong.lcdbusview.tools.FileUtil;
import com.rong.lcdbusview.tools.HttpUtils;
import com.rong.lcdbusview.tools.LogTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

import static com.rong.lcdbusview.datas.ActionType.ADID;
import static com.rong.lcdbusview.datas.ActionType.BEGINTIME;
import static com.rong.lcdbusview.datas.ActionType.CHECKVALUE;
import static com.rong.lcdbusview.datas.ActionType.CODE;
import static com.rong.lcdbusview.datas.ActionType.DATA;
import static com.rong.lcdbusview.datas.ActionType.DEVICECODE;
import static com.rong.lcdbusview.datas.ActionType.ENDTIME;
import static com.rong.lcdbusview.datas.ActionType.JSON_APLICATION;
import static com.rong.lcdbusview.datas.ActionType.MESSAGE;
import static com.rong.lcdbusview.datas.ActionType.NAME;
import static com.rong.lcdbusview.datas.ActionType.ORDERNO;
import static com.rong.lcdbusview.datas.ActionType.PASSWORD;
import static com.rong.lcdbusview.datas.ActionType.SIZE;
import static com.rong.lcdbusview.datas.ActionType.SYSID;
import static com.rong.lcdbusview.datas.ActionType.TIMES;
import static com.rong.lcdbusview.datas.ActionType.TOKEN;
import static com.rong.lcdbusview.datas.ActionType.TYPE;
import static com.rong.lcdbusview.datas.ActionType.URL;
import static com.rong.lcdbusview.datas.ActionType.URL_COLON;
import static com.rong.lcdbusview.datas.ActionType.URL_CONFIRMRECEIVE;
import static com.rong.lcdbusview.datas.ActionType.URL_HEAD;
import static com.rong.lcdbusview.datas.ActionType.URL_HEARTBEAT;
import static com.rong.lcdbusview.datas.ActionType.URL_LOGIN;
import static com.rong.lcdbusview.datas.ActionType.URL_QUERYAD;
import static com.rong.lcdbusview.datas.ActionType.URL_QUERYDEVICE;
import static com.rong.lcdbusview.datas.ActionType.USERNAME;
import static com.rong.lcdbusview.datas.ActionType.VERSION;

public class ADDownloadManager {
    private static final String TAG = ADDownloadManager.class.getSimpleName();


    private String mUserName = "bst";
    private String mPassWord = "123";
    private String mSysId = "f86783b869ed488099a73eb227163dd3";
    private String mToken = null;
    private String mDomain = "public.nandasoft-its.com";
    private String mPost = "27006";
    private String mDeviceCode = "5441";
    private String mSession = null;
    private String mVersion = "201903300061";
    private ReentrantLock lock;
    private HashMap<String,AdvertisementData> advertisementDataHashMap;
    private List<AdvertisementData> advertisementDataQueue;
    private ADDownloadCallBack mADDownloadCallBack;
//    private Condition condition;

    public void setADDownloadCallBack(ADDownloadCallBack adDownloadCallBack){
        this.mADDownloadCallBack = adDownloadCallBack;
    }

    public interface ADDownloadCallBack{
        void onChangeADCompleted(List<AdvertisementData> advertisementDataQueue);
    }

    public ADDownloadManager(){
        lock = new ReentrantLock(true);
        advertisementDataHashMap = new HashMap<>();
        advertisementDataQueue = new ArrayList<>();
//        condition = lock.newCondition();
    }

    public void login() throws IOException, JSONException {
        JSONObject json = new JSONObject();
        json.put(USERNAME,mUserName);
        json.put(PASSWORD,mPassWord);
        json.put(SYSID,mSysId);
        final String url =URL_HEAD+mDomain+URL_COLON+mPost+URL_LOGIN;
        HttpUtils.getInstance().postAsync(url, JSON_APLICATION, json.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogTools.e(TAG,"login onFailure >>>>>>>>>>>>>>");
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    final String body =  response.body().string();
                    //获取session的操作，session放在cookie头，且取出后含有“；”，取出后为下面的 s （也就是jsesseionid）
                    Headers headers = response.headers();
                    LogTools.d(TAG, "header " + headers);
                    List<String> cookies = headers.values("Set-Cookie");
                    String session = cookies.get(0);
                    LogTools.d(TAG, "onResponse-size: " + cookies);

                    String s = session.substring(0, session.indexOf(";"));
                    LogTools.i(TAG, "session is  :" + s);
                    LogTools.d(TAG, "login onResponse >>>>>>>>>>>>>>" + body + ",session : " + session);
                    response.close();
                    call.cancel();
                    try {
                        JSONObject json = new JSONObject(body);
                        final int code = json.optInt(CODE);
                        if(code == 200) {
                            JSONObject jsonObject = json.optJSONObject(DATA);
                            final String token = jsonObject.optString(TOKEN);
                            while (true) {
                                if (lock.tryLock()) {
                                    break;
                                } else {
                                    Thread.sleep(50);
                                }
                            }
                            mToken = token;
                            mSession = s;
                            lock.unlock();
                            LogTools.d(TAG, "mToken = " + mToken);
//                            queryDevice();
                           // heartbeat();
                            queryAd();
                            startHeartBeat();
                        }else {
                            final String message = json.optString(MESSAGE);
                            LogTools.e(TAG,"login onFailure >>>>>>>>>>>>>>" + message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void queryDevice() throws IOException, JSONException {
        if(TextUtils.isEmpty(mToken)){
            return;
        }
        if(TextUtils.isEmpty(mSession)){
            return;
        }
        JSONObject json = new JSONObject();
        json.put(DEVICECODE,mDeviceCode);
        json.put(TOKEN,mToken);
        final String url =URL_HEAD+mDomain+URL_COLON+mPost+URL_QUERYDEVICE;
        HttpUtils.getInstance().postAsyncAndHead(url,mSession,JSON_APLICATION, json.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogTools.e(TAG,"queryDevice onFailure >>>>>>>>>>>>>>");
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    final String body = response.body().string();
                    response.close();
                    LogTools.d(TAG, "queryDevice onResponse >>>>>>>>>>>>>>" + body);
                    call.cancel();
                    try {
                        JSONObject json = new JSONObject(body);
                        final int code = json.optInt(CODE);
                        if (code == 200) {
                            JSONObject jsonObject = json.optJSONObject(DATA);

                        } else {
                            final String message = json.optString(MESSAGE);
                            LogTools.e(TAG, "queryDevice onFailure >>>>>>>>>>>>>>" + message);
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void confirmReceive() throws IOException, JSONException {
        if(TextUtils.isEmpty(mToken)){
            return;
        }
        if(TextUtils.isEmpty(mSession)){
            return;
        }
        JSONObject json = new JSONObject();
        json.put(DEVICECODE,mDeviceCode);
        json.put(VERSION,mVersion);
        json.put(TOKEN,mToken);
        final String url =URL_HEAD+mDomain+URL_COLON+mPost+URL_CONFIRMRECEIVE;
        HttpUtils.getInstance().postAsyncAndHead(url,mSession,JSON_APLICATION, json.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogTools.e(TAG,"confirmReceive onFailure >>>>>>>>>>>>>>");
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    final String body = response.body().string();
                    response.close();
                    LogTools.d(TAG, "confirmReceive onResponse >>>>>>>>>>>>>>" + body);
                    call.cancel();
                    try {
                        JSONObject json = new JSONObject(body);
                        final int code = json.optInt(CODE);
                        if (code == 200) {
                            JSONObject jsonObject = json.optJSONObject(DATA);
                        } else {
                            final String message = json.optString(MESSAGE);
                            LogTools.e(TAG, "confirmReceive onFailure >>>>>>>>>>>>>>" + message);
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void queryAd()throws IOException, JSONException {
        if(TextUtils.isEmpty(mToken)){
            return;
        }
        if(TextUtils.isEmpty(mSession)){
            return;
        }
        if(TextUtils.isEmpty(mVersion)){
            return;
        }
        JSONObject json = new JSONObject();
        json.put(DEVICECODE,mDeviceCode);
        json.put(VERSION,mVersion);
        json.put(TOKEN,mToken);

        final String url =URL_HEAD+mDomain+URL_COLON+mPost+URL_QUERYAD;
        HttpUtils.getInstance().postAsyncAndHead(url,mSession,JSON_APLICATION, json.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogTools.e(TAG,"queryAd onFailure >>>>>>>>>>>>>>");
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    final String body = response.body().string();
                    response.close();
                    LogTools.d(TAG, "queryAd onResponse >>>>>>>>>>>>>>" + body);
                    call.cancel();
                    try {
                        JSONObject json = new JSONObject(body);
                        final int code = json.optInt(CODE);
                        if (code == 200) {
                            JSONArray jsonArray = json.optJSONArray(DATA);
                            final int length = jsonArray.length();
                            for (int i = 0; i < length; i ++){
                                JSONObject jsonObject = jsonArray.optJSONObject(i);
                                AdvertisementData advertisementData = new AdvertisementData();
                                final String adId = jsonObject.optString(ADID);
                                advertisementData.setAdId(adId);
                                advertisementData.setBeginTime(jsonObject.optString(BEGINTIME));
                                advertisementData.setCheckValue(jsonObject.optString(CHECKVALUE));
                                advertisementData.setName(jsonObject.optString(NAME));
                                advertisementData.setEndTime(jsonObject.optString(ENDTIME));
                                advertisementData.setOrderNo(jsonObject.optInt(ORDERNO));
                                advertisementData.setSize(jsonObject.optLong(SIZE));
                                advertisementData.setTimes(jsonObject.optInt(TIMES));
                                advertisementData.setType(jsonObject.optInt(TYPE));
                                advertisementData.setUrl(jsonObject.optString(URL));
                                while (true) {
                                    if (lock.tryLock()) {
                                        break;
                                    } else {
                                        Thread.sleep(50);
                                    }
                                }
                                if(!advertisementDataHashMap.containsKey(adId)) {
                                    advertisementDataHashMap.put(adId,advertisementData);
                                    new DownLoadThread(advertisementData, new DownLoadCallBack() {
                                        @Override
                                        public void onFailure(AdvertisementData mAdvertisementData,Exception e) {

                                        }

                                        @Override
                                        public void onCompleted(AdvertisementData mAdvertisementData) {
                                            LogTools.e(TAG,"onCompleted " + mAdvertisementData.toString());
                                            final String adID =  mAdvertisementData.getAdId();
                                            while (true) {
                                                if (lock.tryLock()) {
                                                    break;
                                                } else {
                                                    try {
                                                        Thread.sleep(50);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                            advertisementDataHashMap.remove(adID);
                                            advertisementDataQueue.add(advertisementData);
                                            if(advertisementDataHashMap.isEmpty()){
                                                try {
                                                    confirmReceive();
                                                    if(mADDownloadCallBack != null){
                                                        mADDownloadCallBack.onChangeADCompleted(advertisementDataQueue);
                                                    }
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            lock.unlock();
                                        }
                                    }).start();
                                }
                                lock.unlock();
                            }
                        } else {
                            final String message = json.optString(MESSAGE);
                            LogTools.e(TAG, "queryAd onFailure >>>>>>>>>>>>>>" + message);
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void heartbeat() throws IOException, JSONException {
        if(TextUtils.isEmpty(mToken)){
            return;
        }
        if(TextUtils.isEmpty(mSession)){
            return;
        }
        JSONObject json = new JSONObject();
        json.put(DEVICECODE,mDeviceCode);
        json.put(TOKEN,mToken);
        final String url =URL_HEAD+mDomain+URL_COLON+mPost+URL_HEARTBEAT;
        HttpUtils.getInstance().postAsyncAndHead(url,mSession ,JSON_APLICATION, json.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogTools.e(TAG,"heartbeat onFailure >>>>>>>>>>>>>>");
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    final String body = response.body().string();
                    response.close();
                    LogTools.d(TAG, "heartbeat onResponse >>>>>>>>>>>>>>" + body);
                    call.cancel();
                    try {
                        JSONObject json = new JSONObject(body);
                        final int code = json.optInt(CODE);
                        if (code == 200) {
                            JSONArray jsonArray = json.optJSONArray(DATA);
                            if(jsonArray != null && jsonArray.length() > 0) {
                                JSONObject jsonObject = jsonArray.optJSONObject(0);
                                if (jsonObject != null) {
                                    final String version = jsonObject.optString(VERSION);
                                    if (mVersion == null || !mVersion.equals(version)) {
                                        while (true) {
                                            if (lock.tryLock()) {
                                                break;
                                            } else {
                                                Thread.sleep(50);
                                            }
                                        }
                                        mVersion = version;
                                        advertisementDataQueue.clear();
                                        lock.unlock();
                                        queryAd();
                                    }
                                }
                            }
                        } else {
                            final String message = json.optString(MESSAGE);
                            LogTools.e(TAG, "heartbeat onFailure >>>>>>>>>>>>>>" + message);
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private Timer heartBeatTimer = null;
    private HeartBeatTimer heartBeatTimerTask = null;


    private void startHeartBeat(){
        if(heartBeatTimer == null){
            heartBeatTimer = new Timer();
            heartBeatTimerTask = new HeartBeatTimer();
            heartBeatTimer.schedule(heartBeatTimerTask,100,1000 * 60);
        }
    }

    private void stopHeartBeat(){
        if(heartBeatTimer != null){
            heartBeatTimer.cancel();
            heartBeatTimerTask.cancel();
            heartBeatTimerTask = null;
            heartBeatTimer = null;
        }
    }

    private class HeartBeatTimer extends TimerTask{

        @Override
        public void run() {
            try {
                heartbeat();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private interface DownLoadCallBack{
         void onFailure(AdvertisementData mAdvertisementData,Exception e);
         void onCompleted(AdvertisementData mAdvertisementData);
    }

    private class DownLoadThread extends Thread{
        private final AdvertisementData mAdvertisementData;
        private final DownLoadCallBack mCallBack;

        public DownLoadThread(AdvertisementData advertisementData,DownLoadCallBack callBack) {
            this.mAdvertisementData = advertisementData;
            this.mCallBack = callBack;
        }

        @Override
        public void run() {
            super.run();
            if(mAdvertisementData == null||mCallBack == null){
                LogTools.e(TAG,"mAdvertisementData == NULL or mCallBack == NULL");
                return;
            }
            try {
                HttpUtils.getInstance().downloadAsyncFile(mAdvertisementData.getUrl(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mCallBack.onFailure(mAdvertisementData,e);
                        call.cancel();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()) {
                            final byte[] body = response.body().bytes();
                            response.close();
//                            LogTools.d(TAG, "heartbeat onResponse >>>>>>>>>>>>>>" + body);
                            call.cancel();
                            final long size = mAdvertisementData.getSize();
                            final String checkValue = mAdvertisementData.getCheckValue();
                            if(size != body.length){
                                mCallBack.onFailure(mAdvertisementData,new NullPointerException("File format error"));
                                return;
                            }

                            final String url = mAdvertisementData.getUrl();
                            final String fileName =  mAdvertisementData.getName() + url.substring(url.length()-4);
                            File file = new File(MainApplication.videoFile);
                            if(!file.exists()){
                                file.mkdirs();
                            }
                            final String savePath = file.getAbsolutePath()+ File.separator + fileName;
                            boolean isFinish = FileUtil.writeBytes( savePath,body);
                            if(isFinish) {
                                final File saveFile = new File(savePath);
                                try {
                                    final String md5 = getFileMD5(saveFile);
                                    LogTools.e(TAG,"md5 = " + md5 + " , checkValue = " +checkValue);
                                    if (md5.toUpperCase().equals(checkValue.toUpperCase())) {
                                        mAdvertisementData.setSavePath(savePath);
                                        mCallBack.onCompleted(mAdvertisementData);
                                    }else {
                                        mCallBack.onFailure(mAdvertisementData,new NullPointerException("File format error"));
                                    }
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                    mCallBack.onFailure(mAdvertisementData,e);
                                }
                            }else {
                                mCallBack.onFailure(mAdvertisementData,new NullPointerException("save file error"));
                            }
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                mCallBack.onFailure(mAdvertisementData,e);
            }
        }
    }

    /**
     * get file md5
     * 获取文件的MD5
     * @param file
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private String getFileMD5(File file) throws NoSuchAlgorithmException, IOException {
        if (!file.isFile()) {
            return null;
        }
        int buffersize = 1024;
        FileInputStream fis = null;
        DigestInputStream dis = null;
        //创建MD5转换器和文件流
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        fis = new FileInputStream(file);
        dis = new DigestInputStream(fis, messageDigest);

        byte[] buffer = new byte[buffersize];
        //DigestInputStream实际上在流处理文件时就在内部就进行了一定的处理
        while (dis.read(buffer) > 0) ;

        //通过DigestInputStream对象得到一个最终的MessageDigest对象。
        messageDigest = dis.getMessageDigest();

        // 通过messageDigest拿到结果，也是字节数组，包含16个元素
        byte[] array = messageDigest.digest();
        // 同样，把字节数组转换成字符串
        StringBuilder hex = new StringBuilder(array.length * 2);
        for (byte b : array) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}
