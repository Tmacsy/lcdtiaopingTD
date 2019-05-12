package com.rong.lcdbusview.datas;
/**
 * 消息类型
 * @author rong_pc
 *
 */
public interface MessageType {

	/** 定时发送消息 */
	int TimingSendMsg = 0x1001;
	/** 导乘屏反馈消息 */
	int TimingBackMsg = 0x2001;
	/** 完整线路内容消息 */
	int RouteSnedMsg = 0x1002;
	/** 完整线路内容消息反馈 */
	int RouteBackMsg = 0x2002;
	/** 进出站消息 */
	int InOutStation = 0x1003;
	/** 进出站消息反馈 */
	int InOutStationBack = 0x2003;
}
