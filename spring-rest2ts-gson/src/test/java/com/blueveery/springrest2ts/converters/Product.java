package com.blueveery.springrest2ts.converters;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

class Product {
    public transient String tempName = "phone";

    public String name = "phone";

    @SerializedName("year")
    public int productionYear = 2021;

    @Expose
    public String exposedName = "phone";

    @Expose(serialize = false, deserialize = false)
    public String falseExposedName = "phone";

    @Expose(deserialize = false)
    public String serializedOnly = "phone";

    @Expose(serialize = false)
    public String deserializedOnly = "phone";

    @Since(2.0)
    public String sinceField = "phone";

    @Until(4.0)
    public String untilField = "phone";

    public int doors = 5;

    public Keyboard keyboard = new Keyboard();
}

class KeyboardTypeAdapter extends TypeAdapter<Keyboard>{
    @Override
    public void write(JsonWriter jsonWriter, com.blueveery.springrest2ts.converters.Keyboard value) throws IOException {
        jsonWriter.value(value.keyNumber);
    }

    @Override
    public Keyboard read(JsonReader in) throws IOException {
        return new Keyboard(in.nextInt());
    }
}

@JsonAdapter(value = KeyboardTypeAdapter.class)
class Keyboard {
    public Keyboard(int keyNumber) {
        this.keyNumber = keyNumber;
    }

    public Keyboard() {
    }

    public int keyNumber = 2;
}
