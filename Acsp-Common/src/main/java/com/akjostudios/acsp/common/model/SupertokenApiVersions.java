package com.akjostudios.acsp.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.data.util.Version;

import java.util.List;

@Getter
public class SupertokenApiVersions {
    @JsonProperty("versions")
    private List<String> versions;

    public Version getLatestVersion() {
        return versions.stream()
                .map(Version::parse)
                .max(Version::compareTo)
                .orElse(Version.parse("2.10"));
    }
}