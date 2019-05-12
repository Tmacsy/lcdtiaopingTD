package com.rong.lcdbusview.analysis;

import java.util.LinkedList;
import java.util.Queue;

import com.rong.lcdbusview.tools.LogTools;

import android.os.Handler;
import android.os.Message;

/**
 * 数据读取工具类
 * 
 * @author rong_pc
 *
 */
public class ReadDataJointThrean {

	private static final String TAG = "ReadDataJoint";
	private static volatile ReadDataJointThrean instance;
	private Handler mHandler;// 向上反馈数据的handler
	private Thread mThread;
	private ReadRunnable readRunnable;// 读取数据的线程

	public static ReadDataJointThrean getInstance(Handler handler) {
		if (instance != null) {

		} else {
			synchronized (ReadDataJointThrean.class) {
				if (instance == null) {
					instance = new ReadDataJointThrean(handler);
				}
			}
		}
		return instance;
	}

	private ReadDataJointThrean(Handler handler) {
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
							LogTools.d(TAG, "wait");
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
				synchronized (lock) {
					switch (jointState) {
					case Start:
						if (dateLength == 0) {
							if (b == 0x55) {
								feedbackDatas = new byte[1024];
								feedbackDatas[dateLength++] = b;
								saveDatas.poll();
								setJointState(JointState.Start);
							}
						}else if(dateLength == 1){
							if(b == 0xAA){
								feedbackDatas[dateLength++] = b;
								saveDatas.poll();
								setJointState(JointState.Donning);
							}else{
								dateLength = 0;
								saveDatas.poll();
								feedbackDatas = null;
								setJointState(JointState.Start);
							}
						}
						break;
					case Donning:
						if(dateLength < 4){
							feedbackDatas[dateLength++] = b;
							saveDatas.poll();
							setJointState(JointState.Donning);
						}else{
							if(length == -1){
								byte[] size = new byte[2];
								size[0] = feedbackDatas[2];
								size[1] = feedbackDatas[3];
								length = (int) ((size[0] & 0xFF) | ((size[1] & 0xFF)<<8));
								setJointState(JointState.Donning);
							}else{
								if(dateLength < length){
									feedbackDatas[dateLength++] = b;
									saveDatas.poll();
									setJointState(JointState.Donning);
								}else{
									byte b1 = feedbackDatas [dateLength -2];
									byte b2 = feedbackDatas [dateLength -1];
									if(b1 == 0x66 && b2 == 0xBB){
										byte[] datas = new byte[dateLength];
										System.arraycopy(feedbackDatas, 0, datas, 0, dateLength);// 将有效数据拷贝下来
										Message msg = Message.obtain();
										msg.obj = datas;
										msg.what = WATH_FEEDBACK;
										mHandler.sendMessage(msg);
										dateLength = 0;
										feedbackDatas = new byte[1024];
										setJointState(JointState.Start);
									}else{
										dateLength = 0;
										feedbackDatas = new byte[1024];
										setJointState(JointState.Start);
									}
								}
							}
						}
						break;
					case Stop:
						dateLength = 0;
						break;
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
