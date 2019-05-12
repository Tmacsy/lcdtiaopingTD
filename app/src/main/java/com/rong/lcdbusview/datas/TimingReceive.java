package com.rong.lcdbusview.datas;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

public class TimingReceive {
	
	private int stationNamber;
	private int inoutStation;
	private int direction;
	private byte[] reserved;
	
	public TimingReceive(byte[] data) {
		super();
		readData(data);
	}
	
	
	
	public TimingReceive(int stationNamber, int inoutStation, int direction, byte[] reserved) {
		super();
		this.stationNamber = stationNamber;
		this.inoutStation = inoutStation;
		this.direction = direction;
		this.reserved = reserved;
	}

	private void readData(byte[] data){
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		DataInputStream in = new DataInputStream(stream);
		try {
			stationNamber = in.readByte();
			inoutStation = in.readByte();
			direction = in.readByte();
			in.read(reserved);
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

	public int getStationNamber() {
		return stationNamber;
	}
	public void setStationNamber(int stationNamber) {
		this.stationNamber = stationNamber;
	}
	public int getInoutStation() {
		return inoutStation;
	}
	public void setInoutStation(int inoutStation) {
		this.inoutStation = inoutStation;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public byte[] getReserved() {
		return reserved;
	}
	public void setReserved(byte[] reserved) {
		this.reserved = reserved;
	}

	@Override
	public String toString() {
		return "TimingReceive [stationNamber=" + stationNamber + ", inoutStation=" + inoutStation + ", direction="
				+ direction + ", reserved=" + Arrays.toString(reserved) + "]";
	}
	
}
