package com.rong.lcdbusview.analysis;

import com.rong.lcdbusview.datas.InOutStationReceive;
import com.rong.lcdbusview.datas.RouteInformation;
import com.rong.lcdbusview.datas.RouteMsg;
import com.rong.lcdbusview.datas.TextShow;
import com.rong.lcdbusview.datas.TimingReceive;

public interface Transmission {
	void appearTimingMsg(TimingReceive timingReceive);
	void appearRouteMsg(RouteInformation route);
	void appearInOutStation(InOutStationReceive station);
	void appearText(TextShow textShow);
	void appearRoute(RouteMsg routeMsg);
}
