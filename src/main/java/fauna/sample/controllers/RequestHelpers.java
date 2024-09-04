package fauna.sample.controllers;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHelpers {
    private static Object getFromField(Field f, Object o) {
        try {
            return f.get(o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
