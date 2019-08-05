package com.pruebanelson.dto;

import javax.validation.constraints.NotNull;

public class PostRequest {

    @NotNull
    private String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
