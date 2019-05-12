package android_serialport_api;

import com.rong.lcdbusview.tools.LogTools;

public class SerialPortCom {
	private static final String TAG = SerialPort.class.getSimpleName();

	// private int mFd=0;

	private SerialPortCallBack mCallback;
	private ReadThread mReadThread;
	// private int nReadBufferSize=0;
	private boolean bOpenPort = false;
	private int mBaudrate = 0;
	private SerialUtilOld serialUtilOld;

	public interface SerialPortCallBack {
		void onDataReceived(byte[] buffer, int size);
	}

	public boolean isOpen() {
		return bOpenPort;
	}

	/*
	 * 返回值：-1表示打开串口出错；0表示串口已打开；1表示正确打开串口
	 */
	public void OpenPort(String path, int baudrate, int flags, int readBufferSize,
			SerialPortCallBack serialPortCallBack) {
		if (isOpen())
			return;
		serialUtilOld = new SerialUtilOld(path, baudrate, flags);
		mBaudrate = baudrate;
		bOpenPort = true;
		mCallback = serialPortCallBack;
	}

	public void ClosePort() {
		stopRead();

		if (bOpenPort) {
			serialUtilOld.close();
		}

		bOpenPort = false;
		mCallback = null;
	}

	public void startRead() {

		stopRead();

		mReadThread = new ReadThread();
		if (mReadThread != null) {
			mReadThread.start();
		}
	}

	public void stopRead() {
		if (mReadThread != null) {
			mReadThread.interrupt();
		}
		mReadThread = null;
	}

	// buffer：格式为{(byte) 0x91, (byte) 0x81, (byte) 0x8C}
	// 返回值：-1表示出错；>=0表示写入成功
	public void writeData(byte[] buffer) {
		if (!isOpen()) {
			return;
		}
		if (buffer == null || buffer.length == 0) {
			return;
		}
		serialUtilOld.setData(buffer);
	}

	// 返回值：-1表示出错；>=0表示写入成功
	public void writeData(String buffer) {
		if (!isOpen()) {
			return;
		}
		if (buffer == null) {
			return;
		}

		byte[] buf = HexString2Bytes(buffer);
		serialUtilOld.setData(buf);

	}

	private byte[] HexString2Bytes(String src) {

		byte[] tmp = src.getBytes();
		int bytes = tmp.length / 2;

		byte[] ret = new byte[bytes];

		for (int i = 0; i < bytes; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	private byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				byte[] buffer = serialUtilOld.getData();
				if (buffer != null) {
					int size = serialUtilOld.getSize();
					byte[] b = new  byte[size];
					System.arraycopy(buffer, 0, b, 0, size);
					if (size > 0) {
						if (mCallback != null) {
						//	LogTools.d(TAG, b);
							mCallback.onDataReceived(b, size);
						}
					}
				}
			}
		}
	}

	// public void watchDogCtrl(int type){
	// WatchDogCtrl(type);
	// }

}
