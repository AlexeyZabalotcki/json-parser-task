package ru.clevertec.zabalotcki.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {
    private String json;
    private int position;

    public JsonParser(String json) {
        this.json = json;
        this.position = 0;
    }

    public Object parse() {
        skipSpaces();
        if (json.charAt(position) == '{') {
            return parseObject();
        } else if (json.charAt(position) == '[') {
            return parseArray();
        } else {
            throw new IllegalArgumentException("Invalid JSON format.");
        }
    }

    private Map<String, Object> parseObject() {
        if (json.charAt(position) != '{') {
            throw new IllegalArgumentException("Expected '{' at position " + position);
        }
        position++;
        skipSpaces();
        Map<String, Object> object = new HashMap<>();
        while (json.charAt(position) != '}') {
            String key = parseString();
            skipSpaces();
            if (json.charAt(position) != ':') {
                throw new IllegalArgumentException("Expected ':' at position " + position);
            }
            position++;
            skipSpaces();
            Object value = parseValue();
            object.put(key, value);
            skipSpaces();
            if (json.charAt(position) == ',') {
                position++;
                skipSpaces();
            } else if (json.charAt(position) != '}') {
                throw new IllegalArgumentException("Expected '}' or ',' at position " + position);
            }
        }
        position++;
        return object;
    }

    private List<Object> parseArray() {
        if (json.charAt(position) != '[') {
            throw new IllegalArgumentException("Expected '[' at position " + position);
        }
        position++;
        skipSpaces();
        List<Object> array = new ArrayList<>();
        while (json.charAt(position) != ']') {
            Object value = parseValue();
            array.add(value);
            skipSpaces();
            if (json.charAt(position) == ',') {
                position++;
                skipSpaces();
            } else if (json.charAt(position) != ']') {
                throw new IllegalArgumentException("Expected ']' or ',' at position " + position);
            }
        }
        position++;
        return array;
    }

    private Object parseValue() {
        if (json.charAt(position) == '{') {
            return parseObject();
        } else if (json.charAt(position) == '[') {
            return parseArray();
        } else if (json.charAt(position) == '\"') {
            return parseString();
        } else if (Character.isDigit(json.charAt(position)) || json.charAt(position) == '-') {
            return parseNumber();
        } else if (json.startsWith("true", position)) {
            position += 4;
            return true;
        } else if (json.startsWith("false", position)) {
            position += 5;
            return false;
        } else if (json.startsWith("null", position)) {
            position += 4;
            return null;
        } else {
            throw new IllegalArgumentException("Invalid JSON value at position " + position);
        }
    }

    private String parseString() {
        if (json.charAt(position) != '\"') {
            throw new IllegalArgumentException("Expected '\"' at position " + position);
        }
        position++;
        StringBuilder sb = new StringBuilder();
        while (json.charAt(position) != '\"') {
            if (json.charAt(position) == '\\') {
                position++;
                char c = json.charAt(position);
                switch (c) {
                    case '\"':
                        sb.append('\"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '/':
                        sb.append('/');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u':
                        String hex = json.substring(position + 1, position + 5);
                        char unicode = (char) Integer.parseInt(hex, 16);
                        sb.append(unicode);
                        position += 4;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid escape character at position " + position);
                }
            } else {
                sb.append(json.charAt(position));
            }
            position++;
        }
        position++;
        return sb.toString();
    }

    private Object parseNumber() {
        StringBuilder builder = new StringBuilder();
        boolean isDouble = false;
        char c = json.charAt(position);
        if (c == '-') {
            builder.append(c);
            position++;
        }
        while (position < json.length()) {
            c = json.charAt(position);
            if (c == '.' || c == 'e' || c == 'E') {
                isDouble = true;
            }
            if (!Character.isDigit(c) && c != '.' && c != '-' && c != '+' && c != 'e' && c != 'E') {
                break;
            }
            builder.append(c);
            position++;
        }
        if (isDouble) {
            return Double.parseDouble(builder.toString());
        } else {
            return Long.parseLong(builder.toString());
        }
    }

    private void skipSpaces() {
        while (position < json.length()) {
            char ch = json.charAt(position);
            if (!Character.isWhitespace(ch)) {
                break;
            }
            position++;
        }
    }
}
