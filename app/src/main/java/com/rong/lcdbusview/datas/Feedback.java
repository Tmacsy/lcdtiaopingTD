package com.rong.lcdbusview.datas;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Feedback {
	private int dataState;
	private byte[] reserved;
	
	public Feedback() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Feedback(int dataState, byte[] reserved) {
		super();
		this.dataState = dataState;
		this.reserved = reserved;
	}

	public int getDataState() {
		return dataState;
	}

	public void setDataState(int dataState) {
		this.dataState = dataState;
	}

	public byte[] getReserved() {
		return reserved;
	}

	public void setReserved(byte[] reserved) {
		this.reserved = reserved;
	}
	
	public byte[] getData(){
		byte[] data = null;
		ByteArrayOutputStream stream = null;
		DataOutputStream out = null;
		try {
			stream = new ByteArrayOutputStream();
			out = new DataOutputStream(stream);
			out.writeByte(dataState);
			if(reserved != null){
				out.write(reserved);
			}
			data = stream.toByteArray();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(out != null){
					out.close();
				}
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	@Override
	public String toString() {
		return "Feedback [dataState=" + dataState + ", reserved=" + Arrays.toString(reserved) + "]";
	}
	
	
	
}
