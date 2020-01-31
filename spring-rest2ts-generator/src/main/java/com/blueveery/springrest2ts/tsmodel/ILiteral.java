package com.blueveery.springrest2ts.tsmodel;

import java.io.BufferedWriter;
import java.io.IOException;

public interface ILiteral {
    void write(BufferedWriter writer) throws IOException;
}
