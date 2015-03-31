/*
 * This file is part of JTimber.
 * Copyright (c) 2015 QuarterCode <http://quartercode.com/>
 *
 * JTimber is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimber is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JTimber. If not, see <http://www.gnu.org/licenses/>.
 */

package com.quartercode.jtimber.api.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.quartercode.jtimber.api.node.Node;
import com.quartercode.jtimber.api.node.ParentAware;

/**
 * An internal utility class for accessing metadata concerning the library.
 * For example, this class is able to return the allowed parent type of any parent-aware class.
 */
public class MetadataAccessor {

    private static final Logger            LOGGER               = LoggerFactory.getLogger(MetadataAccessor.class);

    private static Map<Class<?>, Class<?>> allowedParentClasses = new HashMap<>();

    // ----- Parsing -----

    static {

        // Parse the parent-aware class index in order to retrieve the allowed parent type for each parent-aware class
        String paIndexPath = "/META-INF/jtimber/parentAwares.index";
        try (ResourceLister resourceLister = new ResourceLister(paIndexPath, false)) {
            for (Path paIndexResource : resourceLister.getResourcePaths()) {
                parsePAIndex(paIndexResource);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot read index files from '{}'", paIndexPath, e);
        }

    }

    private static void parsePAIndex(Path paIndexResource) {

        try (BufferedReader in = Files.newBufferedReader(paIndexResource, Charset.forName("UTF-8"))) {
            String line;
            while ( (line = in.readLine()) != null) {
                parsePAIndexLine(paIndexResource, line);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot read lines from specific index file ('{}')", paIndexResource, e);
        }
    }

    private static void parsePAIndexLine(Path paIndexResource, String line) {

        if (!line.trim().isEmpty()) {
            String[] mapping = line.split(":");

            Class<?> parentAwareClass = forName(paIndexResource, mapping[0]);
            Class<?> parentClass = forName(paIndexResource, mapping[1]);

            if (parentAwareClass != null && parentClass != null) {
                allowedParentClasses.put(parentAwareClass, parentClass);
            }
        }
    }

    private static Class<?> forName(Path paIndexResource, String name) {

        try {
            return Class.forName(name.trim());
        } catch (ClassNotFoundException e) {
            LOGGER.error("Cannot find class '{}' which is referenced by the specific index file '{}'", name, paIndexResource, e);
            return null;
        }
    }

    // ----- Public Access -----

    /**
     * Returns the allowed parent class (also known as the generic type parameter <code>ParentAware.&lt;P&gt;</code>) of the given {@link ParentAware} class.
     * Only classes which are "compatible" with the returned class are allowed to be a parent of the parent-aware class.
     * That rule must be enforced by the {@link ParentAware#addParent(Node)} method.
     * 
     * @param parentAwareClass The parent-aware class whose allowed parent type should be returned.
     *        This class must implement somehow {@link ParentAware}.
     * @return The allowed parent type of the given parent-aware class.
     */
    public static Class<?> getAllowedParentClass(Class<?> parentAwareClass) {

        return allowedParentClasses.get(parentAwareClass);
    }

    private MetadataAccessor() {

    }

}
