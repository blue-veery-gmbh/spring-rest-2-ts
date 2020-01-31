package com.blueveery.springrest2ts.tsmodel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class TSJsonLiteral extends TSElement implements ILiteral{
    SortedMap<String, ILiteral> fieldMap = new TreeMap<>();

    public TSJsonLiteral() {
        super("");
    }

    public Map<String, ILiteral> getFieldMap() {
        return fieldMap;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        writer.write("{");
        boolean isFirstField = true;
        for (String fieldName : fieldMap.keySet()) {
            ILiteral literal = fieldMap.get(fieldName);
            if (!isFirstField) {
                writer.write(",");
            }
            isFirstField = false;
            writer.newLine();
            writer.write(fieldName);
            writer.write(":");
            literal.write(writer);
        }
        writer.newLine();
        writer.write("}");
    }
}
