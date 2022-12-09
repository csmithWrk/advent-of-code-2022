package uk.co.deloitte.cjsmith.aoc22.day09;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.IntStream;

import static uk.co.deloitte.cjsmith.aoc22.utils.Constants.DAY_9_DIR;
import static uk.co.deloitte.cjsmith.aoc22.utils.IOUtils.readFileToList;

//https://adventofcode.com/2022/day/9
public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final int DISTANCE_INDEX = 1;
    private static final int DIRECTION_INDEX = 0;

    public static void main(String[] args) {
        LOGGER.info("Total visited location by tail: {}", new Day09().task1());
        LOGGER.info("Total visited location by 9 tails: {}", new Day09().task2());
    }

    private static class Day09 {
        List<Move> values = readFileToList(DAY_9_DIR, "1.txt").stream()
                .filter(str -> !str.isBlank())
                .map(Move::new)
                .toList();

        int task1() {
            Knots knots = new Knots();
            values.forEach(knots::moveHead);
            return knots.totalVisitedLocations();
        }

        int task2() {
            Knots knots = new Knots(9);
            values.forEach(knots::moveHead);
            return knots.totalVisitedLocations();
        }
    }

    private static class Knots {
        Location head = new Location();
        List<Location> tails = new ArrayList<>();
        Set<String> allTailLocation = new HashSet<>();

        Knots() {
            tails.add(new Location());
            allTailLocation.add(stringLocation(tails.get(0)));
        }

        Knots(int numberTails) {
            IntStream.range(0, numberTails).forEach(i -> tails.add(new Location()));
            allTailLocation.add(stringLocation(tails.get(tails.size() - 1)));
        }

        int totalVisitedLocations() {
            return allTailLocation.size();
        }

        void moveHead(Move move) {
            IntStream.range(0, move.distance)
                    .forEach(i -> this.moveHead(move.direction, head));
        }

        private void moveHead(Direction direction, Location head) {
            head.move(direction);
            moveTail(head, 0);
        }

        private void moveTail(Location head, int tailIndex) {
            if (tailIndex == tails.size()) {
                allTailLocation.add(
                        stringLocation(tails.get(tailIndex - 1))
                );
                return;
            }

            Location tail = tails.get(tailIndex);
            if (!tail.isNextTo(head)) {
                tail.moveTowards(head);
                moveTail(tail, tailIndex + 1);
            }
        }

        private String stringLocation(Location location) {
            return location.x + "," + location.y;
        }
    }

    private static class Location {
        int x = 0;
        int y = 0;

        boolean isNextTo(Location other) {
            return Math.abs(other.x - x) <= 1 && Math.abs(other.y - y) <= 1;
        }

        void move(Direction move) {
            switch (move) {
                case UP -> y++;
                case DOWN -> y--;
                case LEFT -> x--;
                case RIGHT -> x++;
            }
        }

        void moveTowards(Location other) {
            if (other.x - x > 0) {
                x++;
            } else if (other.x - x < 0) {
                x--;
            }

            if (other.y - y > 0) {
                y++;
            } else if (other.y - y < 0) {
                y--;
            }
        }
    }

    private static class Move {
        final Direction direction;
        final int distance;

        Move(String input) {
            String[] splitInput = input.split("\\s+");
            this.direction = Direction.bySymbol(splitInput[DIRECTION_INDEX]);
            this.distance = Integer.parseInt(splitInput[DISTANCE_INDEX]);
        }
    }

    private enum Direction {
        UP("U"),
        DOWN("D"),
        LEFT("L"),
        RIGHT("R");

        final String symbol;

        Direction(String symbol) {
            this.symbol = symbol;
        }

        static Direction bySymbol(String symbol) {
            return Arrays.stream(values())
                    .filter(direction -> direction.symbol.equals(symbol))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Failed to find direction symbol " + symbol));
        }
    }
}
