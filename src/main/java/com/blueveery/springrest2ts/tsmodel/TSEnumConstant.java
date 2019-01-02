package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.GenerationContext;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by tomek on 08.08.17.
 */
public class TSEnumConstant extends TSElement {
    public TSEnumConstant(String name) {
        super(name);
    }

    @Override
    public void write(GenerationContext generationContext, BufferedWriter writer) throws IOException {
        writer.write(getName());
    }
}
