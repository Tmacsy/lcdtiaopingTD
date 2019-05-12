package com.rong.lcdbusview.link;

import com.rong.lcdbusview.tools.LogTools;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class  NatifyStationMsg {

	private int inoutState;//到站：0 离站：1
	private int currentStationNum;//当前站序
	private int routeType;//上行：0 下行：1
	private int stationSumNum;//站点总数
	private String content;//报站内容
	
	
	
	public NatifyStationMsg(byte[] buffer) {
		super();
		readByte(buffer);
	}

	private void readByte(byte[] buffer){
		ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
		DataInputStream in = new DataInputStream(stream);
		try {
			int b1 = in.readUnsignedByte();
			inoutState = ((b1 >> 7) & 0x1);
			currentStationNum = (b1 & 0x7F);
			int b2 = in.readUnsignedByte();
			routeType = ((b2 >> 7) & 0x1);
			stationSumNum = (b2 & 0x7F);
			LogTools.d("NatifyStationMsg","inoutState:"+Integer.toHexString(b1 & 0xff)+","+inoutState+",currentStationNum:"+currentStationNum+",routeType:"+Integer.toHexString(b2 & 0xff)+","+routeType+",stationSumNum:"+stationSumNum);
			int length = in.available();
			byte[] src = new byte[length];
			in.read(src);
			content = new String(src,"GBK");
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
	
	public int getInoutState() {
		return inoutState;
	}
	public void setInoutState(int inoutState) {
		this.inoutState = inoutState;
	}
	public int getCurrentStationNum() {
		return currentStationNum;
	}
	public void setCurrentStationNum(int currentStationNum) {
		this.currentStationNum = currentStationNum;
	}
	public int getRouteType() {
		return routeType;
	}
	public void setRouteType(int routeType) {
		this.routeType = routeType;
	}
	public int getStationSumNum() {
		return stationSumNum;
	}
	public void setStationSumNum(int stationSumNum) {
		this.stationSumNum = stationSumNum;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "NatifyStationMsg [inoutState=" + inoutState + ", currentStationNum=" + currentStationNum
				+ ", routeType=" + routeType + ", stationSumNum=" + stationSumNum + ", content=" + content + "]";
	}
	
	
	
}
