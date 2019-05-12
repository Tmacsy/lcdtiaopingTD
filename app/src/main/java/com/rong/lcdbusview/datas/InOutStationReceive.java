package com.rong.lcdbusview.datas;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.rong.lcdbusview.tools.StreamTool;

public class InOutStationReceive {

	private int in_out;
	private int serial_number;
	private int stay_time;
	private byte reserved;
	private String currentStation;
	private String promptContent;
	
	
	
	public InOutStationReceive(byte[] data) {
		super();
		readData(data);
	}

	public InOutStationReceive(int in_out, int serial_number, int stay_time, byte reserved, String currentStation,
			String promptContent) {
		super();
		this.in_out = in_out;
		this.serial_number = serial_number;
		this.stay_time = stay_time;
		this.reserved = reserved;
		this.currentStation = currentStation;
		this.promptContent = promptContent;
	}

	private void readData(byte[] data){
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		DataInputStream in = new DataInputStream(stream);
		try {
			in_out = in.readByte();
			serial_number = in.readByte();
			stay_time = in.readByte();
			reserved = in.readByte();
			byte[] station = new byte[40];
			in.read(station,0,40);
			byte[] content = new byte[60];
			in.read(content,0,60);
			currentStation = new String(station,0,StreamTool.returnActualLength(station),"GBK");
			promptContent = new String(content,0,StreamTool.returnActualLength(content),"GBk");
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	
	public int getIn_out() {
		return in_out;
	}

	public void setIn_out(int in_out) {
		this.in_out = in_out;
	}

	public int getSerial_number() {
		return serial_number;
	}

	public void setSerial_number(int serial_number) {
		this.serial_number = serial_number;
	}

	public int getStay_time() {
		return stay_time;
	}

	public void setStay_time(int stay_time) {
		this.stay_time = stay_time;
	}

	public byte getReserved() {
		return reserved;
	}

	public void setReserved(byte reserved) {
		this.reserved = reserved;
	}

	public String getCurrentStation() {
		return currentStation;
	}

	public void setCurrentStation(String currentStation) {
		this.currentStation = currentStation;
	}

	public String getPromptContent() {
		return promptContent;
	}

	public void setPromptContent(String promptContent) {
		this.promptContent = promptContent;
	}

	@Override
	public String toString() {
		return "InOutStationReceive [in_out=" + in_out + ", serial_number=" + serial_number + ", stay_time=" + stay_time
				+ ", reserved=" + reserved + ", currentStation=" + currentStation + ", promptContent=" + promptContent
				+ "]";
	}
	
}
