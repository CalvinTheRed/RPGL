package org.rpgl.testUtils;

import org.rpgl.core.RPGLEvent;
import org.rpgl.core.RPGLResource;

import java.util.List;
import java.util.Objects;

public final class TestUtils {

    public static RPGLEvent getEventById(List<RPGLEvent> events, String eventId) {
        for (RPGLEvent event : events) {
            if (Objects.equals(event.getId(), eventId)) {
                return event;
            }
        }
        return null;
    }

    public static RPGLResource getResourceById(List<RPGLResource> resources, String resourceId) {
        for (RPGLResource resource : resources) {
            if (Objects.equals(resource.getId(), resourceId)) {
                return resource;
            }
        }
        return null;
    }

}
