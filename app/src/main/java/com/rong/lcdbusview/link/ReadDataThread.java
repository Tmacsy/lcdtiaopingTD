package com.rong.lcdbusview.link;

import java.util.LinkedList;
import java.util.Queue;
 import java.util.Arrays;
import com.rong.lcdbusview.tools.LogTools;

import android.os.Handler;
import android.os.Message;

/**
 * 数据读取工具类
 * 
 * @author rong_pc
 *
 */
public class ReadDataThread {

	private static final String TAG = "ReadDataJoint";
	private static volatile ReadDataThread instance;
	private Handler mHandler;// 向上反馈数据的handler
	private Thread mThread;
	private ReadRunnable readRunnable;// 读取数据的线程

	public static ReadDataThread getInstance(Handler handler) {
		if (instance != null) {

		} else {
			synchronized (ReadDataThread.class) {
				if (instance == null) {
					instance = new ReadDataThread(handler);
				}
			}
		}
		return instance;
	}

	private ReadDataThread(Handler handler) {
		mHandler = handler;
		saveDatas = new LinkedList<Byte>();
		lock = new Object();
		feedbackDatas = new byte[1204];
		setJointState(JointState.Start);
		isRunning = false;
		readRunnable = new ReadRunnable();
		mThread = new Thread(readRunnable);
		mThread.setPriority(Thread.MAX_PRIORITY);
		mThread.setName("ReadThread");
		mThread.start();

	}

	public static final int WATH_FEEDBACK = 0x01;
	private volatile Queue<Byte> saveDatas = null;// 存储数据的栈
	private int dateLength = 0;// 记录字节的长度
	private Object lock;// 操作锁
	private volatile boolean isRunning = true;// 进行判断数据是否在工作状态的标识
	private byte[] feedbackDatas;// 获取数据的数组
	private int length = -1 ;

	private class ReadRunnable implements Runnable {
		public synchronized void go() {
			notifyAll();
		}

		@Override
		public void run() {
			if (Thread.interrupted()) {
				return;
			}
			while (true) {
				if (isRunning && !saveDatas.isEmpty()) {
					handState();
				} else {
					synchronized (this) {
						try {
							//LogTools.d(TAG, "wait");
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		private void handState() {
			isRunning = false;
			if(saveDatas != null && !saveDatas.isEmpty()){
				byte b = saveDatas.peek();
			//	LogTools.e(TAG, ""+Integer.toHexString(b & 0xff));
				synchronized (lock) {
					if(dateLength == 0)
					{
						feedbackDatas = new byte[1204];
					}
					feedbackDatas[dateLength] = b;
					dateLength++;
					if(dateLength >= 1204)
					{
						dateLength = 0;
					}
					saveDatas.poll();
				}
			}
			if(saveDatas.isEmpty())
			{
				if(dateLength > 0)
				{
					int i;
					byte[] rev = new byte[dateLength];
					System.arraycopy(feedbackDatas, 0, rev, 0, dateLength);// 将有效数据拷贝下来
					LogTools.d(TAG, rev);
					if(dateLength > 9)
					{
						for(i = 0; i < dateLength;i ++)
						{
							//LogTools.e(TAG, "length:"+","+Integer.toHexString(feedbackDatas[i] & 0xff));
							if((feedbackDatas[i] & 0xff) == 0x7E)
							{
								int bufsize;
								bufsize = (int)((feedbackDatas[i+5]<<8) & 0xff00)+(int)(feedbackDatas[i+6] & 0xff);
								LogTools.e(TAG, "length:"+bufsize + ",len:"+Integer.toHexString(feedbackDatas[i+6] & 0xff)+"dateLength:"+dateLength);
								if((bufsize + i + 9)> dateLength)
								{
									LogTools.e(TAG, "bufsize:"+(bufsize + i )+",dateLength:"+dateLength);
									break;
								}
								if((feedbackDatas[bufsize+i+8] & 0xff) != 0x7F)
								{
									dateLength = 0;
									LogTools.e(TAG, "break end:"+Integer.toHexString(feedbackDatas[bufsize+i+8] & 0xff));
									break;
								}
								else
								{
									byte[] datas = new byte[bufsize+9];
									System.arraycopy(feedbackDatas, i, datas, 0, bufsize+9);// 将有效数据拷贝下来
									LogTools.d(TAG,"WATH_FEEDBACK");
									LogTools.d(TAG,datas);
									Message msg = Message.obtain();
									msg.obj = datas;
									msg.what = WATH_FEEDBACK;
									mHandler.sendMessage(msg);
									System.arraycopy(feedbackDatas, bufsize+9+i, feedbackDatas, 0, dateLength - (bufsize+9+i));// 将有效数据拷贝下来
									dateLength -= bufsize+9+i;
									//LogTools.d(TAG,"dateLength:"+dateLength+","+feedbackDatas);
								}
							}
						}
					}
				}
			}
			isRunning = true;
		}

	}
	private JointState jointState;

	private enum JointState {
		Start, Donning, Stop
	}

	public void setJointState(JointState jointState) {
		this.jointState = jointState;
		LogTools.d(TAG, "当前状态：" + jointState.name());
	}

	public JointState getJointState() {
		return jointState;
	}

	public synchronized void receiveData(byte[] msg) {// 存放数据的操作方法

		if (msg != null && msg.length > 0) {
			synchronized (lock) {
				for (byte b : msg) {
					saveDatas.add(b);
				}
				isRunning = true;
				readRunnable.go();
			}
		}

	}
}
