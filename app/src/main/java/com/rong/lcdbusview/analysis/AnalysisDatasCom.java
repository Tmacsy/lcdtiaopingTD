package com.rong.lcdbusview.analysis;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.rong.lcdbusview.datas.Head;
import com.rong.lcdbusview.datas.InOutStationReceive;
import com.rong.lcdbusview.datas.MessageBody;
import com.rong.lcdbusview.datas.MessageHead;
import com.rong.lcdbusview.datas.MessageType;
import com.rong.lcdbusview.datas.RouteInformation;
import com.rong.lcdbusview.datas.RouteMsg;
import com.rong.lcdbusview.datas.TextShow;
import com.rong.lcdbusview.datas.TimingReceive;
import com.rong.lcdbusview.tools.CRCCheckTools;
import com.rong.lcdbusview.tools.LogTools;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android_serialport_api.SerialPortCom;
import android_serialport_api.SerialPortCom.SerialPortCallBack;

public class AnalysisDatasCom implements SerialPortCallBack {

	private static final String TAG = "AnalysisDatasCom";
	private static volatile AnalysisDatasCom instance;
	private byte[] content;
	private SerialPortCom port;
	private Transmission transmission;
	private ReadDataJoint joint;
//	private ReadDataJointThrean joint;
	private MyHandler mHandler;

	public static AnalysisDatasCom getInstance(Transmission transmission) {
		if (instance != null) {

		} else {
			synchronized (AnalysisDatasCom.class) {
				if (instance == null) {
					instance = new AnalysisDatasCom(transmission);
				}
			}
		}
		return instance;
	}

	public class MyHandler extends Handler {
		public MyHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			switch (what) {
			case ReadDataJoint.WATH_FEEDBACK:
				byte[] datas = (byte[]) msg.obj;
				if (datas != null && datas.length > 0) {
					LogTools.d(TAG, datas);
					analysisData(datas);
//					analysisMsg(datas);
				}
				break;

			default:
				break;
			}
		}
	}

	private AnalysisDatasCom(Transmission transmission) {
		LogTools.d(TAG, "AnalysisDatasCom");
		port = new SerialPortCom();
		this.transmission = transmission;
		HandlerThread handlerThread = new HandlerThread("AnalysisDatasCom");
		handlerThread.start();
		mHandler = new MyHandler(handlerThread.getLooper());
//		joint = ReadDataJointThrean.getInstance(mHandler);
		joint = ReadDataJoint.getInstance(mHandler);
	}

	public synchronized void open(String path, int baudrate, int bits, char event, int stop, int flags,
			int readBufferSize) {
		port.OpenPort(path, baudrate, flags, readBufferSize, this);
	}

	public synchronized void close() {
		port.stopRead();
		port.ClosePort();
	}

	public void sendMsg(byte[] msg) {
		port.writeData(msg);
	}

	public synchronized void startRead() {
		port.startRead();
	}

	@Override
	public void onDataReceived(byte[] buffer, int size) {
		joint.receiveData(buffer);
	}


	private synchronized void analysisData(byte[] datas) {
		LogTools.d(TAG, datas);
		if ((datas[1] & 0xff) != 0xc4) {
			Head head = new Head(datas);
			LogTools.d(TAG, head.toString());
			if (head.getOder() == (int) (0x63 & 0xff) && (head.getId() & 0xff) == 0xB3) {
				TextShow textShow = new TextShow(head.getContent());
				LogTools.d(TAG, textShow.toString());
				transmission.appearText(textShow);
			} else if (head.getOder() == (int) (0x63 & 0xff)) {
				if ((head.getId() & 0xff) == 0xB1 || (head.getId() & 0xff) == 0xB0 || (head.getId() & 0xff) == 0xB2) {
					RouteMsg routeMsg = new RouteMsg(head.getContent());
					LogTools.d(TAG, routeMsg.toString());
					transmission.appearRoute(routeMsg);
				}
			}
		}
	}

	private synchronized void analysisMsg(byte[] datas) {
		if(datas != null && datas.length > 0){
			MessageBody messageBody = new MessageBody(datas);
			MessageHead messageHead = messageBody.getHead();
			if(messageHead != null && messageBody != null){
				feedbackDatas(messageBody.getContent(), messageHead.getType());
			}
		}
	}
	
	private synchronized void groupPackage(byte[] datas) {
		ByteArrayOutputStream stream = null;
		DataOutputStream out = null;
		try {
			stream = new ByteArrayOutputStream();
			out = new DataOutputStream(stream);
			if (content != null && content.length > 0) {
				out.write(content);
			}
			out.write(datas);
			content = stream.toByteArray();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void feedbackDatas(byte[] datas, int type) {
		LogTools.d(TAG, datas);
		LogTools.d(TAG, "type:" + type);
		switch (type) {
		case MessageType.InOutStation:
			InOutStationReceive station = new InOutStationReceive(datas);
			LogTools.d(TAG, station.toString());
			transmission.appearInOutStation(station);
			break;
		case MessageType.RouteSnedMsg:
			RouteInformation route = new RouteInformation(datas);
			transmission.appearRouteMsg(route);
			break;
		case MessageType.TimingSendMsg:
			TimingReceive timing = new TimingReceive(datas);
			transmission.appearTimingMsg(timing);
			break;
		}
	}

	public void sendMessage(byte[] datas, int type) {
		if (datas.length < 1006) {
			MessageHead head = new MessageHead(datas.length, type, CRCCheckTools.Crc16Check(datas, datas.length), 1, 1,
					0, new byte[5]);
			MessageBody body = new MessageBody(head, datas);
			sendMsg(body.getData());
		} else {
			int m = datas.length / 1006;
			int n = datas.length % 1006;
			byte[] b = new byte[1006];
			if (n != 0) {
				m = m + 1;
			}
			for (int i = 0; i < m; i++) {
				if (n != 0) {
					if (i == (m - 1)) {
						System.arraycopy(datas, datas.length - n, b, 0, n);
						MessageHead head = new MessageHead(b.length, type, CRCCheckTools.Crc16Check(b, b.length), m, m,
								0, new byte[5]);
						MessageBody body = new MessageBody(head, b);
						sendMsg(body.getData());
					} else {
						System.arraycopy(datas, i * 1006, b, 0, 1006);
						MessageHead head = new MessageHead(b.length, type, CRCCheckTools.Crc16Check(b, b.length), m,
								i + 1, 0, new byte[5]);
						MessageBody body = new MessageBody(head, b);
						sendMsg(body.getData());
					}
				} else {
					System.arraycopy(datas, i * 1006, b, 0, 1006);
					MessageHead head = new MessageHead(b.length, type, CRCCheckTools.Crc16Check(b, b.length), m, i + 1,
							0, new byte[5]);
					MessageBody body = new MessageBody(head, b);
					sendMsg(body.getData());
				}
			}
		}
	}

}
