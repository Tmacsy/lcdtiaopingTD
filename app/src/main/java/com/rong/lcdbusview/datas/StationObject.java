package com.rong.lcdbusview.datas;

public class StationObject {
	private String id;
	private String stationName;
	private String stationTypeface;
	private String arriveTypeface;
	
	
	public StationObject(String id, String stationName, String stationTypeface, String arriveTypeface) {
		super();
		this.id = id;
		this.stationName = stationName;
		this.stationTypeface = stationTypeface;
		this.arriveTypeface = arriveTypeface;
	}
	
	public StationObject() {
		super();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public String getStationTypeface() {
		return stationTypeface;
	}
	public void setStationTypeface(String stationTypeface) {
		this.stationTypeface = stationTypeface;
	}
	public String getArriveTypeface() {
		return arriveTypeface;
	}
	public void setArriveTypeface(String arriveTypeface) {
		this.arriveTypeface = arriveTypeface;
	}

	@Override
	public String toString() {
		return "StationObject [id=" + id + ", stationName=" + stationName + ", stationTypeface=" + stationTypeface
				+ ", arriveTypeface=" + arriveTypeface + "]";
	}
	
	
	
}
