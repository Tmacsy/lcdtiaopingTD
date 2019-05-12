package com.rong.lcdbusview.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;
import org.nanohttpd.util.IHandler;

import com.rong.lcdbusview.tools.LogTools;

import android.text.TextUtils;

public class RouteServer extends NanoHTTPD {

	private static final String TAG = "RouteServer";
	public static final int DEFAULT_SERVER_PORT = 9080;
	public static final String path = "/mnt/sdcard/webView";
	private String mVideoFilePath;

	public RouteServer(String mVideoFilePath) {
		super(DEFAULT_SERVER_PORT);
		this.mVideoFilePath = mVideoFilePath;
		saveFile(path);
		
	}

	@Override
	public Response serve(IHTTPSession session) {
		LogTools.d(TAG, "OnRequest:" + session.getUri());
		String uri = session.getUri();
		LogTools.d(TAG, uri);
		String[] s = uri.split("/");
		if (filesName != null && !filesName.isEmpty() && s.length > 0) {
			for (String file : filesName) {
				if(s[s.length - 1].contains(file)){
					return responseVideoStream(session, path+"/"+file);
				}
			}
		}
		return responseVideoStream(session, path+"/1.html");
	}

	@Override
	public void addHTTPInterceptor(IHandler<IHTTPSession, Response> interceptor) {
		// TODO Auto-generated method stub
		super.addHTTPInterceptor(interceptor);
	}

	public Response response404(IHTTPSession session, String url) {
		StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html><html><body>");
		builder.append("Sorry, Can't Found " + url + " !");
		builder.append("</body></html>\n");
		return Response.newFixedLengthResponse(builder.toString());
	}

	public Response responseVideoStream(IHTTPSession session, String mVideoFilePath) {
		try {
			FileInputStream fis = new FileInputStream(mVideoFilePath);
			return Response.newChunkedResponse(Status.OK, "text/html", fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return response404(session, mVideoFilePath);
		}
	}

	private List<String> filesName;

	private void saveFile(String path) {
		filesName = new ArrayList<String>();
		if (!TextUtils.isEmpty(path)) {
			File file = new File(path);
			if (file.exists()) {
				if (file.isDirectory()) {
					File[] files = file.listFiles();
					for (File file2 : files) {
						LogTools.d(TAG, file2.getName());
						filesName.add(file2.getName());
					}
				}
			}
		}
	}
}
