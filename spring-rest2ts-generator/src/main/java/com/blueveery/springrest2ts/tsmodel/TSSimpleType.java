package com.blueveery.springrest2ts.tsmodel;


import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class TSSimpleType extends TSType {
    public TSSimpleType(String name) {
        super(name);
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        writer.write(": "+getName());
    }

}
