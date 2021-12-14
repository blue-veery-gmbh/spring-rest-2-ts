package com.blueveery.springrest2ts.tsmodel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TSLiteralArray that = (TSLiteralArray) o;
        return Objects.equals(literalList, that.literalList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literalList);
    }

    @Override
    public String toString() {
        return Objects.toString(literalList);
    }
}
