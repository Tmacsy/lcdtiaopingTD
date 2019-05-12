package com.rong.lcdbusview.link;

public class StationMsg {

	private int StationNum;//当前站序
	private String stationName;//站点
	
	
	public StationMsg() {
		super();
	}
	
	public int getStationNum() {
		return StationNum;
	}
	public void setStationNum(int stationNum) {
		StationNum = stationNum;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	@Override
	public String toString() {
		return "StationMsg [StationNum=" + StationNum + ", stationName=" + stationName + "]";
	}
	
	
	
}
