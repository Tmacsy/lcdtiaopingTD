package com.rong.lcdbusview.link;

import com.rong.lcdbusview.tools.LogTools;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

public class MassageContent {
	private int head;
	private int addr;
	private int length;
	private int cmd;
	private byte[] content;
	private int suml;
	private int sumh;
	private int end;
	
	
	public MassageContent(byte[] buffer) {
		super();
		readByte(buffer);
	}
	
	private void readByte(byte[] buffer){
		ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
		DataInputStream in = new DataInputStream(stream);
		try {
			head = in.readUnsignedByte();
			addr = in.readUnsignedByte();
			int yaddr = in.readUnsignedByte();
			cmd = in.readUnsignedByte();
			int msgsnl = in.readUnsignedByte();
			int lenh = in.readUnsignedByte();
			int lenl = in.readUnsignedByte();
			length = (int)((lenh<<8) & 0xff00)+(int)(lenl & 0xff);
		//	LogTools.e("MassageContent", "length:"+length + ",lenh:"+Integer.toHexString(lenh & 0xff)+"lenl:"+Integer.toHexString(lenl & 0xff));
			content = new byte[length];
			in.read(content);
		//	LogTools.d("MassageContent", content);
			suml = in.readUnsignedByte();
			end = in.readUnsignedByte();
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

	public int getHead() {
		return head;
	}

	public void setHead(int head) {
		this.head = head;
	}

	public int getAddr() {
		return addr;
	}

	public void setAddr(int addr) {
		this.addr = addr;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public int getSuml() {
		return suml;
	}

	public void setSuml(int suml) {
		this.suml = suml;
	}

	public int getSumh() {
		return sumh;
	}

	public void setSumh(int sumh) {
		this.sumh = sumh;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	@Override
	public String toString() {
		return "MassageContent [head=" + head + ", addr=" + addr + ", length=" + length + ", cmd=" + cmd + ", content="
				+ Arrays.toString(content) + ", suml=" + suml + ", sumh=" + sumh + ", end=" + end + "]";
	}
	
	
	
	

}
