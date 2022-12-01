package uk.co.deloitte.cjsmith.aoc22.day01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.co.deloitte.cjsmith.aoc22.utils.IOUtils.*;
import static uk.co.deloitte.cjsmith.aoc22.utils.Constants.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// https://adventofcode.com/2022/day/1
public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static final Integer SEPARATOR = -1;

    public static void main(String[] args) {
        LOGGER.info("Max calories on single elf: {}", new Day1().task1());
        LOGGER.info("Calories of top 3 elves: {}", new Day1().task2());
    }

    private static class Day1 {
        List<Integer> values = readFileToList(DAY_1_DIR, "1.txt")
                .stream()
                .map(val -> val.isBlank() ? SEPARATOR : Integer.parseInt(val))
                .toList();

        List<Elf> elves = toElves(values);

        int task1() {
            int maxCalories = 0;
            for (Elf elf : elves) {
                maxCalories = Math.max(maxCalories, elf.caloriesCarried);
            }
            return maxCalories;
        }

        int task2() {
            List<Elf> sortedElves = new ArrayList<>(elves);
            sortedElves.sort(Comparator.comparingInt(e -> -e.caloriesCarried)); //Negative to sort high to low
            return sortedElves.get(0).caloriesCarried +
                    sortedElves.get(1).caloriesCarried +
                    sortedElves.get(2).caloriesCarried;
        }

        private List<Elf> toElves(List<Integer> values) {
            List<Elf> output = new ArrayList<>();
            List<Integer> currentElf = new ArrayList<>();
            for (Integer value : values) {
                if (value.equals(SEPARATOR)) {
                    output.add(new Elf(currentElf));
                    currentElf = new ArrayList<>();
                } else {
                    currentElf.add(value);
                }
            }
            output.add(new Elf(currentElf));

            return output;
        }
    }

    private static class Elf {
        final int caloriesCarried;
        final List<Integer> allFood;

        Elf(List<Integer> allFood) {
            this.allFood = allFood;
            this.caloriesCarried = allFood.stream().reduce(0, Integer::sum);
        }
    }
}
