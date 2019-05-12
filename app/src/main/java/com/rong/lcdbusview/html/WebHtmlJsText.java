package com.rong.lcdbusview.html;

import android.text.Layout;
import android.text.TextUtils;

import com.rong.lcdbusview.tools.LogTools;

public class WebHtmlJsText {
	
	private WebHtmlJsText(){
		
	}
	/*
	public static String  getRouteHtml(String route,String startTime,String endTime,String total_top,String total_buttom,String times){
		StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n<meta charset=\"utf-8\">\n");
		builder.append("<!-- Always force latest IE rendering engine or request Chrome Frame -->\n");
		builder.append("<meta content=\"IE=edge,chrome=1\\\" http-equiv=\"X-UA-Compatible\">\n");
		builder.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">\n");
		builder.append("<title>Cart</title>\n<link href=\"css/bus.css\"  rel=\"stylesheet\" />\n</head>\n<body>\n");
		builder.append("<div id=\"total\">\n<div class=\"video\">\n<img src=\"images/adver.png\" />\n");
		builder.append("</div>\n<div class=\"total\">\n<div class=\"total-title\">\n<h3><span>欢迎乘坐 <small>");
		builder.append(route);
		builder.append("</small>路 公交车</span><span>首班:<small>");
		builder.append(startTime);
		builder.append("</small></span><span> 末班:<small>");
		builder.append(endTime);
		builder.append("</small></span><span> 发车间隔:<small>");
		builder.append(times);
		builder.append("<small></span></h3>\n</div>\n");
		builder.append("<div class=\"total-top\" id=\"total-top\">\n");
		builder.append(total_top);
		builder.append("</div>\n<div class=\"total-buttom\" id=\"total-buttom\">\n");
		builder.append(total_buttom);  
		builder.append("</div>\n</div>\n</div>\n</body>\n</html>");		
		return builder.toString();
	}
	*/
	public static String  getRouteHtml(String route,String startTime,String endTime,String total_top,String total_buttom,String times,int withtype){
		StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n<meta charset=\"utf-8\">\n");
		builder.append("<!-- Always force latest IE rendering engine or request Chrome Frame -->\n");
		builder.append("<meta content=\"IE=edge,chrome=1\\\" http-equiv=\"X-UA-Compatible\">\n");
		builder.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">\n");
		if(withtype == 0)
		{
			builder.append("<title>Cart</title>\n<link href=\"css/bus.css\"  rel=\"stylesheet\" />\n</head>\n<body>\n");
		}
		else if(withtype == 1)
		{
			builder.append("<title>Cart</title>\n<link href=\"css/bus1.css\"  rel=\"stylesheet\" />\n</head>\n<body>\n");
		}
		else if(withtype == 2)
		{
			builder.append("<title>Cart</title>\n<link href=\"css/bus2.css\"  rel=\"stylesheet\" />\n</head>\n<body>\n");
		}
		else if(withtype == 3)
		{
			builder.append("<title>Cart</title>\n<link href=\"css/bus3.css\"  rel=\"stylesheet\" />\n</head>\n<body>\n");
		}
		builder.append("<div id=\"total\">\n<div class=\"video\">\n<img src=\"images/adver.png\" />\n");
		if(route != null)
		{
			builder.append("</div>\n<div class=\"total\">\n<div class=\"total-title\">\n<h3><span>欢迎乘坐 <small>");
			//builder.append("</div>\n<div class=\"total\">\n<div class=\"total-title\">\n<h3><span>Welcome to No. <small>");
			builder.append(route);
			builder.append("</small>路 公交车</span><span><small>");
			//builder.append("</small> bus </span><span><small>");
		}
		else
		{
			builder.append("</div>\n<div class=\"total\">\n<div class=\"total-title\">\n<h3><span><small>");
			//	builder.append(route);
			builder.append("</small></span><span><small>");
		}

	//	builder.append(startTime);
		builder.append("</small></span><span><small>");
		//builder.append(endTime);
		builder.append("</small></span><span><small>");
		//builder.append(times);
		builder.append("<small></span></h3>\n</div>\n");
		builder.append("<div class=\"total-top\" id=\"total-top\">\n");
		builder.append(total_top);
		builder.append("</div>\n<div class=\"total-buttom\" id=\"total-buttom\">\n");
		builder.append(total_buttom);
		builder.append("</div>\n</div>\n</div>\n</body>\n</html>");
		return builder.toString();
	}
	/**
	 * 设置没个站点的html
	 * 
	 * @param stations
	 * @param type
	 * @return
	 */
	public static String getTotalTop(StationData stationData, int type) {
		StringBuilder builder = new StringBuilder();
		builder.append("<div class=\"total-js\">\n");
		String stationre = stationData.getName().replace("(","︵").replace("（","︵").replace(")","︶").replace("）","︶");
		switch (type) {
		case 0:
			if(stationre.length() > 6 ){
				builder.append("<li class=\"over in longword\">");
			}else{
			builder.append("<li class=\"over in\">");
			}
			builder.append(stationre);
			builder.append("<i></i><p class=\"over in\">");
			builder.append(stationData.getDeputy());
			builder.append("</p></li>\n");
			break;
		case 1:
			if(stationre.length() > 6 ){
				builder.append("<li class=\"in longword\">");
			}else{
				builder.append("<li class=\"in\">");
			}
			builder.append(stationre);
			builder.append("<i></i><p class=\"in\">");
			builder.append(stationData.getDeputy());
			builder.append("</p></li>\n");
			break;
		case 2:
			if(stationre.length() > 6 ){
				builder.append("<li class=\" longword\">");
			}else{
				builder.append("<li class=\"\">");
			}
			builder.append(stationre);
			builder.append("<i></i><p class=\"\">");
			builder.append(stationData.getDeputy());
			builder.append("</p></li>\n");
			break;
		}
		builder.append("</div>");
		return builder.toString();
	}

	/**
	 * 设置没个站点的html
	 * 
	 * @param stations
	 * @param type
	 * @return
	 */
	public static String getTotalButtom(StationData stationData, int type) {
		StringBuilder builder = new StringBuilder();
		builder.append("<div class=\"total-js2\" >\n");
		switch (type) {
		case 0:
			if(stationData.getName().length() > 6){
				builder.append("<li class=\"over in longword\"><i></i>");
			}else{
			builder.append("<li class=\"over in\"><i></i>");
			}
			builder.append(stationData.getName());
			builder.append("<p class=\"over in\">");
			builder.append(stationData.getDeputy());
			builder.append("</p></li>\n");
			break;
		case 1:
			if(stationData.getName().length() > 6 ){
				builder.append("<li class=\"in longword\"><i></i>");
			}else{
				builder.append("<li class=\"in\"><i></i>");
			}
			builder.append(stationData.getName());
			builder.append("<p class=\"in\">");
			builder.append(stationData.getDeputy());
			builder.append("</p></li>\n");
			break;
		case 2:
			if(stationData.getName().length() > 6 ){
				builder.append("<li class=\" longword\"><i></i>");
			}else{
				builder.append("<li class=\"\"><i></i>");
			}
			builder.append(stationData.getName());
			builder.append("<p class=\"\">");
			builder.append(stationData.getDeputy());
			builder.append("</p></li>\n");
			break;
		}
		builder.append("</div>");
		return builder.toString();
	}
	
	public static String getPageHtml(StationData startStation,StationData defStation,StationData nextStation,StationData endStation){
		LogTools.d("getPageHtml","getPageHtml");
		StringBuilder builder = new StringBuilder();

		builder.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n<meta charset=\"utf-8\">\n");
		builder.append("<!-- Always force latest IE rendering engine or request Chrome Frame -->\n");
		builder.append("<meta content=\"IE=edge,chrome=1\" http-equiv=\"X-UA-Compatible\">\n");
		builder.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">\n");
		builder.append("<title>Cart</title>\n<link href=\"css/bus.css\"  rel=\"stylesheet\" />\n</head>\n<body>\n"); 
		builder.append("<div id=\"total\">\n<div class=\"video\">\n");
		builder.append("<img src=\"images/adver.png\" />\n</div>\n<div class=\"total-list\">\n<div class=\"total-page\">\n");
		builder.append("<div class=\"box start\">\n<h1>");
		LogTools.d("getPageHtml","startStation");
		builder.append(startStation.getName());
		builder.append("</h1>\n");
		if(!TextUtils.isEmpty(startStation.getDeputy())){
			builder.append("<p>");
			builder.append(startStation.getDeputy());
			builder.append("</p>\n");
		}	
		builder.append("</div>\n<div class=\"icon page-right\">\n<img src=\"images/right.png\" class=\"thereD\" />\n");
		builder.append("</div>\n<div class=\"box current\">\n<h1>");
		LogTools.d("getPageHtml","defStation");

		builder.append(defStation.getName());
		builder.append("</h1>\n");
		if(!TextUtils.isEmpty(defStation.getDeputy())){
			builder.append("<p>");
			builder.append(defStation.getDeputy());
			builder.append("</p>\n");
		}
		builder.append("<i class=\"icon-t\"></i>\n");
		builder.append("</div>\n<div class=\"icon page-right\">\n<img src=\"images/right.png\" class=\"thereD\"/>\n</div>\n");
		builder.append("<div class=\"box next\">\n<h1>");
		LogTools.d("getPageHtml","nextStation");
		builder.append(nextStation.getName());
		builder.append("</h1>\n");
		if(!TextUtils.isEmpty(nextStation.getDeputy())){
			builder.append("<p>");
			builder.append(nextStation.getDeputy());
			builder.append("</p>\n");
		}					
		builder.append("<i class=\"icon-t\"></i>\n");					
		builder.append("</div>\n<div class=\"icon page-right\">\n<img src=\"images/right.png\" class=\"thereD\"/>\n</div>");
		builder.append("<div class=\"box end\">\n<h1>");
		LogTools.d("getPageHtml","endStation");
		builder.append(endStation.getName());
		builder.append("</h1>\n");		
		if(!TextUtils.isEmpty(endStation.getDeputy())){
			builder.append("<p>");
			builder.append(endStation.getDeputy());
			builder.append("</p>\n");
		}	
		builder.append("</div>\n</div>\n </div>\n</div>\n\"<script src=\"js/vue.js\"></script>\n");					
		return builder.toString();
	}
	
	public static String getCssHtml(){
		StringBuilder builder = new StringBuilder();
		return builder.toString();
	}
}
