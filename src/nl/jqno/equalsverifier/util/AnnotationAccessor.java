/*
 * Copyright 2011 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.asm.AnnotationVisitor;
import net.sf.cglib.asm.Attribute;
import net.sf.cglib.asm.ClassReader;
import net.sf.cglib.asm.ClassWriter;
import net.sf.cglib.asm.FieldVisitor;
import net.sf.cglib.asm.Type;
import nl.jqno.equalsverifier.Annotation;

/**
 * Provides access to the annotations that are defined on a class
 * and its fields.
 * 
 * @author Jan Ouwens
 */
class AnnotationAccessor {
	private final Class<?> type;
	private final Annotation[] supportedAnnotations;
	private final Set<Annotation> classAnnotations = new HashSet<Annotation>();
	private final Map<String, Set<Annotation>> fieldAnnotations = new HashMap<String, Set<Annotation>>();
	
	private boolean processed = false;

	/**
	 * Constructor
	 * 
	 * @param type The class whose annotations need to be queried.
	 */
	public AnnotationAccessor(Annotation[] supportedAnnotations, Class<?> type) {
		this.supportedAnnotations = supportedAnnotations;
		this.type = type;
	}
	
	/**
	 * Determines whether {@link #type} has a particular annotation. 
	 * 
	 * @param annotation The annotation we want to find.
	 * @return True if {@link #type} has an annotation with the supplied name.
	 */
	public boolean typeHas(Annotation annotation) {
		process();
		return classAnnotations.contains(annotation);
	}
	
	/**
	 * Determines whether {@link #type} has a particular annotation on a
	 * particular field.
	 *  
	 * @param fieldName The name of the field for which we want to know if it
	 * 			has the annotation.
	 * @param annotation The annotation we want to find.
	 * @return True if the specified field in {@link #type} has the specified
	 * 			annotation.
	 * @throws InternalException if {@link #type} does not have the specified
	 * 			field.
	 */
	public boolean fieldHas(String fieldName, Annotation annotation) {
		process();
		Set<Annotation> annotations = fieldAnnotations.get(fieldName);
		if (annotations == null) {
			throw new InternalException("Class " + type.getName() + " does not have field " + fieldName);
		}
		return annotations.contains(annotation);
	}

	private void process() {
		if (processed) {
			return;
		}
		
		try {
			visit();
			processed = true;
		}
		catch (IOException e) {
			throw new InternalException(e);
		}
	}
	
	private void visit() throws IOException {
		Class<?> i = type;
		while (i != null && i != Object.class) {
			visitType(i);
			i = i.getSuperclass();
		}
	}
	
	private void visitType(Class<?> type) throws IOException {
		ClassLoader classLoader = type.getClassLoader();
		Type asmType = Type.getType(type);
		String url = asmType.getInternalName() + ".class";
		InputStream is = classLoader.getResourceAsStream(url);
		
		Visitor v = new Visitor();
		ClassReader cr = new ClassReader(is);
		cr.accept(v, 0);
	}

	private class Visitor extends ClassWriter {
		public Visitor() {
			super(0);
		}
		
		@Override
		public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
			add(classAnnotations, descriptor);
			return null;
		}
		
		@Override
		public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
			HashSet<Annotation> annotations = new HashSet<Annotation>();
			fieldAnnotations.put(name, annotations);
			return new MyFieldVisitor(annotations);
		}
	}
	
	public class MyFieldVisitor implements FieldVisitor {
		private final Set<Annotation> fieldAnnotations;
		
		public MyFieldVisitor(Set<Annotation> fieldAnnotations) {
			this.fieldAnnotations = fieldAnnotations;
		}
		
		@Override
		public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
			add(fieldAnnotations, descriptor);
			return null;
		}

		@Override
		public void visitAttribute(Attribute attr) {
			// Deliberately empty
		}

		@Override
		public void visitEnd() {
			// Deliberately empty
		}
	}

	private void add(Set<Annotation> annotations, String annotationDescriptor) {
		for (Annotation annotation : supportedAnnotations) {
			for (String descriptor : annotation.descriptors()) {
				String asBytecodeIdentifier = descriptor.replaceAll("\\.", "/");
				if (annotationDescriptor.contains(asBytecodeIdentifier)) {
					annotations.add(annotation);
				}
			}
		}
	}
}
