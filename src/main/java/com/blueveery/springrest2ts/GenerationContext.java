package com.blueveery.springrest2ts;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;

/**
 * Created by tomaszw on 31.07.2017.
 */
public class GenerationContext {
    private ImplementationGenerator implementationGenerator;

    public GenerationContext(ImplementationGenerator implementationGenerator) {
        this.implementationGenerator = implementationGenerator;
    }

    public ImplementationGenerator getImplementationGenerator() {
        return implementationGenerator;
    }
}
