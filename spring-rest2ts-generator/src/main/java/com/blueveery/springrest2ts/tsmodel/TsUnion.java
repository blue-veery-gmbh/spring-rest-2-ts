package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.GenerationContext;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TsUnion extends TSType {
    private List<TSType> joinedTypeList = new ArrayList<>();

    public TsUnion(TSType... joinedTypes) {
        super(generateUnionName(Arrays.asList(joinedTypes)));
        for (TSType joinedType : joinedTypes) {
            joinedTypeList.add(joinedType);
        }
    }

    private static String generateUnionName(List<TSType> joinedTypes) {
        return String.join(" | ", joinedTypes.stream().map(t -> t.getName()).collect(Collectors.toList()));
    }

    public List<TSType> getJoinedTypeList() {
        return joinedTypeList;
    }

    @Override
    public void write(GenerationContext generationContext, BufferedWriter writer) throws IOException {
        writer.write(": " + generateUnionName(getJoinedTypeList()));
    }
}
