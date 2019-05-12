package com.rong.lcdbusview.datas;

public class PlayFile {
	private String name;
	private String path;
	private long stopTime;
	private int times;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public long getStopTime() {
		return stopTime;
	}
	public void setStopTime(long stopTime) {
		this.stopTime = stopTime;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	
	public PlayFile() {
		super();
	}
	
	public PlayFile(String name, String path, long stopTime, int times) {
		super();
		this.name = name;
		this.path = path;
		this.stopTime = stopTime;
		this.times = times;
	}
	
	@Override
	public String toString() {
		return "PlayFile [name=" + name + ", path=" + path + ", stopTime=" + stopTime + ", times=" + times + "]";
	}
	
	
	
}
