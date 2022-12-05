package uk.co.deloitte.cjsmith.aoc22.day05;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static uk.co.deloitte.cjsmith.aoc22.utils.IOUtils.*;
import static uk.co.deloitte.cjsmith.aoc22.utils.Constants.*;

//https://adventofcode.com/2022/day/5
public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("Top containers after move: {}", new Day05().task1());
        LOGGER.info("Top containers after moved in order: {}", new Day05().task2());
    }

    private static class Day05 {
        private static final String KEY_ROW_PREFIX = " 1";

        final List<String> values = readFileToList(DAY_5_DIR, "1.txt")
                .stream().filter(str -> !str.isBlank()).toList();
        final Ship ship = new Ship();
        final List<Order> orders = new ArrayList<>();

        Day05() {
            int keyRow = findKeyRow();
            fillShip(keyRow);
            listOrders(keyRow + 1);
        }

        String task1() {
            orders.forEach(ship::moveContainers);
            return ship.tops();
        }

        String task2() {
            orders.forEach(ship::moveContainersInOrder);
            return ship.tops();
        }

        private int findKeyRow() {
            for (int index = 0; index < values.size(); index++) {
                if (values.get(index).startsWith(KEY_ROW_PREFIX)) {
                    return index;
                }
            }
            throw new RuntimeException("Can't find key row");
        }

        private void fillShip(int keyRow) {
            for (int index = keyRow - 1; index >= 0; index--) {
                ship.addRow(values.get(index));
            }
        }

        private void listOrders(int startIndex) {
            for (int index = startIndex; index < values.size(); index++) {
                orders.add(new Order(values.get(index)));
            }
        }
    }

    private static class Ship {
        private static final int INITIAL_CHARACTER_INDEX = 1;
        private static final int CHARACTER_GAP = 4;

        final Map<Integer, Deque<Character>> containers = new HashMap<>();

        void addRow(String row) {
            char[] characters = row.toCharArray();
            for (int index = INITIAL_CHARACTER_INDEX; index < characters.length; index += CHARACTER_GAP) {
                Deque<Character> stack = containers.computeIfAbsent(
                        indexToContainer(index),
                        key -> new ArrayDeque<>()
                );

                char character = characters[index];
                if (character != ' ') {
                    stack.push(character);
                }
            }
        }

        int indexToContainer(int index) {
            return (index - INITIAL_CHARACTER_INDEX) / CHARACTER_GAP;
        }

        void moveContainers(Order order) {
            Deque<Character> from = containers.get(order.from - 1);
            Deque<Character> to = containers.get(order.to - 1);

            IntStream.range(0, order.numberToMove).boxed()
                    .map(index -> from.pop())
                    .forEach(to::push);
        }

        void moveContainersInOrder(Order order) {
            Deque<Character> from = containers.get(order.from - 1);
            Deque<Character> to = containers.get(order.to - 1);

            IntStream.range(0, order.numberToMove).boxed()
                    .map(index -> from.pop())
                    .collect(Collectors.toCollection(ArrayDeque::new))
                    .descendingIterator()
                    .forEachRemaining(to::push);
        }

        String tops() {
            StringBuilder sb = new StringBuilder();
            orderedKeys().stream()
                    .map(containers::get)
                    .map(Deque::peek)
                    .forEach(sb::append);
            return sb.toString();
        }

        List<Integer> orderedKeys() {
            return containers.keySet().stream().sorted().toList();
        }
    }

    private static class Order {
        private static final int MOVES_INDEX = 1;
        private static final int FROM_INDEX = 3;
        private static final int TO_INDEX = 5;

        final int numberToMove;
        final int from;
        final int to;

        Order(String order) {
            String[] words = order.split("\\s+");
            numberToMove = Integer.parseInt(words[MOVES_INDEX]);
            from = Integer.parseInt(words[FROM_INDEX]);
            to = Integer.parseInt(words[TO_INDEX]);
        }
    }
}
