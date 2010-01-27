package nl.jqno.equalsverifier.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FieldIterable implements Iterable<Field> {
	private final Class<?> klass;
	
	public FieldIterable(Class<?> klass) {
		this.klass = klass;
	}
	
	@Override
	public Iterator<Field> iterator() {
		return createFieldList().iterator();
	}

	private List<Field> createFieldList() {
		List<Field> result = new ArrayList<Field>();
		
		Class<?> i = klass;
		while (i != null && i != Object.class) {
			for (Field field : i.getDeclaredFields()) {
				result.add(field);
			}
			i = i.getSuperclass();
		}
		
		return result;
	}
}
