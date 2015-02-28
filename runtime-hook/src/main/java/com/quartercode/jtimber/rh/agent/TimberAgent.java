/*
 * This file is part of JTimber.
 * Copyright (c) 2015 QuarterCode <http://www.quartercode.com/>
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

package com.quartercode.jtimber.rh.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.quartercode.jtimber.rh.agent.asm.TimberClassFileTransformer;
import com.quartercode.jtimber.rh.agent.util.ResourceLister;

/**
 * The javaagent which installs the bytecode manipulator using a {@link ClassFileTransformer} (the {@link TimberClassFileTransformer}).
 * The bytecode manipulator then adds extra bytecode for tracking the parents of parent-aware objects.
 * This agent is part of the runtime hook.
 * 
 * @see TimberClassFileTransformer
 */
public class TimberAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimberAgent.class);

    public static void premain(String args, Instrumentation inst) {

        // Read the stored node index; use a set in order to avoid (possible) duplicate entries
        Set<String> nodeIndex = readIndex("/META-INF/jtimber/nodes.index");

        // Add a transformer to transform all nodes
        inst.addTransformer(new TimberClassFileTransformer(nodeIndex));
    }

    /*
     * Reads all index files that can be found on the classpath under the given resource path and returns the merged indexes.
     */
    private static Set<String> readIndex(String resourcePath) {

        Set<String> index = new HashSet<>();

        try (ResourceLister resourceLister = new ResourceLister(resourcePath, false)) {
            for (Path resource : resourceLister.getResourcePaths()) {
                try {
                    index.addAll(readLines(resource));
                } catch (IOException e) {
                    LOGGER.error("Cannot read lines from specific index file ('{}')", resource, e);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Cannot read index files from '{}'", resourcePath, e);
        }

        return index;
    }

    /*
     * A custom method is required for avoiding empty lines and converting from binary to internal names.
     */
    private static List<String> readLines(Path path) throws IOException {

        List<String> result = new ArrayList<>();

        try (BufferedReader in = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
            String line;
            while ( (line = in.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    result.add(line.trim().replace('.', '/'));
                }
            }

            return result;
        }
    }

    private TimberAgent() {

    }

}
