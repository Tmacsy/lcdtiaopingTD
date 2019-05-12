package com.rong.lcdbusview.tools;

import android.util.Log;

public class LogTools {
	private static final boolean isShow = true;
	private static final boolean isWrite = false;
	private static final String path = "mnt/sdcard"+ "/LogTools";
	private static final String FORMAT = "yyyyMMddHHmmss";

	private LogTools() {
		throw new AssertionError();
	}

	public static void e(String TAG, String msg) {
		if (isShow) {
			Log.e(TAG, msg);
			if (isWrite) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTools.getCurDateStr());
				builder.append(":/n");
				builder.append(TAG);
				builder.append(":");
				builder.append(msg);
				FileUtil.writeString(path+"/"+DateTools.getCurDateStr(FORMAT)+".txt", builder.toString(), "GBK");
			}
		}
	}

	public static void v(String TAG, String msg) {
		if (isShow) {
			Log.v(TAG, msg);
			if (isWrite) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTools.getCurDateStr());
				builder.append(":/n");
				builder.append(TAG);
				builder.append(":");
				builder.append(msg);
				FileUtil.writeString(path+"/"+DateTools.getCurDateStr(FORMAT)+".txt", builder.toString(), "GBK");
			}
		}
	}

	public static void i(String TAG, String msg) {
		if (isShow) {
			Log.i(TAG, msg);
			if (isWrite) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTools.getCurDateStr());
				builder.append(":/n");
				builder.append(TAG);
				builder.append(":");
				builder.append(msg);
				FileUtil.writeString(path+"/"+DateTools.getCurDateStr(FORMAT)+".txt", builder.toString(), "GBK");
			}
		}
	}

	public static void d(String TAG, String msg) {
		if (isShow) {
			Log.d(TAG, msg);
			if (isWrite) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTools.getCurDateStr());
				builder.append(":/n");
				builder.append(TAG);
				builder.append(":");
				builder.append(msg);
				FileUtil.writeString(path+"/"+DateTools.getCurDateStr(FORMAT)+".txt", builder.toString(), "GBK");
			}
		}
	}
	
	public static void d(String TAG, byte[] msg) {
		if (isShow) {
			StringBuilder builder1 = new StringBuilder();
			for(byte b : msg){
				int x = b & 0xff;
				if(x<0x0f){
					builder1.append("0"+Integer.toHexString(x));
				}else{
					builder1.append(Integer.toHexString(x));
				}
				builder1.append(" ");
			}
			Log.d(TAG, builder1.toString());
			if (isWrite) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTools.getCurDateStr());
				builder.append(":/n");
				builder.append(TAG);
				builder.append(":");
				builder.append(builder1.toString());
				FileUtil.writeString(path+"/"+DateTools.getCurDateStr(FORMAT)+".txt", builder.toString(), "GBK");
			}
		}
	}

	public static void w(String TAG, String msg) {
		if (isShow) {
			Log.w(TAG, msg);
			if (isWrite) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTools.getCurDateStr());
				builder.append(":/n");
				builder.append(TAG);
				builder.append(":");
				builder.append(msg);
				FileUtil.writeString(path+"/"+DateTools.getCurDateStr(FORMAT)+".txt", builder.toString(), "GBK");
			}
		}
	}
}
