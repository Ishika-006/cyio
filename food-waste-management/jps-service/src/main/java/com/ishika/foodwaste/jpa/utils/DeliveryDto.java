package com.ishika.foodwaste.jpa.utils;



public class DeliveryDto {
    private int fid;
    private String name;

    public DeliveryDto(int fid, String name) {
        this.fid = fid;
        this.name = name;
    }

    // getters & setters
    public int getFid() { return fid; }
    public void setFid(int fid) { this.fid = fid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
