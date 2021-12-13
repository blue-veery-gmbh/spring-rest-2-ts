package com.blueveery.springrest2ts.tsmodel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TSLiteralArray implements ILiteral {
    private List<ILiteral> literalList;

    public TSLiteralArray(ILiteral... literals) {
        this.literalList = Arrays.asList(literals);
    }

    public List<ILiteral> getLiteralList() {
        return literalList;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        writer.append("[");
        for (int i = 0; i < literalList.size(); i++) {
            ILiteral l = literalList.get(i);
            l.write(writer);
            if (i < literalList.size()-1) {
                writer.append(", ");
            }
        }
        writer.append("]");
    }
}
