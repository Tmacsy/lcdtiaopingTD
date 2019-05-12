package com.rong.lcdbusview.datas;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

public class Head {
	private int id;
	private int oder;
	private int lenght;
	private byte[] content;
	
	public Head(byte[] b) {
		readData(b);
	}
	
	private void readData(byte[] data) {
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		DataInputStream in = new DataInputStream(stream);
		try {
			in.readByte();
			id = in.readByte();
			if((id&0xff) != 0xc4){
			oder = in.readByte();
			lenght = in.readShort();
			content = new byte[lenght];
			in.read(content);
			in.readByte();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOder() {
		return oder;
	}

	public void setOder(int oder) {
		this.oder = oder;
	}

	public int getLenght() {
		return lenght;
	}

	public void setLenght(int lenght) {
		this.lenght = lenght;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Head [id=" + id + ", oder=" + oder + ", lenght=" + lenght + ", content=" + Arrays.toString(content)
				+ "]";
	}
	
	
}

