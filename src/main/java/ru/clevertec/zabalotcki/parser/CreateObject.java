package ru.clevertec.zabalotcki.parser;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

public class CreateObject {

    public static <T> T createObjectFromMap(Map<String, Object> map, Class<T> clazz) throws Exception {
        T object = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (map.containsKey(fieldName)) {
                Object value = map.get(fieldName);
                field.setAccessible(true);
                if (field.getType() == String.class) {
                    field.set(object, value.toString());
                } else if (field.getType() == Integer.class || field.getType() == int.class) {
                    field.set(object, Integer.parseInt(value.toString()));
                } else if (field.getType() == Long.class || field.getType() == long.class) {
                    field.set(object, Long.parseLong(value.toString()));
                } else if (field.getType() == Double.class || field.getType() == double.class) {
                    field.set(object, Double.parseDouble(value.toString()));
                } else if (field.getType() == Float.class || field.getType() == float.class) {
                    field.set(object, Float.parseFloat(value.toString()));
                } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                    field.set(object, Boolean.parseBoolean(value.toString()));
                } else if (Collection.class.isAssignableFrom(field.getType())) {
                    Collection<Object> collection = createCollection(field.getType());
                    if (value instanceof Collection) {
                        collection.addAll((Collection) value);
                    } else if (value.getClass().isArray()) {
                        collection.addAll(Arrays.asList((Object[]) value));
                    }
                    field.set(object, collection);
                } else if (field.getType().isArray()) {
                    if (value.getClass().isArray()) {
                        int length = Array.getLength(value);
                        Object newArray = Array.newInstance(field.getType().getComponentType(), length);
                        for (int i = 0; i < length; i++) {
                            Object arrayElement = Array.get(value, i);
                            if (arrayElement instanceof Map) {
                                Array.set(newArray, i, createObjectFromMap((Map) arrayElement, field.getType().getComponentType()));
                            } else {
                                Array.set(newArray, i, arrayElement);
                            }
                        }
                        field.set(object, newArray);
                    } else if (value instanceof Collection) {
                        Collection collection = (Collection) value;
                        int length = collection.size();
                        Object newArray = Array.newInstance(field.getType().getComponentType(), length);
                        int i = 0;
                        for (Object collectionElement : collection) {
                            if (collectionElement instanceof Map) {
                                Array.set(newArray, i++, createObjectFromMap((Map) collectionElement, field.getType().getComponentType()));
                            } else {
                                Array.set(newArray, i++, convertValue(collectionElement, field.getType().getComponentType()));
                            }
                        }
                        field.set(object, newArray);
                    }
                } else if (Map.class.isAssignableFrom(field.getType())) {
                    if (value instanceof Map) {
                        Map<Object, Object> mapValue = (Map) value;
                        Map<Object, Object> newMap = createMap((Class<? extends Map>) field.getType());
                        for (Map.Entry<Object, Object> entry : mapValue.entrySet()) {
                            Object key = entry.getKey();
                            Object val = entry.getValue();
                            newMap.put(key, convertValue(val, Object.class));
                        }
                        field.set(object, newMap);
                    }
                } else if (field.getType() == HashMap.class) {
                    if (value instanceof Map) {
                        field.set(object, new HashMap((Map) value));
                    } else {
                        throw new Exception("Unsupported value type: " + value.getClass().getName());
                    }
                } else {
                    field.set(object, convertValue(value, field.getType()));
                }
            }
        }
        return object;
    }

    private static Collection<Object> createCollection(Class<?> type) throws Exception {
        if (type.isAssignableFrom(ArrayList.class)) {
            return new ArrayList<>();
        } else if (type.isAssignableFrom(LinkedList.class)) {
            return new LinkedList<>();
        } else if (type.isAssignableFrom(HashSet.class)) {
            return new HashSet<>();
        } else if (type.isAssignableFrom(TreeSet.class)) {
            return new TreeSet<>();
        } else if (type.isAssignableFrom(Stack.class)) {
            return new Stack<>();
        } else if (type.isAssignableFrom(Vector.class)) {
            return new Vector<>();
        } else {
            throw new Exception("Unsupported collection type: " + type.getName());
        }
    }

    private static <T> T convertValue(Object value, Class<T> clazz) throws Exception {
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        if (clazz == String.class) {
            return clazz.cast(value.toString());
        }
        if (clazz == Character.class || clazz == char.class) {
            String strValue = value.toString();
            if (strValue.length() > 0) {
                return (T) Character.valueOf(value.toString().charAt(0));
            }
        }
        if (clazz == Integer.class || clazz == int.class) {
            return (T) Integer.valueOf(value.toString());
        }
        if (clazz == Long.class || clazz == long.class) {
            return (T) Long.valueOf(value.toString());
        }
        if (clazz == Double.class || clazz == double.class) {
            return (T) Double.valueOf(value.toString());
        }
        if (clazz == Float.class || clazz == float.class) {
            return (T) Float.valueOf(value.toString());
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return clazz.cast(Boolean.parseBoolean(value.toString()));
        }
        if (clazz.isArray()) {
            Class<?> componentType = clazz.getComponentType();
            if (value instanceof Collection) {
                Collection<Object> collection = (Collection) value;
                Object array = Array.newInstance(componentType, collection.size());
                int i = 0;
                for (Object element : collection) {
                    Array.set(array, i++, convertValue(element, componentType));
                }
                return clazz.cast(array);
            }
            if (value.getClass().isArray()) {
                int length = Array.getLength(value);
                Object array = Array.newInstance(componentType, length);
                for (int i = 0; i < length; i++) {
                    Array.set(array, i, convertValue(Array.get(value, i), componentType));
                }
                return clazz.cast(array);
            }
            throw new Exception("Unsupported array value: " + value.getClass().getName());
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            Collection<Object> collection = createCollection(clazz);
            if (value instanceof Collection) {
                collection.addAll((Collection) value);
            } else if (value.getClass().isArray()) {
                collection.addAll(Arrays.asList((Object[]) value));
            }
            return clazz.cast(collection);
        }
        if (HashMap.class.isAssignableFrom(clazz)) {
            if (value instanceof Map) {
                Map<Object, Object> mapValue = (Map) value;
                Map<Object, Object> newMap = createMap((Class<? extends Map>) clazz);
                for (Map.Entry<Object, Object> entry : mapValue.entrySet()) {
                    Object key = entry.getKey();
                    Object val = entry.getValue();
                    if (val instanceof Map) {
                        val = convertValue(val, Object.class);
                    }
                    newMap.put(convertValue(key, Object.class), val);
                }
                return clazz.cast(newMap);
            }
            throw new Exception("Unsupported map value: " + value.getClass().getName());
        }
        throw new Exception("Unsupported value type: " + value.getClass().getName());
    }


    private static <K, V> Map<K, V> createMap(Class<? extends Map> type) throws Exception {
        if (type.isAssignableFrom(HashMap.class)) {
            return new HashMap<K, V>();
        } else if (type.isAssignableFrom(LinkedHashMap.class)) {
            return new LinkedHashMap<K, V>();
        } else if (type.isAssignableFrom(TreeMap.class)) {
            return new TreeMap<K, V>();
        } else {
            throw new Exception("Unsupported map type: " + type.getName());
        }
    }
}
