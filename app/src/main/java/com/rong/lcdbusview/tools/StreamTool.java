package com.rong.lcdbusview.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class StreamTool {

	
	private StreamTool(){
		/** 保证次类不被实例化 */
	}
	/**
	 * @方法功能 InputStream 转为 byte
	 * @param InputStream
	 * @return 字节数组
	 * @throws Exception
	 */
	public static byte[] inputStream2Byte(InputStream inStream) throws Exception {
		// ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		// byte[] buffer = new byte[1024];
		// int len = -1;
		// while ((len = inStream.read(buffer)) != -1) {
		// outSteam.write(buffer, 0, len);
		// }
		// outSteam.close();
		// inStream.close();
		// return outSteam.toByteArray();
		int count = 0;
		while (count == 0) {
			count = inStream.available();
		}
		byte[] b = new byte[count];
		inStream.read(b);
		return b;
	}

	/**
	 * @方法功能 byte 转为 InputStream
	 * @param 字节数组
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream byte2InputStream(byte[] b) throws Exception {
		InputStream is = new ByteArrayInputStream(b);
		return is;
	}

	/**
	 * @功能 短整型与字节的转换
	 * @param 短整型
	 * @return 两位的字节数组
	 */
	public static byte[] shortToByte(short number) {
		int temp = number;
		byte[] b = new byte[2];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
		return b;
	}

	/**
	 * @功能 字节的转换与短整型
	 * @param 两位的字节数组
	 * @return 短整型
	 */
	public static short byteToShort(byte[] b) {
		short s = 0;
		short s0 = (short) (b[0] & 0xff);// 最低位
		short s1 = (short) (b[1] & 0xff);
		s1 <<= 8;
		s = (short) (s0 | s1);
		return s;
	}

	/**
	 * @方法功能 整型与字节数组的转换
	 * @param 整型
	 * @return 四位的字节数组
	 */
	public static byte[] intToByte(int i) {
		byte[] bt = new byte[4];
		bt[0] = (byte) (0xff & i);
		bt[1] = (byte) ((0xff00 & i) >> 8);
		bt[2] = (byte) ((0xff0000 & i) >> 16);
		bt[3] = (byte) ((0xff000000 & i) >> 24);
		return bt;
	}

	/**
	 * @方法功能 字节数组和整型的转换
	 * @param 字节数组
	 * @return 整型
	 */
	public static int bytesToInt(byte[] bytes) {
		int num = bytes[0] & 0xFF;
		num |= ((bytes[1] << 8) & 0xFF00);
		num |= ((bytes[2] << 16) & 0xFF0000);
		num |= ((bytes[3] << 24) & 0xFF000000);
		return num;
	}

	/**
	 * @方法功能 字节数组和长整型的转换
	 * @param 字节数组
	 * @return 长整型
	 */
	public static byte[] longToByte(long number) {
		long temp = number;
		byte[] b = new byte[8];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Long(temp & 0xff).byteValue();
			// 将最低位保存在最低位
			temp = temp >> 8;
			// 向右移8位
		}
		return b;
	}

	/** 
     * @方法功能 字节数组和长整型的转换 
     * @param 字节数组 
     * @return 长整型 
     */  
    public static long byteToLong(byte[] b) {  
       long s = 0;  
        long s0 = b[0] & 0xff;// 最低位  
        long s1 = b[1] & 0xff;  
        long s2 = b[2] & 0xff;  
       long s3 = b[3] & 0xff;  
        long s4 = b[4] & 0xff;// 最低位  
        long s5 = b[5] & 0xff;  
        long s6 = b[6] & 0xff;  
        long s7 = b[7] & 0xff; // s0不变  
        s1 <<= 8;  
        s2 <<= 16;  
       s3 <<= 24;  
        s4 <<= 8 * 4;  
       s5 <<= 8 * 5;  
       s6 <<= 8 * 6;  
        s7 <<= 8 * 7;  
       s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;  
        return s;  
    }
    
    public static int returnActualLength(byte[] data){
    	int i = 0; 
    	for(;i<data.length;i++){
    		if((data[i]&0xff) == 0){
    			break;
    		}
    	}
    	return i;
    }
    
}
