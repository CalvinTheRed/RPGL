package org.rpgl.datapack;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is used to interface with a datapack's pack.info file. It would be considered a transfer object, except
 * that this information does not persist in RPGL.
 *
 * @author Calvin Withun
 */
public class DatapackInfo {

    public static final String VERSION_ALIAS = "version";
    public static final String DESCRIPTION_ALIAS = "description";

    @JsonProperty(VERSION_ALIAS)
    public String version;
    @JsonProperty(DESCRIPTION_ALIAS)
    public String description;

}
