package com.blueveery.springrest2ts.tests.model;

public class Keyboard implements KeyboardInterface {
    public Keyboard(int keyNumber) {
        this.fKeyNumber = keyNumber;
    }

    public Keyboard() {
    }

    public int fKeyNumber = 2;

    @Override
    public int getKeyNumber() {
        return 0;
    }

    @Override
    public void setKeyNumber(int fKeyNumber) {

    }
}
