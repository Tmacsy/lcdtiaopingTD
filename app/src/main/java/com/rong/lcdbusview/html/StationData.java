package com.rong.lcdbusview.html;

public class StationData {
	private String name;
	private boolean isActive;
	private boolean hasError;
	private String Deputy;
	private int stationNum = -1;
	
	public StationData() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isHasError() {
		return hasError;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	public String getDeputy() {
		return Deputy;
	}

	public void setDeputy(String deputy) {
		Deputy = deputy;
	}

	
	
	public int getStationNum() {
		return stationNum;
	}

	public void setStationNum(int stationNum) {
		this.stationNum = stationNum;
	}

	@Override
	public String toString() {
		return "StationData [name=" + name + ", isActive=" + isActive + ", hasError=" + hasError + ", Deputy=" + Deputy
				+ ", stationNum=" + stationNum + "]";
	}
	
	
}
