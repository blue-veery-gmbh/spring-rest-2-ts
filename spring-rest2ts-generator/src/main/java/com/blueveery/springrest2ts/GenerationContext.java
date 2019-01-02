package com.blueveery.springrest2ts;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSComplexType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomaszw on 31.07.2017.
 */
public class GenerationContext {
    private ImplementationGenerator implementationGenerator;
    private Map<TSComplexType, ImplementationGenerator> tsComplexTypeImplementationGeneratorMap = new HashMap<>();

    public GenerationContext(ImplementationGenerator implementationGenerator) {
        this.implementationGenerator = implementationGenerator;
    }

    public ImplementationGenerator getImplementationGenerator(TSComplexType tsComplexType) {
        ImplementationGenerator nonDefaultImplementationGenerator = tsComplexTypeImplementationGeneratorMap.get(tsComplexType);
        if (nonDefaultImplementationGenerator != null) {
            return nonDefaultImplementationGenerator;
        }else {
            return this.implementationGenerator;
        }
    }

    public ImplementationGenerator getDefaultImplementationGenerator() {
        return implementationGenerator;
    }

    public void addImplementationGenerator(TSClass tsClass, ImplementationGenerator generator) {
        tsComplexTypeImplementationGeneratorMap.put(tsClass, generator);
    }
}
