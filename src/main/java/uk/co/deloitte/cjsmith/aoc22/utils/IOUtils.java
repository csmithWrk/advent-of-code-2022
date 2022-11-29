package uk.co.deloitte.cjsmith.aoc22.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IOUtils {

    public static String readFile(String path) {
        return readFile(Paths.get(path));
    }

    public static String readFile(String path, String... morePath) {
        return readFile(Paths.get(path, morePath));
    }

    public static String readFile(Path file) {
        try {
            return Files.readString(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> readFileToList(String path) {
        return readFileToList(Paths.get(path));
    }

    public static List<String> readFileToList(String path, String... morePath) {
        return readFileToList(Paths.get(path, morePath));
    }

    public static List<String> readFileToList(Path file) {
        try {
            return Files.readAllLines(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static  <V, T> List<T> listToType(Function<V, T> converter, List<V> values) {
        return values.stream().map(converter).collect(Collectors.toList());
    }
}
