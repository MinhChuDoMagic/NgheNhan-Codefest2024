package org.codefest2024.nghenhan.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {
    public static boolean isEmpty(String s) {
        if (s == null) {
            return true;
        } else {
            return s.isEmpty();
        }
    }

    @SafeVarargs
    public static <T> List<T> filterNonNull(T... args) {
        if (args == null) {
            return Collections.emptyList(); // Return an empty list if args itself is null
        }
        List<T> result = new ArrayList<>();
        for (T arg : args) {
            if (arg != null) {
                result.add(arg);
            }
        }
        return result;
    }
}
