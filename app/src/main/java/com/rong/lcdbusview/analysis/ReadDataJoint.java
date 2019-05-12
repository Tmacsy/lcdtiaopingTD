package com.rong.lcdbusview.analysis;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import com.rong.lcdbusview.tools.CRCCheckTools;
import com.rong.lcdbusview.tools.LogTools;
import com.rong.lcdbusview.tools.StreamTool;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 数据读取工具类
 * 
 * @author rong_pc
 *
 */
public class ReadDataJoint {

	private static final String TAG = "ReadDataJoint";
	private static volatile ReadDataJoint instance;
	private Handler mHandler;// 向上反馈数据的handler
	private Thread mThread;
	private ReadRunnable readRunnable;// 读取数据的线程

	public static ReadDataJoint getInstance(Handler handler) {
		if (instance != null) {

		} else {
			synchronized (ReadDataJoint.class) {
				if (instance == null) {
					instance = new ReadDataJoint(handler);
				}
			}
		}
		return instance;
	}

	private ReadDataJoint(Handler handler) {
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
	private volatile boolean isRunning;// 进行判断数据是否在工作状态的标识
	private byte[] feedbackDatas;// 获取数据的数组

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
				if (isRunning) {
					handState();
				} else {
					synchronized (this) {
						try {
//							LogTools.d(TAG, "wait");
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		private void handState() {
			synchronized (lock) {
				switch (jointState) {
				case Start:// 开始读取数据
					if (saveDatas != null && !saveDatas.isEmpty() && dateLength <= 1024) {
						byte b1 = saveDatas.peek();
						// byte b2 = saveDatas.peek();
						int x1 = b1 & 0xff;
						// int x2 = b2 & 0xff;
						// LogTools.d(TAG, "x1:" + Integer.toHexString(x1));
						if (x1 == (0x55 & 0xff)) {// 判断数据是否是协议头
							dateLength = 0;
							feedbackDatas = null;
							feedbackDatas = new byte[1024];
							if (getJointState() == JointState.Start) {
								feedbackDatas[0] = b1;
								// feedbackDatas[1] = b2;
								saveDatas.poll();
								dateLength = dateLength + 1;
								setJointState(JointState.Donning);
							}
						} else {
							if (getJointState() == JointState.Start) {
								if (dateLength > 0) {
									if (getJointState() == JointState.Start) {
										feedbackDatas[dateLength] = saveDatas.poll();
										dateLength++;
										setJointState(JointState.Donning);
									}
								} else {
									saveDatas.poll();
									if (saveDatas.isEmpty()) {
										isRunning = false;
									} else {
										setJointState(JointState.Start);
									}
								}
							}
						}
					} else {
						if (dateLength > 1024) {
							dateLength = 0;
							feedbackDatas = null;
							feedbackDatas = new byte[1024];
						} else {
							isRunning = false;
						}
					}
					break;
				case Donning:// 进行数据导入操作
					if (dateLength != 0 && dateLength <= 1024) {
						int x1 = feedbackDatas[dateLength - 1] & 0xff;
						// int x2 = feedbackDatas[dateLength - 1] & 0xff;
						byte[] datas1 = new byte[dateLength - 1];
						System.arraycopy(feedbackDatas, 0, datas1, 0, dateLength - 1);
						int b = CRCCheckTools.getVerify(datas1) & 0xff;
						LogTools.d(TAG, "x1:" + Integer.toHexString(x1) + ",j:" + Integer.toHexString(b));
						if (x1 == (b)) {// 判断是否是帧尾
							if (dateLength > 5) {
								if ((feedbackDatas[1] & 0xff) == 0xc4) {
									if (getJointState() == JointState.Donning) {
										dateLength = 0;
										feedbackDatas = null;
										feedbackDatas = new byte[1024];
										if (saveDatas.isEmpty()) {
											isRunning = false;
										} else {
											setJointState(JointState.Start);
										}
									}
								} else {
									if (getJointState() == JointState.Donning) {
										byte[] d = new byte[2];
										System.arraycopy(feedbackDatas, 3, d, 0, 2);
										LogTools.d(TAG, d);
										ByteArrayInputStream stream = new ByteArrayInputStream(d);
										DataInputStream in = new DataInputStream(stream);
										try {
											int l = in.readShort();
											LogTools.d(TAG, "s:" + l);
											LogTools.d(TAG, "length:" + dateLength + ",l:" + l);
											if (dateLength == (l + 6)) {
												setJointState(JointState.Stop);
											} else {
												setJointState(JointState.Start);
											}
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
								}
							} else {
								if (getJointState() == JointState.Donning) {
									setJointState(JointState.Start);
								}
							}

						} else {
							if (getJointState() == JointState.Donning) {
								setJointState(JointState.Start);
							}
						}
					} else {
						if (saveDatas.isEmpty()) {

							isRunning = false;

						} else {
							if (getJointState() == JointState.Donning) {
								setJointState(JointState.Start);
							}
						}
					}
					break;
				case Stop:// 停止导入
					if (dateLength > 0) {
						byte[] datas = new byte[dateLength];
						System.arraycopy(feedbackDatas, 0, datas, 0, dateLength);// 将有效数据拷贝下来
						Message msg = Message.obtain();
						msg.obj = datas;
						msg.what = WATH_FEEDBACK;
						mHandler.sendMessage(msg);
						feedbackDatas = new byte[1024];
						if (saveDatas.isEmpty()) {
							isRunning = false;
						} else {
							setJointState(JointState.Start);
						}
						// } else {
						// LogTools.e(TAG, "接收的数据有误！");
						// }
					} else {
						isRunning = false;
					}
					break;
				}
			}
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
