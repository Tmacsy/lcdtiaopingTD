package com.rong.lcdbusview.datas;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class MessageBody {

	private MessageHead head;
	private byte[] content;
	private static final int FT = 0x66BB;
	
	
	
	public MessageBody(byte[] data) {
		super();
		readData(data);
	}

	public MessageBody(MessageHead head, byte[] content) {
		super();
		this.head = head;
		this.content = content;
	}

	public MessageHead getHead() {
		return head;
	}
	
	public byte[] getContent() {
		return content;
	}
	
	private void readData(byte[] data){
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		
		byte[] headData = new byte[12];
		DataInputStream in = new DataInputStream(stream);
		try {
			in.read(headData,0,12);
			head = new MessageHead(headData);
			content = new byte[head.getLength()];
			in.read(content,0,head.getLength());
			in.readShort();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			
			try {
				if(stream != null){
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
	
	public byte[] getData(){
		byte[] data = null;
		ByteArrayOutputStream stream = null;
		DataOutputStream out = null;
		try {
			stream = new ByteArrayOutputStream();
			out = new DataOutputStream(stream);
			out.write(head.getData());
			out.write(content);
			out.writeShort(FT);
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
		return "MassageBody [head=" + head + ", content=" + Arrays.toString(content) + "]";
	}
	
}
