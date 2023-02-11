package org.rpgl.uuidtable;

import org.rpgl.core.RPGLEffect;
import org.rpgl.core.RPGLItem;
import org.rpgl.core.RPGLObject;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is dedicated to tracking all JsonObject objects which persist during runtime. Objects are given UUID's so
 * that they may reference them without the risk of generating an infinitely large JSON structure via mutual reference.
 *
 * @author Calvin Withun
 */
public final class UUIDTable {

    private static final Map<String, UUIDTableElement> UUID_TABLE = new ConcurrentHashMap<>();

    /**
     * 	<p><b><i>register</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * static void register(UUIDTableElement uuidTableElement)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method assigns a UUIDTableElement a UUID and registers it with the UUIDTable. If the passed object already
     *  has a UUID, it is registered under that value. This method is intended to be used to register RPGLEffects,
     *  RPGLItems, and RPGLObjects. This method may cause problems if an object already has a UUID which has already been
     *  assigned to a different object.
     * 	</p>
     *
     * 	@param uuidTableElement a UUIDTableElement
     */
    public static void register(UUIDTableElement uuidTableElement) {
        String uuid = uuidTableElement.getUuid();
        while (uuid == null || UUID_TABLE.containsKey(uuid)) {
            uuid = UUID.randomUUID().toString();
        }
        UUID_TABLE.put(uuid, uuidTableElement);
        uuidTableElement.setUuid(uuid);
    }

    /**
     * 	<p><b><i>unregister</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public static UUIDTableElement unregister(String uuid)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method deletes a UUID from its associated UUIDTableElement (if it exists) and removes the UUID from the
     *  UUIDTable.
     * 	</p>
     *
     * 	@param uuid the UUID of a registered UUIDTableElement
     */
    public static UUIDTableElement unregister(String uuid) {
        UUIDTableElement uuidTableElement = UUID_TABLE.remove(uuid);
        if (uuidTableElement != null) {
            uuidTableElement.deleteUuid();
        }
        return uuidTableElement;
    }

    /**
     * 	<p><b><i>clear</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public static void clear()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method clears the UUIDTable. Note that this method does not recursively remove references between
     * 	UUIDTableElement objects, so attempting to use or re-register some but not all of the unregistered
     * 	UUIDTableElement objects may result in errors.
     * 	</p>
     */
    public static void clear() {
        UUID_TABLE.clear();
    }

    /**
     * 	<p><b><i>getEffect</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public static RPGLEffect getEffect(String uuid)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns a RPGLEffect object with the passed uuid, or null if no RPGLEffect exists for that uuid or
     * 	the uuid is null.
     * 	</p>
     *
     *  @param uuid the UUID of a RPGLEffect
     *  @return a RPGLEffect
     */
    public static RPGLEffect getEffect(String uuid) {
        return (RPGLEffect) UUID_TABLE.get(uuid);
    }

    /**
     * 	<p><b><i>getItem</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public static RPGLItem getItem(String uuid)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns a RPGLItem object with the passed uuid, or null if no RPGLItem exists for that uuid or
     * 	the uuid is null.
     * 	</p>
     *
     *  @param uuid the UUID of a RPGLItem
     *  @return a RPGLItem
     */
    public static RPGLItem getItem(String uuid) {
        return (RPGLItem) UUID_TABLE.get(uuid);
    }

    /**
     * 	<p><b><i>getObject</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public static RPGLObject getObject(String uuid)
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns a RPGLObject object with the passed uuid, or null if no RPGLObject exists for that uuid or
     * 	the uuid is null.
     * 	</p>
     *
     *  @param uuid the UUID of a RPGLObject
     *  @return a RPGLObject or null
     */
    public static RPGLObject getObject(String uuid) {
        if (uuid != null) {
            return (RPGLObject) UUID_TABLE.get(uuid);
        }
        return null;
    }

    /**
     * 	<p><b><i>size</i></b></p>
     * 	<p>
     * 	<pre class="tab"><code>
     * public static int size()
     * 	</code></pre>
     * 	</p>
     * 	<p>
     * 	This method returns the number of UUIDTableElement objects stored in UUIDTable.
     * 	</p>
     *
     *  @return the number of UUIDTableElement objects stored in UUIDTable
     */
    public static int size() {
        return UUID_TABLE.size();
    }

}
