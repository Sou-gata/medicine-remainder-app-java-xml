package com.sougata.meditrack;

public class HomeMedicineListItem {
    int image, numOfDose, id, repeat;
    String name;

    public HomeMedicineListItem(int image, int numOfDose, String name, int id, int repeat) {
        this.image = image;
        this.numOfDose = numOfDose;
        this.name = name;
        this.id = id;
        this.repeat = repeat;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getNumOfDose() {
        return numOfDose;
    }

    public void setNumOfDose(int numOfDose) {
        this.numOfDose = numOfDose;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }
}
