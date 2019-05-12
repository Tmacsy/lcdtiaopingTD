package com.rong.lcdbusview.datas;

import java.util.Objects;

public class AdvertisementData {
    private String adId;
    private String name;
    private int type;
    private String url;
    private int orderNo;
    private int times;
    private long size;
    private String checkValue;
    private String beginTime;
    private String endTime;
    private String savePath;

    public AdvertisementData() {
    }

    public AdvertisementData(String adId, String name, int type, String url, int orderNo, int times,
                             long size, String checkValue, String beginTime, String endTime, String savePath) {
        this.adId = adId;
        this.name = name;
        this.type = type;
        this.url = url;
        this.orderNo = orderNo;
        this.times = times;
        this.size = size;
        this.checkValue = checkValue;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.savePath = savePath;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getCheckValue() {
        return checkValue;
    }

    public void setCheckValue(String checkValue) {
        this.checkValue = checkValue;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    @Override
    public String toString() {
        return "AdvertisementData{" +
                "adId='" + adId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", url='" + url + '\'' +
                ", orderNo=" + orderNo +
                ", times=" + times +
                ", size=" + size +
                ", checkValue='" + checkValue + '\'' +
                ", beginTime='" + beginTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", savePath='" + savePath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdvertisementData)) return false;
        AdvertisementData that = (AdvertisementData) o;
        return Objects.equals(getAdId(), that.getAdId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAdId());
    }
}
