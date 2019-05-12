package com.rong.lcdbusview.datas;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 消息头
 * 
 * @author rong_pc
 *
 */
public class MessageHead {
	private static final int FH = 0x55AA;
	private int length;
	private int type;
	private short CRCCheck;
	private int frameAll;
	private int franmeNamber;
	private int equipment;
	private byte[] retain;

	public MessageHead(byte[] data) {
		super();
		readData(data);
	}

	public MessageHead(int length, int type, short cRCCheck, int frameAll, int franmeNamber, int equipment,
			byte[] retain) {
		super();
		this.length = length;
		this.type = type;
		CRCCheck = cRCCheck;
		this.frameAll = frameAll;
		this.franmeNamber = franmeNamber;
		this.equipment = equipment;
		this.retain = retain;
	}

	private void readData(byte[] data) {
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		DataInputStream in = new DataInputStream(stream);
		try {
			in.readShort();
			length = in.readShort();
			type = in.readShort();
			CRCCheck = in.readShort();
			frameAll = in.readByte();
			franmeNamber = in.readByte();
			equipment = in.readByte();
			in.readFully(retain);
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

	public byte[] getData() {
		ByteArrayOutputStream stream = null;
		DataOutputStream out = null;
		byte[] data = null;
		retain = new byte[5];
		try {
			stream = new ByteArrayOutputStream();
			out = new DataOutputStream(stream);
			out.writeShort(FH);
			out.writeShort(length);
			out.writeShort(type);
			out.writeShort(CRCCheck);
			out.writeByte(frameAll);
			out.writeByte(franmeNamber);
			out.writeByte(equipment);
			out.write(retain);
			data = stream.toByteArray();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public int getLength() {
		return length;
	}

	public int getType() {
		return type;
	}

	public int getCRCCheck() {
		return CRCCheck;
	}

	public int getFrameAll() {
		return frameAll;
	}

	public int getFranmeNamber() {
		return franmeNamber;
	}

	public int getEquipment() {
		return equipment;
	}

	public byte[] getRetain() {
		return retain;
	}

	@Override
	public String toString() {
		return "MassgeHead [length=" + length + ", type=" + type + ", CRCCheck=" + CRCCheck + ", frameAll=" + frameAll
				+ ", franmeNamber=" + franmeNamber + ", equipment=" + equipment + ", retain=" + retain + "]";
	}

}
