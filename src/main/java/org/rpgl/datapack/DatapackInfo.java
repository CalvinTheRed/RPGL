package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatapackInfo {

    public static final String VERSION_ALIAS = "version";
    public static final String DESCRIPTION_ALIAS = "description";

    @JsonProperty(VERSION_ALIAS)
    public String version;
    @JsonProperty(DESCRIPTION_ALIAS)
    public String description;

}
