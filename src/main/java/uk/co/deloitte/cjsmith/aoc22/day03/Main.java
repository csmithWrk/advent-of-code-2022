package uk.co.deloitte.cjsmith.aoc22.day03;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static uk.co.deloitte.cjsmith.aoc22.utils.IOUtils.*;
import static uk.co.deloitte.cjsmith.aoc22.utils.Constants.*;

public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final int CHAR_OFFSET_LOWER = 'a' - 1;
    private static final int CHAR_OFFSET_UPPER = 'A' - 27;

    public static void main(String[] args) {
        LOGGER.info("Sum of priorities: {}", new Day03().task1());
        LOGGER.info("Sum of badges: {}", new Day03().task2());
    }

    private static int charToVal(Character ch) {
        if (Character.isLowerCase(ch)) {
            return ch - CHAR_OFFSET_LOWER;
        } else {
            return ch - CHAR_OFFSET_UPPER;
        }
    }

    private static class Day03 {
        List<String> values = readFileToList(DAY_3_DIR, "1.txt")
                .stream().filter(str -> !str.isBlank()).toList();

        List<Rucksack> bags = values.stream().map(Rucksack::new).toList();

        List<Character> groupsBadge = findGroupsBadges(bags);

        int task1() {
            return bags.stream().mapToInt(bag -> bag.valueOfSimilar).sum();
        }

        int task2() {
            return groupsBadge.stream().mapToInt(Main::charToVal).sum();
        }

        private List<Character> findGroupsBadges(List<Rucksack> bags ) {
            List<Character> output = new ArrayList<>();
            for (int index = 0; index < bags.size(); index += 3) {
                output.add(
                        findBadgeForGroup(
                            bags.get(index),
                            bags.get(index + 1),
                            bags.get(index + 2)
                        )
                );
            }
            return output;
        }

        private Character findBadgeForGroup(Rucksack first, Rucksack second, Rucksack third) {
            return first.contentCharacters.stream()
                    .filter(second.contentCharacters::contains)
                    .filter(third.contentCharacters::contains)
                    .findFirst().orElse(null);
        }
    }

    private static class Rucksack {
        final List<Character> contentCharacters;
        final List<Character> firstHalf;
        final List<Character> secondHalf;
        final Set<Character> similar;
        final int valueOfSimilar;

        Rucksack(String contents) {
            contentCharacters= getCharacters(contents);
            firstHalf = contentCharacters.subList(0, contentCharacters.size()/2);
            secondHalf = contentCharacters.subList(contentCharacters.size()/2, contentCharacters.size());
            similar = findSimilar();
            valueOfSimilar = calculateSimilarValue();
        }

        private List<Character> getCharacters(String str) {
            List<Character> output = new ArrayList<>();
            for (byte ch : str.getBytes(StandardCharsets.UTF_8)) {
                output.add((char) ch);
            }
            return output;
        }

        private Set<Character> findSimilar() {
            Set<Character> output = new HashSet<>(firstHalf);
            return output.stream().filter(secondHalf::contains).collect(Collectors.toSet());
        }

        private int calculateSimilarValue() {
            return similar.stream().mapToInt(Main::charToVal).sum();
        }
    }
}
