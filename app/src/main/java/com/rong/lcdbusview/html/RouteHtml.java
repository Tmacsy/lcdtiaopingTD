package com.rong.lcdbusview.html;

import com.rong.lcdbusview.MainApplication;

public class RouteHtml {
	private RouteHtml() {

	}

	/**
	 * 设置test.html
	 * 
	 * @param body
	 * @return
	 */
	public static String getHeaderHtml(String body) {
		StringBuffer builder = new StringBuffer();
		builder.append("<!DOCTYPE html><head>");
		builder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\" />");
		builder.append("<h1 style=\"font-family:verdana\" text-align: center><style type=\"text/css\">");
		builder.append("*{margin:0px;border:0px;padding:0px;}");
		builder.append(".shuli{text-align:center;width:" + MainApplication.stationTypeface + "px;line-height:"
				+ MainApplication.stationTypeface + "px;font-size:" + MainApplication.stationTypeface + "px;margin-right:"
				+ MainApplication.interval + "px;margin-left:" + MainApplication.interval + "px;}");
		builder.append("</style></head>");
		builder.append("<body background=\"background.jpg\";>");
		builder.append(body);
		builder.append("</body></html>");
		return builder.toString();
	}

	/**
	 * 设置没个站点的html
	 * 
	 * @param stations
	 * @param type
	 * @return
	 */
	public static String getBody(String stations,String typeface, int type) {
		StringBuilder builder = new StringBuilder();
		builder.append("<p><li style=\"float:left;list-style:none;\"><div class=\"shuli\" style=\"font-size:"
				+ typeface
				+ "px;width:"
				+ typeface
				+ "px;line-height:"
				+ typeface
				+ "px\">");
		switch (type) {
		case 0:
			builder.append("<font><img src=\"in.png\" width=\"25\" height=\"25\" />");
			builder.append(stations);
			builder.append("</font></div></p>");
			break;
		case 1:
			builder.append("<img src=\"now.gif\" width=\"25\" height=\"25\" />");
			builder.append(stations);
			builder.append("</div></p>");
			break;
		case 2:
			builder.append("<img src=\"out.png\" width=\"25\" height=\"25\" />");
			builder.append(stations);
			builder.append("</div></p>");
			break;
		}

		return builder.toString();
	}

	/**
	 * 设置生成线路的line.html
	 * 
	 * @param name
	 *            线路
	 * @param start
	 *            首班
	 * @param end
	 *            末班
	 * @param startStation
	 *            开始站点
	 * @param endStation
	 *            终点
	 * @param nextStation
	 *            下一站
	 * @return
	 */
	public static String getLine(String name, String start, String end, String startStation, String endStation,
			String nextStation) {
		StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html><html><head>");
		builder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\"/>");
		builder.append("<h1 style=\"font-family:verdana\" text-align: center><style type=\"text/css\">");
		builder.append("*{margin:0px;border:0px;padding:0px;}div#container{width:" + MainApplication.width);
		builder.append("px}div#name {background-color:#008080;height:60px;width:");
		builder.append((MainApplication.width - 160) + "px");
		builder.append(
				";text-align:center;float:left;}div#time {background-color:#008080;height:60px;width:160px;text-align:center;float:left;}");
		builder.append("div#dir {background-color:#008080;height:40px;width:");
		builder.append(MainApplication.width + "px;float:left;text-align:center;}</style>");
		builder.append(
				"</head><body><div id=\"container\"><div id=\"name\"><p><span style=\"font-size:20px;\"> 欢迎乘坐</span><span style=\"font-size:50px;margin:5px;\">");
		builder.append(name);
		builder.append(
				"</span><span style=\"font-size:20px;=\"> 公交车</span></p></div><div id=\"time\"><span style=\"font-size:20px;line-height:1;\"><p>首班: ");
		builder.append(start);
		builder.append("</p><p>末班: ");
		builder.append(end);
		builder.append("</p></span></div><div id=\"dir\"><span style=\"font-size:26px;\"><p>本车由 ");
		builder.append(startStation + " 开往 " + endStation);
		builder.append(
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 下一站:");
		builder.append(nextStation);
		builder.append("</p></span></div></body></html>");
		return builder.toString();
	}

	/**
	 * 生成1.html
	 * 
	 * @return
	 */
	public static String onceHtml() {
		StringBuilder builder = new StringBuilder();
		builder.append(
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html><head><title> ");
		builder.append("New Document </title><meta http-equiv=\"content-type\" content=\"text/html;");
		builder.append(
				"charset=gb3212\"/><style type=\"text/css\">*{margin:0px;border:0px;padding:0px;}</style></head>");
		builder.append(
				"<body><p id=\"demo\"></p><script type=\"text/javascript\"> function doAjaxCall(the_request){ var request=null,voteable,voteable1,age,");
		builder.append(
				"flag = 0; if(window.XMLHttpRequest){request=new XMLHttpRequest();}else if(window.ActiveXObject){");
		builder.append(
				"request=new ActiveXObject(\"Microsoft.XMLHTTP\");}if(request){request.open(\"GET\",the_request,true);");
		builder.append(
				"request.onreadystatechange=function(){if(request.readyState===4){if (request.status == 200 || request.status == 0){");
		builder.append(
				"voteable = request.responseText;document.getElementById(\"demo\").innerHTML=voteable;age = parseInt(voteable);");
		builder.append(
				"if(document.getElementById(\"ifr\").name != voteable){if(age > 18){document.getElementById(\"ifr\").src=\"realtime.html\";}");
		builder.append(
				"if(age < 18){document.getElementById(\"ifr\").src=\"disweb.html\";}document.getElementById(\"ifr\").name=voteable;}");
		builder.append(
				"}}}request.send(null);}else{alert(\"error\");}}setInterval(function(){doAjaxCall('a.txt')},100*1000);");
		builder.append(" </script><iframe id=\"ifr\" src=\"disweb.html\" name=\"11\" width=\"");
		builder.append(MainApplication.width + "px\" height=\"");
		builder.append(MainApplication.height + "px");
		builder.append("\" frameborder=\"0\"></iframe></body></html>");
		return builder.toString();
	}

	/**
	 * 生产2.hmtl
	 * @return
	 */
	public static String twoHtml() {
		StringBuilder builder = new StringBuilder();
		builder.append(
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html><head><title> ");
		builder.append("New Document </title><meta http-equiv=\"content-type\" content=\"text/html;");
		builder.append(
				"charset=gb3212\"/><style type=\"text/css\">*{margin:0px;border:0px;padding:0px;}</style></head>");
		builder.append(
				"<body><p id=\"demo\"></p><script type=\"text/javascript\"> function doAjaxCall(the_request){ var request=null,voteable,voteable1,age,");
		builder.append(
				"flag = 0; if(window.XMLHttpRequest){request=new XMLHttpRequest();}else if(window.ActiveXObject){");
		builder.append(
				"request=new ActiveXObject(\"Microsoft.XMLHTTP\");}if(request){request.open(\"GET\",the_request,true);");
		builder.append(
				"request.onreadystatechange=function(){if(request.readyState===4){if (request.status == 200 || request.status == 0){");
		builder.append(
				"voteable = request.responseText;document.getElementById(\"demo\").innerHTML=voteable;age = parseInt(voteable);");
		builder.append(
				"if(document.getElementById(\"ifr\").name != voteable){if(age > 18){document.getElementById(\"ifr\").src=\"realtime.html\";}");
		builder.append(
				"if(age < 18){document.getElementById(\"ifr\").src=\"disweb.html\";}document.getElementById(\"ifr\").name=voteable;}");
		builder.append(
				"}}}request.send(null);}else{alert(\"error\");}}setInterval(function(){doAjaxCall('a.txt')},100*1000);");
		builder.append(" </script><iframe id=\"ifr\" src=\"realtime.html\" name=\"11\" width=\"");
		builder.append(MainApplication.width + "px\" height=\"");
		builder.append(MainApplication.height + "px\"");
		builder.append("\" frameborder=\"0\"></iframe> </body></html>");
		return builder.toString();
	}

	/**
	 * 生成disweb.html
	 * @return
	 */
	public static String diswebHtml() {

		StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html><html><style type=\"text/css\">*{");
		builder.append("margin:0px;border:0px;padding:0px;}</style><body>");
		builder.append("<iframe src=\"line.html\"width=\"" + MainApplication.width
				+ "px\" height=\"100px\"frameborder=\"0\"></iframe>");
		builder.append("<iframe src=\"test.html\"width=\"" + MainApplication.width + "px\" height=\""
				+ (MainApplication.height - 130) + "px\" frameborder=\"0\"></iframe>");
		builder.append("<iframe src=\"text.html\"width=\"" + MainApplication.width
				+ "px\"height=\"30px\"frameborder=\"0\"></iframe>");
		builder.append("</body></html>");
		return builder.toString();
	}
	/**
	 * 生成text.html
	 * @return
	 */
	public static String textHtml() {
		StringBuilder builder = new StringBuilder();
		builder.append(
				"<!DOCTYPE html><html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\" /> <style type=\"text/css\">*{");
		builder.append("margin:0px;border:0px;padding:0px;}");
		builder.append("div#text {background-color:#008080;height:30px;width:" + MainApplication.width
				+ "px;text-align:center;}");
		builder.append("</style><body>");
		builder.append("<div id=\"text\"><p>");
		builder.append("<span style=\"font-size:20px;line-height:1;\">本车实行前门上车后门下车，自动投币，不设找零，上车请主动刷卡投币! \"</span></p>");
		builder.append("</body></html>");
		return builder.toString();
	}
}
