package com.rong.lcdbusview.datas;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class TextShow {
	private int property;
	private int namber;
	private int startdian;
	private int startline;
	private int enddian;
	private int endline;
	private int time;
	private int onetime;
	private int color;
	private int showTime;
	private int imgType;
	private int baoli;
	private String content;

	public TextShow(byte[] b) {
		readData(b);
	}

	private void readData(byte[] data) {
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		DataInputStream in = new DataInputStream(stream);
		try {
			property = in.readByte();
			namber = in.readByte();
			startdian = in.readByte();
			startline = in.readByte();
			enddian = in.readByte();
			endline = in.readByte();
			time = in.readByte();
			onetime = in.readByte();
			color = in.readByte();
			showTime = in.readByte();
			imgType = in.readByte();
			baoli = in.readByte();
			byte[] b = new byte[data.length - 12];
			in.read(b);
			content = new String(b, "GBK");
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

	public int getProperty() {
		return property;
	}

	public void setProperty(int property) {
		this.property = property;
	}

	public int getNamber() {
		return namber;
	}

	public void setNamber(int namber) {
		this.namber = namber;
	}

	public int getStartdian() {
		return startdian;
	}

	public void setStartdian(int startdian) {
		this.startdian = startdian;
	}

	public int getStartline() {
		return startline;
	}

	public void setStartline(int startline) {
		this.startline = startline;
	}

	public int getEnddian() {
		return enddian;
	}

	public void setEnddian(int enddian) {
		this.enddian = enddian;
	}

	public int getEndline() {
		return endline;
	}

	public void setEndline(int endline) {
		this.endline = endline;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getOnetime() {
		return onetime;
	}

	public void setOnetime(int onetime) {
		this.onetime = onetime;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getShowTime() {
		return showTime;
	}

	public void setShowTime(int showTime) {
		this.showTime = showTime;
	}

	public int getImgType() {
		return imgType;
	}

	public void setImgType(int imgType) {
		this.imgType = imgType;
	}

	public int getBaoli() {
		return baoli;
	}

	public void setBaoli(int baoli) {
		this.baoli = baoli;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "TextShow [property=" + property + ", namber=" + namber + ", startdian=" + startdian + ", startline="
				+ startline + ", enddian=" + enddian + ", endline=" + endline + ", time=" + time + ", onetime="
				+ onetime + ", color=" + color + ", showTime=" + showTime + ", imgType=" + imgType + ", baoli=" + baoli
				+ ", content=" + content + "]";
	}
	

}
