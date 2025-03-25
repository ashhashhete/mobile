package com.igenesys.model;

public class AutoCompleteModal {

    public String code;
    public String description;

    public AutoCompleteModal(String code,String description) {
        this.code = code;
        this.description=description;
    }

    @Override
    public String toString() {
        return description;
    }
}
