package com.blueveery.springrest2ts.tsmodel;

public enum ModuleExtensionType {
    typing("t.ds"), implementation("ts");

    private String extension;

    ModuleExtensionType(String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return extension;
    }
}
