package com.blueveery.springrest2ts.tsmodel;


import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by tomek on 08.08.17.
 */
public class TSEnumConstant extends TSElement {
    private TSEnum owner;

    public TSEnumConstant(String name) {
        super(name);
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        writer.write(getName());
    }

    public void setOwner(TSEnum tsEnum) {
        this.owner = tsEnum;
    }
}
