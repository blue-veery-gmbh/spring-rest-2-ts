package com.blueveery.springrest2ts.tsmodel;


import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by tomaszw on 30.07.2017.
 */
public abstract class TSElement implements Comparable<TSElement>{
    private String name;

    public TSElement(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        TSElement otherTsElement = (TSElement) object;
        return name.equals(otherTsElement.name);
    }

    @Override
    public int compareTo(TSElement otherTsElement) {
        return name.compareTo(otherTsElement.name);
    }

    public abstract void write(BufferedWriter writer) throws IOException;
}
