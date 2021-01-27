package com.image.design.textdetector.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class FileUrlResponseResult {

    @JsonProperty
    private String message;

    @JsonProperty
    private ResponseType type;

    @JsonProperty
    private List<String> urls;

    public void addUrls(final List<String> urls) {
        if(Objects.isNull(this.urls)) {
            this.urls = new ArrayList<>();
        }

        this.urls.addAll(urls);
    }

    public void addUrl(final String url) {
        if(Objects.isNull(this.urls)) {
            this.urls = new ArrayList<>();
        }

        this.urls.add(url);
    }
}
