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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import nl.jqno.equalsverifier.util.exceptions.ReflectionException;

/**
 * Provides access to the annotations that are defined on a class
 * and its fields.
 * 
 * @author Jan Ouwens
 */
class AnnotationAccessor {
	private final Annotation[] supportedAnnotations;
	private final Class<?> type;
	private final boolean ignoreFailure;
	private final Set<Annotation> classAnnotations = new HashSet<Annotation>();
	private final Map<String, Set<Annotation>> fieldAnnotations = new HashMap<String, Set<Annotation>>();
	
	private boolean processed = false;
	private boolean shortCircuit = false;

	/**
	 * Constructor
	 * 
	 * @param supportedAnnotations Collection of annotations to query.
	 * @param type The class whose annotations need to be queried.
	 * @param ignoreFailure Ignore when processing annotations fails when the
	 * 			class file cannot be read.
	 */
	public AnnotationAccessor(Annotation[] supportedAnnotations, Class<?> type, boolean ignoreFailure) {
		this.supportedAnnotations = supportedAnnotations;
		this.type = type;
		this.ignoreFailure = ignoreFailure;
	}
	
	/**
	 * Determines whether {@link #type} has a particular annotation. 
	 * 
	 * @param annotation The annotation we want to find.
	 * @return True if {@link #type} has an annotation with the supplied name.
	 */
	public boolean typeHas(Annotation annotation) {
		if (shortCircuit) {
			return false;
		}
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
	 * @throws ReflectionException if {@link #type} does not have the specified
	 * 			field.
	 */
	public boolean fieldHas(String fieldName, Annotation annotation) {
		if (shortCircuit || fieldName.startsWith("CGLIB$")) {
			return false;
		}
		process();
		Set<Annotation> annotations = fieldAnnotations.get(fieldName);
		if (annotations == null) {
			throw new ReflectionException("Class " + type.getName() + " does not have field " + fieldName);
		}
		return annotations.contains(annotation);
	}

	private void process() {
		if (processed) {
			return;
		}
		
		visit();
		processed = true;
	}
	
	private void visit() {
		visitType(type, false);
		Class<?> i = type.getSuperclass();
		while (i != null && i != Object.class) {
			visitType(i, true);
			i = i.getSuperclass();
		}
	}
	
	private void visitType(Class<?> type, boolean inheriting) {
		ClassLoader classLoader = getClassLoaderFor(type);
		Type asmType = Type.getType(type);
		String url = asmType.getInternalName() + ".class";
		InputStream is = classLoader.getResourceAsStream(url);
		
		Visitor v = new Visitor(inheriting);
		try {
			ClassReader cr = new ClassReader(is);
			cr.accept(v, 0);
		}
		catch (IOException e) {
			if (ignoreFailure) {
				shortCircuit = true;
			}
			else {
				throw new ReflectionException("Cannot read class file for " + type.getSimpleName() +
						".\nSuppress Warning.ANNOTATION to skip annotation processing phase.");
			}
		}
	}

	private ClassLoader getClassLoaderFor(Class<?> type) {
		ClassLoader result = type.getClassLoader();
		if (result == null) {
			result = ClassLoader.getSystemClassLoader();
		}
		return result;
	}

	private class Visitor extends ClassVisitor {
		private final boolean inheriting;

		public Visitor(boolean inheriting) {
			super(Opcodes.ASM4);
			this.inheriting = inheriting;
		}
		
		@Override
		public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
			add(classAnnotations, descriptor, inheriting);
			return null;
		}
		
		@Override
		public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
			HashSet<Annotation> annotations = new HashSet<Annotation>();
			fieldAnnotations.put(name, annotations);
			return new MyFieldVisitor(annotations, inheriting);
		}
	}
	
	public class MyFieldVisitor extends FieldVisitor {
		private final Set<Annotation> fieldAnnotations;
		private final boolean inheriting;
		
		public MyFieldVisitor(Set<Annotation> fieldAnnotations, boolean inheriting) {
		  super(Opcodes.ASM4);
			this.fieldAnnotations = fieldAnnotations;
			this.inheriting = inheriting;
		}
		
		@Override
		public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
			add(fieldAnnotations, descriptor, inheriting);
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

	private void add(Set<Annotation> annotations, String annotationDescriptor, boolean inheriting) {
		for (Annotation annotation : supportedAnnotations) {
			if (!inheriting || annotation.inherits()) {
				for (String descriptor : annotation.descriptors()) {
					String asBytecodeIdentifier = descriptor.replaceAll("\\.", "/");
					if (annotationDescriptor.contains(asBytecodeIdentifier)) {
						annotations.add(annotation);
					}
				}
			}
		}
	}
}
