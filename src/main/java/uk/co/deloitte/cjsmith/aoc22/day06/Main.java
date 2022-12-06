package uk.co.deloitte.cjsmith.aoc22.day06;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static uk.co.deloitte.cjsmith.aoc22.utils.IOUtils.*;
import static uk.co.deloitte.cjsmith.aoc22.utils.Constants.*;

//https://adventofcode.com/2022/day/6
public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final int NON_REPEATING_SECTOR_T1 = 4;
    private static final int NON_REPEATING_SECTOR_T2 = 14;

    public static void main(String[] args) {
        LOGGER.info("Start of transmission: {}", new Day06().task1());
        LOGGER.info("Start of message maker: {}", new Day06().task2());
    }

    private static class Day06 {
        final String value = readFileToList(DAY_6_DIR, "1.txt")
                .stream().filter(str -> !str.isBlank()).findFirst().orElse("");
        final Datastream input = new Datastream(value);

        int task1() {
            return input.findEndFirstNonRepeatingSector(NON_REPEATING_SECTOR_T1);
        }

        int task2() {
            return input.findEndFirstNonRepeatingSector(NON_REPEATING_SECTOR_T2);
        }
    }

    private static class Datastream {
        final char[] characters;

        Datastream(String input) {
            characters = input.toCharArray();
        }

        int findEndFirstNonRepeatingSector(int length) {
            Set<Character> uniqueSectorCheck = new HashSet<>();

            for (int index = 0; index + length <= characters.length; index++) {
                if (checkSector(index, length, uniqueSectorCheck)) {
                    return index + length;
                }
                uniqueSectorCheck.clear();
            }
            throw new RuntimeException("Failed to find non-repeating sector");
        }

        private boolean checkSector(int index, int length, Set<Character> uniqueSectorCheck) {
            for (int i = index; i < index + length; i++) {
                uniqueSectorCheck.add(characters[i]);
            }
            return uniqueSectorCheck.size() >= length;
        }
    }
}
