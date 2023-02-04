package org.rpgl.datapack;

import org.rpgl.json.JsonObject;
import org.rpgl.core.RPGLEffectTemplate;
import org.rpgl.core.RPGLEventTemplate;
import org.rpgl.core.RPGLItemTemplate;
import org.rpgl.core.RPGLObjectTemplate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents a datapack, a collection of customizable data used to load novel content into RPGL for the user
 * to use.
 *
 * @author Calvin Withun
 */
public class Datapack {

    private final Map<String, RPGLEffectTemplate> EFFECT_TEMPLATES = new HashMap<>();
    private final Map<String, RPGLEventTemplate>  EVENT_TEMPLATES  = new HashMap<>();
    private final Map<String, RPGLItemTemplate>   ITEM_TEMPLATES   = new HashMap<>();
    private final Map<String, RPGLObjectTemplate> OBJECT_TEMPLATES = new HashMap<>();

    String datapackNamespace;

    /**
     * 	<p><b><i>Datapack</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public Datapack()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	Constructor for the Datapack class. This constructor loads all data located within a single datapack and stores
     *  it in the constructed object for future reference.
     * 	</p>
     *
     * 	@param directory a File directory for a datapack
     */
    public Datapack (File directory) {
        this.datapackNamespace = directory.getName();
        for (File subDirectory : Objects.requireNonNull(directory.listFiles())) {
            if (subDirectory.isDirectory()) {
                System.out.println("Loading " + subDirectory.getName() + "...");
                switch (subDirectory.getName()) {
                    case "effects" -> loadEffectTemplates(subDirectory);
                    case "events"  -> loadEventTemplates(subDirectory);
                    case "items"   -> loadItemTemplates(subDirectory);
                    case "objects" -> loadObjectTemplates(subDirectory);
                }
            }
        }
    }

    /**
     * 	<p><b><i>loadEffectTemplates</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void loadEffectTemplates(File directory)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method loads all effect templates stored in a single directory into the object.
     * 	</p>
     *
     * 	@param directory a File directory for the effects in a datapack
     */
    void loadEffectTemplates(File directory) {
        for (File effectFile : Objects.requireNonNull(directory.listFiles())) {
            String effectId = effectFile.getName().substring(0, effectFile.getName().indexOf('.'));
            System.out.println(" - " + effectId);
            try {
                RPGLEffectTemplate rpglEffectTemplate = JsonObject.MAPPER.readValue(effectFile, RPGLEffectTO.class).toRPGLEffectTemplate();
                rpglEffectTemplate.putString(DatapackContentTO.ID_ALIAS, datapackNamespace + ":" + effectId);
                EFFECT_TEMPLATES.put(effectId, rpglEffectTemplate);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    /**
     * 	<p><b><i>loadEventTemplates</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void loadEventTemplates(File directory)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method loads all event templates stored in a single directory into the object.
     * 	</p>
     *
     * 	@param directory a File directory for the events in a datapack
     */
    private void loadEventTemplates(File directory) {
        for (File eventFile : Objects.requireNonNull(directory.listFiles())) {
            String eventId = eventFile.getName().substring(0, eventFile.getName().indexOf('.'));
            System.out.println(" - " + eventId);
            try {
                RPGLEventTemplate rpglEventTemplate = JsonObject.MAPPER.readValue(eventFile, RPGLEventTO.class).toRPGLEventTemplate();
                rpglEventTemplate.putString(DatapackContentTO.ID_ALIAS, datapackNamespace + ":" + eventId);
                EVENT_TEMPLATES.put(eventId, rpglEventTemplate);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    /**
     * 	<p><b><i>loadItemTemplates</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void loadItemTemplates(File directory)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method loads all item templates stored in a single directory into the object.
     * 	</p>
     *
     * 	@param directory a File directory for the items in a datapack
     */
    private void loadItemTemplates(File directory) {
        for (File itemFile : Objects.requireNonNull(directory.listFiles())) {
            String itemId = itemFile.getName().substring(0, itemFile.getName().indexOf('.'));
            System.out.println(" - " + itemId);
            try {
                RPGLItemTemplate rpglItemTemplate = JsonObject.MAPPER.readValue(itemFile, RPGLItemTO.class).toRPGLItemTemplate();
                rpglItemTemplate.putString(DatapackContentTO.ID_ALIAS, datapackNamespace + ":" + itemId);
                ITEM_TEMPLATES.put(itemId, rpglItemTemplate);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    /**
     * 	<p><b><i>loadObjectTemplates</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * void loadObjectTemplates(File directory)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method loads all object templates stored in a single directory into the object.
     * 	</p>
     *
     * 	@param directory a File directory for the objects in a datapack
     */
    private void loadObjectTemplates(File directory) {
        for (File objectFile : Objects.requireNonNull(directory.listFiles())) {
            String objectId = objectFile.getName().substring(0, objectFile.getName().indexOf('.'));
            System.out.println(" - " + objectId);
            try {
                RPGLObjectTemplate rpglObjectTemplate = JsonObject.MAPPER.readValue(objectFile, RPGLObjectTO.class).toRPGLObjectTemplate();
                rpglObjectTemplate.putString(DatapackContentTO.ID_ALIAS, datapackNamespace + ":" + objectId);
                OBJECT_TEMPLATES.put(objectId, rpglObjectTemplate);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    /**
     * 	<p><b><i>getEffectTemplate</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public RPGLEffectTemplate getEffectTemplate(String effectName)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns a specified RPGLEffectTemplate object.
     * 	</p>
     *
     * 	@param effectName the name of an effect template stored in this datapack
     */
    public RPGLEffectTemplate getEffectTemplate(String effectName) {
        return EFFECT_TEMPLATES.get(effectName);
    }

    /**
     * 	<p><b><i>getEventTemplate</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public RPGLEventTemplate getEventTemplate(String eventName)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns a specified RPGLEventTemplate object.
     * 	</p>
     *
     * 	@param eventName the name of an event template stored in this datapack
     */
    public RPGLEventTemplate getEventTemplate(String eventName) {
        return EVENT_TEMPLATES.get(eventName);
    }

    /**
     * 	<p><b><i>getItemTemplate</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public RPGLItemTemplate getItemTemplate(String itemName)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns a specified RPGLItemTemplate object.
     * 	</p>
     *
     * 	@param itemName the name of an item template stored in this datapack
     */
    public RPGLItemTemplate getItemTemplate(String itemName) {
        return ITEM_TEMPLATES.get(itemName);
    }

    /**
     * 	<p><b><i>getObjectTemplate</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public RPGLObjectTemplate getObjectTemplate(String objectName)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns a specified RPGLObjectTemplate object.
     * 	</p>
     *
     * 	@param objectName the name of an object template stored in this datapack
     */
    public RPGLObjectTemplate getObjectTemplate(String objectName) {
        return OBJECT_TEMPLATES.get(objectName);
    }

}
