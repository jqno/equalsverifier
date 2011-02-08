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

public class AnnotationAccessor {
	private final Class<?> type;
	private final Set<String> classAnnotations = new HashSet<String>();
	private final Map<String, Set<String>> fieldAnnotations = new HashMap<String, Set<String>>();

	public AnnotationAccessor(Class<?> type) {
		this.type = type;
		process();
	}

	private void process() {
		try {
			visitType();
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private void visitType() throws IOException {
		ClassLoader classLoader = type.getClassLoader();
		Type asmType = Type.getType(type);
		String url = asmType.getInternalName() + ".class";
		InputStream is = classLoader.getResourceAsStream(url);
		
		Visitor v = new Visitor();
		ClassReader cr = new ClassReader(is);
		cr.accept(v, 0);
	}
	
	public boolean typeHas(String annotationDescriptor) {
		return find(classAnnotations, annotationDescriptor);
	}
	
	public boolean fieldHas(String fieldName, String annotationDescriptor) {
		Set<String> annotations = fieldAnnotations.get(fieldName);
		return find(annotations, annotationDescriptor);
	}
	
	private boolean find(Set<String> annotations, String annotationDescriptor) {
		String needle = annotationDescriptor.replaceAll("\\.", "/");
		for (String haystack : annotations) {
			if (haystack.contains(needle)) {
				return true;
			}
		}
		return false;
	}
	
	private class Visitor extends ClassWriter {
		public Visitor() {
			super(0);
		}
		
		@Override
		public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
			classAnnotations.add(descriptor);
			return null;
		}
		
		@Override
		public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
			HashSet<String> annotations = new HashSet<String>();
			fieldAnnotations.put(name, annotations);
			return new MyFieldVisitor(annotations);
		}
	}
	
	public class MyFieldVisitor implements FieldVisitor {
		private final Set<String> fieldAnnotations;
		
		public MyFieldVisitor(Set<String> fieldAnnotations) {
			this.fieldAnnotations = fieldAnnotations;
		}
		
		@Override
		public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
			fieldAnnotations.add(descriptor);
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
}
