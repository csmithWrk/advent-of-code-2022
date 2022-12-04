package uk.co.deloitte.cjsmith.aoc22.day04;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static uk.co.deloitte.cjsmith.aoc22.utils.IOUtils.*;
import static uk.co.deloitte.cjsmith.aoc22.utils.Constants.*;

//https://adventofcode.com/2022/day/4
public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("Pairs where one contains the full range of another: {}", new Day04().task1());
        LOGGER.info("Pairs where have intersecting values: {}", new Day04().task2());
    }

    private static class Day04 {
        List<String> values = readFileToList(DAY_4_DIR, "1.txt")
                .stream().filter(str -> !str.isBlank()).toList();

        List<Pair> pairs = toPairs(values);

        long task1() {
            return pairs.stream()
                    .filter(Pair::oneContainsTheOther)
                    .count();
        }

        long task2() {
            return pairs.stream()
                    .filter(Pair::pairIntersect)
                    .count();
        }

        private List<Pair> toPairs(List<String> values) {
            return values.stream().map(this::toPair).toList();
        }

        private Pair toPair(String value) {
            String[] splitPair = value.split(",");
            return new Pair(new Range(splitPair[0]), new Range(splitPair[1]));
        }

    }

    private static class Range {
        final int min;
        final int max;

        Range(String range) {
            List<Integer> values = Arrays.stream(range.split("-")).map(Integer::parseInt).toList();
            this.min = Math.min(values.get(0), values.get(1));
            this.max = Math.max(values.get(0), values.get(1));
        }

        boolean intersects(Range range) {
            int minMax = Math.min(max, range.max);
            int maxMin = Math.max(min, range.min);
            return maxMin <= minMax;
        }

        boolean contains(Range range) {
            return min <= range.min && max >= range.max;
        }
    }

    private static class Pair {
        final Range value1;
        final Range value2;

        Pair(Range value1, Range value2) {
            this.value1 = value1;
            this.value2 = value2;
        }

        boolean oneContainsTheOther() {
            return value1.contains(value2) || value2.contains(value1);
        }

        boolean pairIntersect() {
            return value1.intersects(value2);
        }
    }
}
