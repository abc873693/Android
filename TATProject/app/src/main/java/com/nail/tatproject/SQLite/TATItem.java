package com.nail.tatproject.SQLite;

/**
 * Created by 70J on 2016/10/29.
 */
public class TATItem {
    String ProductID;
    long AddTime;
    int Count;
    public TATItem(String i, long time,int c) {
        ProductID = i;
        AddTime = time;
        Count = c;
    }

    public TATItem(String i, long time) {
        ProductID = i;
        AddTime = time;
        Count = 1;
    }

    public TATItem() {
        ProductID = null;
        AddTime = 0;
        Count = 1;
    }

    public String getProductID() {
        return ProductID;
    }

    public long getAddTime() {
        return AddTime;
    }

    public int getCount() {
        return Count;
    }


    public void setProductID(String productID) {
        this.ProductID = productID;
    }

    public void setAddTime(long addTime) {
        this.AddTime = addTime;
    }

    public void setCount(int count) {
        this.Count = count;
    }
}
