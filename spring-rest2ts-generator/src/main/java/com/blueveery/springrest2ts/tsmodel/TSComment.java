package com.blueveery.springrest2ts.tsmodel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TSComment extends TSElement {
    Map<String, TSCommentSection> tsCommentSectionMap = new HashMap<>();

    public TSComment(String name) {
        super(name);
    }

    public TSCommentSection getTsCommentSection(String name){
        return tsCommentSectionMap.computeIfAbsent(name, sectionName -> new TSCommentSection(sectionName));
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        if (!tsCommentSectionMap.isEmpty()) {
            writer.write("/**");
            for (TSCommentSection section : tsCommentSectionMap.values()) {
                section.write(writer);
                writer.newLine();
            }
            writer.write("*/");
        }
    }
}
