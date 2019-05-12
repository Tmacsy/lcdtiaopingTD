package com.rong.lcdbusview.link;
/**
 * 消息类型
 * @author rong_pc
 *
 */
public interface MessageType {

//	/** 定时发送消息 */
//	int TimingSendMsg = 0x1001;
//	/** 导乘屏反馈消息 */
//	int TimingBackMsg = 0x2001;
//	/** 上下行标识 */
	int RouteDirMsg = 0x02;
//	/** 站点类型 */
	int StationsnTypeMsg = 0x03;
//	/** 到离站 */
	int InOutStation = 0x04;
//	/** 报站序号 */
	int InOutStationsn = 0x05;

	//	/** 线路名称 */
	int RouteNameMsg = 0x31;

	//	/** 线路名称 */
	int RouteUpMsg = 0x21;

	//	/** 线路名称 */
	int RouteDownMsg = 0x22;

	//	/** 到离站信息播报 */
	int RouteReportstationMsg = 0x03;

	//	/** 路牌显示 */
	int RouteNamestationMsg = 0x04;

	//	/** 设置站点 */
	int RouteSetStationsMsg = 0x10;
}