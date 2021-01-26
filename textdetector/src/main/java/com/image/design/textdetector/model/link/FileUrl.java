package com.image.design.textdetector.model.link;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FileUrl {

    @JsonProperty
    private List<String> urls = new ArrayList<>();

    public FileUrl() {}

    public void addUrl(final String url) {
        this.urls.add(url);
    }
}
