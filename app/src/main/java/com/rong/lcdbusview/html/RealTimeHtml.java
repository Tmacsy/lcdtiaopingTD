package com.rong.lcdbusview.html;

import com.rong.lcdbusview.MainApplication;

public class RealTimeHtml {
	
	private RealTimeHtml(){
		
	}
	/**
	 * 生成到站或者出站的realtime.html
	 * @param nextStation
	 * @param textContent
	 * @return
	 */
	public static String getHeaderHtml(String nextStation,String textContent,String type) {
		StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html><head>");
		builder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\" />");
		builder.append("<style type=\"text/css\">*{margin:0px;border:0px;padding:0px;}div#container{width:"
				+ MainApplication.width
				+ "px}p#text {height:"
				+(MainApplication.height/4+"px")
				+ ";width:"
				+ MainApplication.width
				+ "px;text-align:center;}</style>");
		builder.append("<h1 style=\"font-family:verdana\" text-align: center>");
		builder.append("</head><body background=\"background.jpg\";><p id=\"text\"><center><span style=\"font-size:"
				+ type.trim()
				+ "px\" text-align: center >");
		builder.append(nextStation);
		builder.append("</span></center></p><p><center><span style=\"font-size:50px\"> ");
		builder.append(textContent);
		builder.append("</span></center></p></body><html>");
		return builder.toString();
	}
}
