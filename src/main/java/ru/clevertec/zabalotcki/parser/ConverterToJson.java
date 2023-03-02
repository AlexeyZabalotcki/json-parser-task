package ru.clevertec.zabalotcki.parser;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

public class ConverterToJson {

    public static String toJson(Object object) {
        if (object == null) {
            return "null";
        }
        Set<Object> visited = new HashSet<>();
        StringBuilder builder = new StringBuilder();
        toJsonRecursive(object, visited, builder);
        return builder.toString();
    }

    private static void toJsonRecursive(Object object, Set<Object> visited, StringBuilder builder) {
        if (object == null) {
            builder.append("null");
        } else if (visited.contains(object)) {
            builder.append("{}");
        } else {
            visited.add(object);
            Class<?> clazz = object.getClass();

            if (clazz.isPrimitive() || object instanceof Number || object instanceof Boolean) {
                builder.append(object);
            } else if (object instanceof String) {
                builder.append('"').append(escapeString((String) object)).append('"');
            } else if (object instanceof Character) {
                builder.append('"').append(escapeString(Character.toString((Character) object))).append('"');
            } else if (clazz.isArray()) {
                builder.append("[");
                int length = Array.getLength(object);

                for (int i = 0; i < length; i++) {
                    Object element = Array.get(object, i);
                    toJsonRecursive(element, visited, builder);

                    if (i < length - 1) {
                        builder.append(",");
                    }
                }

                builder.append("]");
            } else if (object instanceof Collection<?>) {
                builder.append("[");
                Collection<?> collection = (Collection<?>) object;

                for (Object element : collection) {
                    toJsonRecursive(element, visited, builder);
                    builder.append(",");
                }

                if (!collection.isEmpty()) {
                    builder.deleteCharAt(builder.length() - 1);
                }

                builder.append("]");
            } else if (object instanceof Map<?, ?>) {
                builder.append("{");
                Map<?, ?> map = (Map<?, ?>) object;

                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    Object key = entry.getKey();
                    Object value = entry.getValue();

                    if (key == null || value == null) {
                        continue;
                    }

                    builder.append('"').append(escapeString(key.toString())).append("\":");
                    toJsonRecursive(value, visited, builder);
                    builder.append(",");
                }

                if (!map.isEmpty()) {
                    builder.deleteCharAt(builder.length() - 1);
                }

                builder.append("}");
            } else {
                builder.append("{");

                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);

                    try {
                        Object fieldValue = field.get(object);

                        if (fieldValue != null) {
                            builder.append('"').append(field.getName()).append("\":");
                            toJsonRecursive(fieldValue, visited, builder);
                            builder.append(",");
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                if (builder.charAt(builder.length() - 1) == ',') {
                    builder.deleteCharAt(builder.length() - 1);
                }

                builder.append("}");
            }

            visited.remove(object);
        }
    }


    private static String escapeString(String string) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            switch (ch) {
                case '\"':
                    builder.append("\\\"");
                    break;
                case '\\':
                    builder.append("\\\\");
                    break;
                case '\b':
                    builder.append("\\b");
                    break;
                case '\f':
                    builder.append("\\f");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                default:
                    if (Character.isISOControl(ch)) {
                        builder.append("\\u").append(String.format("%04X", (int) ch));
                    } else {
                        builder.append(ch);
                    }
            }
        }
        return builder.toString();
    }
}

