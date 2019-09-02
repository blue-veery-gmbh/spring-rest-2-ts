package com.blueveery.springrest2ts.tsmodel;

import java.io.BufferedWriter;
import java.io.IOException;

public class TSCommentSection extends TSElement {

    private StringBuilder commentText = new StringBuilder();

    public TSCommentSection(String name) {
        super(name);
    }

    public StringBuilder getCommentText() {
        return commentText;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        String[] lines = commentText.toString().split("\\\\r?\\\\n");
        for (String line : lines) {
            writer.write("/*\t");
            writer.write(line);
            writer.newLine();
        }
        writer.newLine();

    }
}
