package com.blueveery.springrest2ts.tsmodel;

import com.blueveery.springrest2ts.GenerationContext;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TsUnion extends TSType {
    private List<TSElement> joinedTsElementList = new ArrayList<>();

    public TsUnion(TSType... joinedTypes) {
        super(generateUnionName(Arrays.asList(joinedTypes)));
        for (TSType joinedType : joinedTypes) {
            joinedTsElementList.add(joinedType);
        }
    }

    private static String generateUnionName(List<TSElement> joinedTypes) {
        List<String> list = new ArrayList<>();
        for (TSElement tsElement : joinedTypes) {
            if (tsElement instanceof TSType) {
                String name = tsElement.getName();
                list.add(name);
                continue;
            }
            if (tsElement instanceof TSLiteral) {
                TSLiteral tsLiteral = (TSLiteral) tsElement;
                String name = tsLiteral.toTsValue();
                list.add(name);
            }
        }
        return String.join(" | ", list);
    }

    public List<TSElement> getJoinedTsElementList() {
        return joinedTsElementList;
    }

    @Override
    public void write(GenerationContext generationContext, BufferedWriter writer) throws IOException {
        writer.write(generateUnionName(getJoinedTsElementList()));
    }
}
