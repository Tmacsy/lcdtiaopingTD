package com.rong.lcdbusview.datas;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RouteInformation {
	
	private int upstreamAll;
	private int downAll;
	private String firstPlance;
	private String endPlance;
	private byte[] reserved;
	private int routeNameLength;
	private String routeName;
	private List<String> upStations;
	private List<String> downStations;
	
	public RouteInformation(byte[] data) {
		super();
		readData(data);
	}

	public RouteInformation(int upstreamAll, int downAll, String firstPlance, String endPlance, byte[] reserved,
			int routeNameLength, String routeName, List<String> upStations, List<String> downStations) {
		super();
		this.upstreamAll = upstreamAll;
		this.downAll = downAll;
		this.firstPlance = firstPlance;
		this.endPlance = endPlance;
		this.reserved = reserved;
		this.routeNameLength = routeNameLength;
		this.routeName = routeName;
		this.upStations = upStations;
		this.downStations = downStations;
	}

	public int getUpstreamAll() {
		return upstreamAll;
	}

	public void setUpstreamAll(int upstreamAll) {
		this.upstreamAll = upstreamAll;
	}

	public int getDownAll() {
		return downAll;
	}

	public void setDownAll(int downAll) {
		this.downAll = downAll;
	}

	public String getFirstPlance() {
		return firstPlance;
	}

	public void setFirstPlance(String firstPlance) {
		this.firstPlance = firstPlance;
	}

	public String getEndPlance() {
		return endPlance;
	}

	public void setEndPlance(String endPlance) {
		this.endPlance = endPlance;
	}

	public byte[] getReserved() {
		return reserved;
	}

	public void setReserved(byte[] reserved) {
		this.reserved = reserved;
	}

	public int getRouteNameLength() {
		return routeNameLength;
	}

	public void setRouteNameLength(int routeNameLength) {
		this.routeNameLength = routeNameLength;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public List<String> getUpStations() {
		return upStations;
	}

	public void setUpStations(List<String> upStations) {
		this.upStations = upStations;
	}

	public List<String> getDownStations() {
		return downStations;
	}

	public void setDownStations(List<String> downStations) {
		this.downStations = downStations;
	}

	private void readData(byte[] data){
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		DataInputStream in = new DataInputStream(stream);
		upStations = new ArrayList<String>();
		try {
			upstreamAll = in.readByte();
			downAll = in.readByte();
			int firstHour = in.readByte();
			int firstMinute = in.readByte();
			firstPlance = Integer.toHexString(firstHour)+"点"+Integer.toHexString(firstMinute);
			int endtHour = in.readByte();
			int endMinute = in.readByte();
			endPlance = Integer.toHexString(endtHour)+"点"+Integer.toHexString(endMinute);
			in.readShort();
			routeNameLength = in.readByte();
			byte[] route_name = new byte[routeNameLength];
			in.read(route_name);
			routeName = new String(route_name, "GBK");
			reserved = new byte[2];
			in.read(reserved);
			for(int i = 0;i <upstreamAll;i++){
				int length = in.readByte();
				byte[] stationName = new byte[length];
				in.read(stationName);
				upStations.add(new String(stationName,"GBK"));
			}
			for(int i = 0;i <downAll;i++){
				int length = in.readByte();
				byte[] stationName = new byte[length];
				in.read(stationName);
				downStations.add(new String(stationName, "GBK"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(stream != null){
					stream.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String toString() {
		return "RouteInformation [upstreamAll=" + upstreamAll + ", DownAll=" + downAll + ", firstPlance=" + firstPlance
				+ ", endPlance=" + endPlance + ", reserved=" + Arrays.toString(reserved) + ", routeNameLength="
				+ routeNameLength + ", routeName=" + routeName + ", upStations=" + upStations + ", DownStations="
				+ downStations + "]";
	}
}
