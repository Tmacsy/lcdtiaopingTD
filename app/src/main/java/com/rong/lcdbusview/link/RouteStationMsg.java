package com.rong.lcdbusview.link;

import com.rong.lcdbusview.tools.LogTools;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RouteStationMsg {

	private int stationAll;

	private List<StationMsg> stations;
	
	public RouteStationMsg(byte[] buffer) {
		super();
		readByte(buffer);
	}

	private void readByte(byte[] buffer){
		ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
		DataInputStream in = new DataInputStream(stream);
		try {
			 stations = new ArrayList<>();
			 int allnb = 0;
			 while (in.available() != 0) {
				StationMsg station = new StationMsg();
				int num = in.readUnsignedByte();
				station.setStationNum(allnb);
				int size = in.readUnsignedByte();
				byte[] src = new byte[size];
				in.read(src);
				station.setStationName(new String(src,"GBK"));
				stations.add(station);
				int sizee = in.readUnsignedByte();
				LogTools.d("RouteStationMsg:", station.getStationName()+","+size+","+num);
				allnb ++;
			}
			stationAll = allnb;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stream != null) {
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
	private void readByteall(byte[] buffer){
		ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
		DataInputStream in = new DataInputStream(stream);
		try {
			stationAll = in.readUnsignedByte();
			stations = new ArrayList<>();
			while (in.available() != 0) {
				StationMsg station = new StationMsg();
				int num = in.readUnsignedByte();
				station.setStationNum(num);
				int size = in.readUnsignedByte();
				byte[] src = new byte[size];
				in.read(src);
				station.setStationName(new String(src,"GBK"));
				stations.add(station);
				//LogTools.d("RouteStationMsg:", new String(src,"GBK")+","+size+","+num);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stream != null) {
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
	public int getStationAll() {
		return stationAll;
	}

	public void setStationAll(int stationAll) {
		this.stationAll = stationAll;
	}

	public List<StationMsg> getStations() {
		return stations;
	}

//	public void setStations(List<StationMsg> stations) {
//		this.stations = stations;
//	}

	@Override
	public String toString() {
		return "RouteStationMsg [stationAll=" + stationAll + ", stations=" + stations + "]";
	}
	
	
	
}
