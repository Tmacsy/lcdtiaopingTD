package com.rong.lcdbusview.link;



public interface TransmissionCallback {
	void onNatifyStationInOut(NatifyStationMsg natifyStationMsg);
	void onNatifyRoutename(String name);
	void onStationMsg(RouteStationMsg stationMsg,int dir);
	void onNatifyDirstatus( int dir,int status,int cur);

}
