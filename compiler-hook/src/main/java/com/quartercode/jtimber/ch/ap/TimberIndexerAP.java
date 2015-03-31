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
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
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

    private static final String FQCN_PARENT_AWARE = "com.quartercode.jtimber.api.node.ParentAware";
    private static final String FQCN_NODE         = "com.quartercode.jtimber.api.node.Node";

    private Elements            elementUtils;
    private Types               typeUtils;

    private TypeMirror          paTypeErasure;
    private TypeMirror          nodeTypeErasure;

    private final List<String>  paIndex           = new ArrayList<>();
    private final List<String>  nodeIndex         = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {

        super.init(processingEnv);

        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();

        // Any generic parameters are erased in order to be able to make proper assignabilty checks
        paTypeErasure = typeUtils.erasure(elementUtils.getTypeElement(FQCN_PARENT_AWARE).asType());
        nodeTypeErasure = typeUtils.erasure(elementUtils.getTypeElement(FQCN_NODE).asType());
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
     * Call "processClass" on all type elements from the given element list.
     * Then invokes itself recursively with all nested elements it can find.
     */
    private void processElements(Collection<? extends Element> elements) {

        for (Element element : elements) {
            // Only parent-aware *classes* need to be indexed since the parent type limit is only relevant for actual classes
            // Moreover, only node *classes* need to be indexed since only they contain instruction that could possibly set parent-aware fields
            if (element.getKind() == ElementKind.CLASS) {
                processClass((TypeElement) element);
            }

            // Process all nested elements
            processElements(element.getEnclosedElements());
        }
    }

    /*
     * If the given type element represents a parent-aware class, this method adds it and the generic type argument of its <P> parameter to the PA index.
     * If the given type element represents a node, this method adds it to the node index.
     */
    private void processClass(TypeElement element) {

        String binaryName = elementUtils.getBinaryName(element).toString();
        TypeMirror elementType = element.asType();

        if (typeUtils.isAssignable(typeUtils.erasure(elementType), paTypeErasure)) {
            // Resolve the generic type argument for ParentAware.<P> declared by the parent-aware class
            TypeMirror implPaType = getImplementedPATypeMirror(elementType);
            TypeMirror allowedParentType = ((DeclaredType) implPaType).getTypeArguments().get(0);

            // If ParentAware.<P> is another type variable, resolve that one
            if (allowedParentType instanceof TypeVariable) {
                allowedParentType = ((TypeVariable) allowedParentType).getUpperBound();
            }

            // Add the parent-aware class and its allowed parent type (declared in ParentAware.<P>) to the index
            paIndex.add(binaryName + ":" + elementUtils.getBinaryName((TypeElement) typeUtils.asElement(allowedParentType)).toString());

            // The class can only be a node if it is parent-aware
            if (typeUtils.isAssignable(typeUtils.erasure(elementType), nodeTypeErasure)) {
                // Add the node class to the index
                nodeIndex.add(binaryName);
            }
        }
    }

    /*
     * Given an arbitrary type which somehow implements "ParentAware", this method returns a type mirror which represents the "implemented ParentAware interface" for exactly that type.
     * The returned mirror can then be used to extract the generic type argument for the generic type parameter <P>.
     */
    private TypeMirror getImplementedPATypeMirror(TypeMirror paType) {

        // Retrieve all direct supertypes ("extends" and "implements") of the current parent-aware type
        List<? extends TypeMirror> supertypes = typeUtils.directSupertypes(paType);

        // If one of the supertypes is the "ParentAware" interface, return the mirror which represents that interface
        for (TypeMirror supertype : supertypes) {
            if ( ((TypeElement) typeUtils.asElement(supertype)).getQualifiedName().toString().equals(FQCN_PARENT_AWARE)) {
                return supertype;
            }
        }

        // If the current type doesn't directly implement "ParentAware", call this method on all found supertypes and return the first result
        for (TypeMirror supertype : supertypes) {
            TypeMirror result = getImplementedPATypeMirror(supertype);

            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private void generateResults() {

        writeListToFile("META-INF/jtimber/parentAwares.index", paIndex);
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
