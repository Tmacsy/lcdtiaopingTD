package com.rong.lcdbusview.link;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.rong.lcdbusview.tools.LogTools;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android_serialport_api.SerialPortCom;
import android_serialport_api.SerialPortCom.SerialPortCallBack;

public class AnalysisDatasManage implements SerialPortCallBack {

	private static final String TAG = "AnalysisDatasManage";
	private static volatile AnalysisDatasManage instance;
	private byte[] content;
	private SerialPortCom port;
	private TransmissionCallback mTransmissionCallback;
	private ReadDataThread mDataThread;
	private MyHandler mHandler;
	private byte[] revcont;
	private int revlen = 0;

	public static AnalysisDatasManage getInstance(TransmissionCallback transmission) {
		if (instance != null) {

		} else {
			synchronized (AnalysisDatasManage.class) {
				if (instance == null) {
					instance = new AnalysisDatasManage(transmission);
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
			case ReadDataThread.WATH_FEEDBACK:
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

	private AnalysisDatasManage(TransmissionCallback transmission) {
		LogTools.d(TAG, "AnalysisDatasManage");
		port = new SerialPortCom();
		this.mTransmissionCallback = transmission;
		HandlerThread handlerThread = new HandlerThread("AnalysisDatasManage");
		handlerThread.start();
		mHandler = new MyHandler(handlerThread.getLooper());
//		joint = ReadDataJointThrean.getInstance(mHandler);
		mDataThread = ReadDataThread.getInstance(mHandler);
		revcont = new byte[2048];
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
		mDataThread.receiveData(buffer);
	}

	private synchronized void analysisData(byte[] datas) {
		MassageContent content = new MassageContent(datas);
		LogTools.d(TAG, "getCmd:"+Integer.toHexString(content.getCmd() & 0xff));
		if(content.getContent() != null && content.getContent().length > 0){
			switch (content.getCmd()) {
			case MessageType.RouteReportstationMsg: {
				int i = 0;
				int msglens = content.getContent().length;
				byte[] msgdata = new byte[content.getContent().length];
				System.arraycopy(content.getContent(), 0, msgdata, 0, msglens);// 将有效数据拷贝下来
				int dirState = 0;
				int inoutState = 0;
				int curnum = 0;
				while (i < msglens) {
					int msgtype = 0xff & msgdata[i];
					i++;
					int msglen = (int) ((msgdata[i] << 8) & 0xff00) + (int) (msgdata[i + 1] & 0xff);
					i += 2;
					byte[] onedata = new byte[msglen];
					System.arraycopy(msgdata, i, onedata, 0, msglen);// 将有效数据拷贝下来
					i += msglen;
					LogTools.d(TAG, "msgtype:" + msgtype + "msglen:" + msglen);
					LogTools.d(TAG, onedata);
					switch (msgtype) {
						case MessageType.RouteDirMsg:
							dirState = (0xff & onedata[0]) - 1;
							break;

						case MessageType.InOutStation:
							inoutState = (0xff & onedata[0]) - 1;
							break;

						case MessageType.InOutStationsn:
							curnum = (0xff & onedata[0]) - 1;
							break;
						default:
							break;
					}
				}
				LogTools.d(TAG, "上下行状态:" + dirState + "进出站状态:" + inoutState + "当前站序号:" + curnum);
				if (mTransmissionCallback != null) {
					mTransmissionCallback.onNatifyDirstatus(dirState, inoutState, curnum);
				}
			}
			break;
				case MessageType.RouteNamestationMsg: {
					int i = 0;
					int msglens = content.getContent().length;
					byte[] msgdata = new byte[content.getContent().length];
					System.arraycopy(content.getContent(), 0, msgdata, 0, msglens);// 将有效数据拷贝下来
					String routename = " ";
					while (i < msglens) {
						int msgtype = 0xff & msgdata[i];
						i++;
						int msglen = (int) ((msgdata[i] << 8) & 0xff00) + (int) (msgdata[i + 1] & 0xff);
						i += 2;
						byte[] onedata = new byte[msglen];
						System.arraycopy(msgdata, i, onedata, 0, msglen);// 将有效数据拷贝下来
						i += msglen;
						LogTools.d(TAG, "msgtype:" + msgtype + "msglen:" + msglen);
						LogTools.d(TAG, onedata);
						switch (msgtype) {
							case MessageType.RouteNameMsg: {
								try {
									byte[] rtname = new byte[msglen];
									System.arraycopy(onedata, 0, rtname, 0, msglen);// 将有效数据拷贝下来
									String linename = new String(rtname, "GBK");
									routename = linename.replace("路", "");
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							break;
							default:
								break;
						}
					}
					LogTools.d(TAG, "线路名称:" + routename);

					if (mTransmissionCallback != null) {
						mTransmissionCallback.onNatifyRoutename(routename);
					}
				}
				break;
				case MessageType.RouteSetStationsMsg: {
					int i = 0;
					int msglens = content.getContent().length;
					byte[] msgdata = new byte[content.getContent().length];
					System.arraycopy(content.getContent(), 0, msgdata, 0, msglens);// 将有效数据拷贝下来
					String routename = " ";
					while (i < msglens) {
						int msgtype = 0xff & msgdata[i];
						i++;
						int msglen = (int) ((msgdata[i] << 8) & 0xff00) + (int) (msgdata[i + 1] & 0xff);
						i += 2;
						byte[] onedata = new byte[msglen];
						System.arraycopy(msgdata, i, onedata, 0, msglen);// 将有效数据拷贝下来
						i += msglen;
						LogTools.d(TAG, "msgtype:" + msgtype + "msglen:" + msglen);
						LogTools.d(TAG, onedata);
						switch (msgtype) {
							case MessageType.RouteUpMsg: {
									RouteStationMsg SstationMsg = new  RouteStationMsg(onedata);
									LogTools.d(TAG, SstationMsg.toString());
									if (mTransmissionCallback != null) {
										mTransmissionCallback.onStationMsg(SstationMsg,0);
									}
							}
							break;
							case MessageType.RouteDownMsg: {
								RouteStationMsg SstationMsg = new  RouteStationMsg(onedata);
								LogTools.d(TAG, SstationMsg.toString());
								if (mTransmissionCallback != null) {
										mTransmissionCallback.onStationMsg(SstationMsg,1);
									}
							}
							break;
							default:
								break;
						}
					}
				}
				break;
				default:
					break;
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


}
