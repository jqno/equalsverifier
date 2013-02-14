package nl.jqno.equalsverifier.util;

import java.lang.reflect.Field;
import java.util.regex.Matcher;

public class Formatter {
	private final String message;
	private Object[] objects;

	public static Formatter of(String message, Object... objects) {
		return new Formatter(message, objects);
	}
	
	private Formatter(String message, Object... objects) {
		if (message == null) {
			throw new NullPointerException();
		}
		this.message = message;
		this.objects = objects;
	}
	
	public String format() {
		String result = message;
		for (int i = 0; i < objects.length; i++) {
			String s = result.replaceFirst("%%", Matcher.quoteReplacement(stringify(objects[i])));
			if (result.equals(s)) {
				throw new IllegalStateException("Too many parameters");
			}
			result = s;
		}
		if (result.contains("%%")) {
			throw new IllegalStateException("Not enough parameters");
		}
		return result;
	}
	
	private String stringify(Object obj) {
		if (obj == null) {
			return "null";
		}
		try {
			return obj.toString();
		}
		catch (Throwable e) {
			StringBuilder result = new StringBuilder();
			result.append(stringifyByReflection(obj));
			result.append("-throws ");
			result.append(e.getClass().getSimpleName());
			result.append("(");
			result.append(e.getMessage());
			result.append(")");
			return result.toString();
		}
	}
	
	private String stringifyByReflection(Object obj) {
		StringBuilder result = new StringBuilder();
		
		Class<?> type = obj.getClass();
		ObjectAccessor<?> accessor = ObjectAccessor.of(obj);
		
		result.append("[");
		String typeName = type.getSimpleName().replaceAll("\\$\\$EnhancerByCGLIB.*", "");
		result.append(typeName);
		
		for (Field field : FieldIterable.of(type)) {
			String fieldName = field.getName();
			if (!fieldName.startsWith("CGLIB$")) {
				result.append(" ");
				result.append(fieldName);
				result.append("=");
				Object value = accessor.fieldAccessorFor(field).get();
				result.append(stringify(value));
			}
		}
		
		result.append("]");
		return result.toString();
	}
}
