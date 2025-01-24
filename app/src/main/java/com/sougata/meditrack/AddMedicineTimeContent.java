package com.sougata.meditrack;

public class AddMedicineTimeContent {
    long time;
    String id;
    int h, m;


    public AddMedicineTimeContent(long time, String id, int h, int m) {
        this.time = time;
        this.id = id;
        this.h = h;
        this.m = m;
    }
}
