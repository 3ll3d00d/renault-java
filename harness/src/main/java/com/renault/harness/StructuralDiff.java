package com.renault.harness;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/// Compares two JSON trees structurally (field presence and types), ignoring values.
public class StructuralDiff {

    public enum Status { OK, MISSING_IN_LIVE, EXTRA_IN_LIVE, TYPE_MISMATCH }

    public record Entry(String path, String liveType, String fixtureType, Status status) {}

    public static List<Entry> compare(JsonNode live, JsonNode fixture) {
        var entries = new ArrayList<Entry>();
        walk("$", live, fixture, entries);
        return entries;
    }

    private static void walk(String path, JsonNode live, JsonNode fixture, List<Entry> out) {
        if (fixture.isObject()) {
            Set<Map.Entry<String, JsonNode>> fields = fixture.properties();
            for (var field : fields) {
                String child = path + "." + field.getKey();
                JsonNode liveChild = live.get(field.getKey());
                if (liveChild == null || liveChild.isNull()) {
                    if (!field.getValue().isNull()) {
                        out.add(new Entry(child, "null", typeName(field.getValue()), Status.MISSING_IN_LIVE));
                    } else {
                        out.add(new Entry(child, "null", "null", Status.OK));
                    }
                } else {
                    String lt = typeName(liveChild), ft = typeName(field.getValue());
                    if (!lt.equals(ft)) {
                        out.add(new Entry(child, lt, ft, Status.TYPE_MISMATCH));
                    } else {
                        out.add(new Entry(child, lt, ft, Status.OK));
                        if (liveChild.isObject()) walk(child, liveChild, field.getValue(), out);
                        if (liveChild.isArray() && liveChild.size() > 0 && field.getValue().size() > 0) {
                            walk(child + "[0]", liveChild.get(0), field.getValue().get(0), out);
                        }
                    }
                }
            }
            // fields in live not in fixture
            for (var field : live.properties()) {
                if (fixture.get(field.getKey()) == null) {
                    out.add(new Entry(path + "." + field.getKey(), typeName(field.getValue()), "MISSING", Status.EXTRA_IN_LIVE));
                }
            }
        }
    }

    private static String typeName(JsonNode n) {
        if (n.isNull())    return "null";
        if (n.isTextual()) return "STRING";
        if (n.isInt() || n.isLong()) return "INTEGER";
        if (n.isDouble() || n.isFloat()) return "FLOAT";
        if (n.isBoolean()) return "BOOLEAN";
        if (n.isObject())  return "OBJECT";
        if (n.isArray())   return "ARRAY";
        return "UNKNOWN";
    }
}
