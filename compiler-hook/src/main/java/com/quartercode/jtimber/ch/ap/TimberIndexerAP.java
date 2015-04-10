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

package com.quartercode.jtimber.ch.ap;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * The job of this annotation processor is to create an index file containing the binary names of all classes which implement the "Node" interface.
 * That index is then written into an index file which is packaged alongside the class files for later use by the runtime hook.
 */
@SupportedSourceVersion (SourceVersion.RELEASE_7)
@SupportedAnnotationTypes ("*")
public class TimberIndexerAP extends AbstractProcessor {

    private Elements           elementUtils;
    private Types              typeUtils;
    private TypeMirror         nodeTypeErasure;

    private final List<String> nodeIndex = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {

        super.init(processingEnv);

        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();

        nodeTypeErasure = typeUtils.erasure(elementUtils.getTypeElement("com.quartercode.jtimber.api.node.Node").asType());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        // Index all required classes that can be found in the current root elements
        processElements(roundEnv.getRootElements());

        // If the processing is over, generate the result index file
        if (roundEnv.processingOver()) {
            generateResults();
        }

        return false;
    }

    /*
     * Adds all node class elements from the given list to the node index collection.
     * Then invokes itself recursively with all nested elements it can find.
     */
    private void processElements(Collection<? extends Element> elements) {

        for (Element element : elements) {
            // Only node *classes* need to be indexed since only they contain instruction that could possibly set parent-aware fields
            if (element.getKind() == ElementKind.CLASS) {
                String binaryName = elementUtils.getBinaryName((TypeElement) element).toString();

                if (typeUtils.isAssignable(typeUtils.erasure(element.asType()), nodeTypeErasure)) {
                    // Add the node class to the node index
                    nodeIndex.add(binaryName);
                }
            }

            // Process all nested elements
            processElements(element.getEnclosedElements());
        }
    }

    private void generateResults() {

        writeListToFile("META-INF/jtimber/nodes.index", nodeIndex);
    }

    /*
     * Creates a new output file (which will be packaged alongside the class files) under the given resource path.
     * Then writes the contents of the given list into that file.
     */
    private void writeListToFile(String filePath, List<String> list) {

        try {
            FileObject file = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", filePath);

            try (Writer writer = file.openWriter()) {
                for (String element : list) {
                    writer.append(element).append("\n");
                }
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Unexpected exception while storing an index: " + e.getMessage());
        }
    }

}
