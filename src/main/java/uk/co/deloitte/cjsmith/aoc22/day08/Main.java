package uk.co.deloitte.cjsmith.aoc22.day08;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import static uk.co.deloitte.cjsmith.aoc22.utils.Constants.DAY_8_DIR;
import static uk.co.deloitte.cjsmith.aoc22.utils.IOUtils.readFileToList;

//https://adventofcode.com/2022/day/8
public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("Can be seen from outside forest: {}", new Day08().task1());
        LOGGER.info("Highest scoring tree: {}", new Day08().task2());
    }

    private static class Day08 {
        List<String> values = readFileToList(DAY_8_DIR, "1.txt").stream()
                .filter(str -> !str.isBlank())
                .toList();
        Forest forest = buildForest();

        long task1() {
            return forest.allTrees().stream()
                    .filter(forest::canBeSeenOutsideForest)
                    .count();
        }

        int task2() {
            return forest.allTrees().stream()
                    .mapToInt(forest::scenicScore)
                    .max().getAsInt();
        }

        Forest buildForest() {
            Forest output = new Forest();
            values.forEach(output::addRow);
            return output;
        }
    }

    private static class Forest {
        final List<List<Tree>> byRow = new ArrayList<>();
        final List<List<Tree>> byColumn = new ArrayList<>();

        void addRow(String input) {
            final int[] heights = Arrays.stream(input.split(""))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            final int y = height();

            List<Tree> row =IntStream.range(0, heights.length).boxed()
                    .map(x -> new Tree(x, y, heights[x]))
                    .toList();

            byRow.add(row);
            addRowToColumns(row);
        }

        List<Tree> allTrees() {
            return byRow.stream().flatMap(List::stream).toList();
        }

        int height() {
            return byRow.size();
        }

        int width() {
            return byColumn.size();
        }

        boolean canBeSeenOutsideForest(Tree tree) {
            return onlyShorterNorth(tree) || onlyShorterSouth(tree)
                    || onlyShorterEast(tree) || onlyShorterWest(tree);
        }

        int scenicScore(Tree tree) {
            return scenicScoreNorth(tree) * scenicScoreSouth(tree)
                    * scenicScoreEast(tree) * scenicScoreWest(tree);
        }

        boolean onlyShorterNorth(Tree tree) {
            return onlyShorter(tree.height, tree, this::north);
        }

        boolean onlyShorterEast(Tree tree) {
            return onlyShorter(tree.height, tree, this::east);
        }

        boolean onlyShorterSouth(Tree tree) {
            return onlyShorter(tree.height, tree, this::south);
        }

        boolean onlyShorterWest(Tree tree) {
            return onlyShorter(tree.height, tree, this::west);
        }

        int scenicScoreNorth(Tree tree) {
            return scenicScoreDirection(0, tree.height, tree, this::north);
        }

        int scenicScoreEast(Tree tree) {
            return scenicScoreDirection(0, tree.height, tree, this::east);
        }

        int scenicScoreSouth(Tree tree) {
            return scenicScoreDirection(0, tree.height, tree, this::south);
        }

        int scenicScoreWest(Tree tree) {
            return scenicScoreDirection(0, tree.height, tree, this::west);
        }

        Optional<Tree> north(Tree from) {
            if (from.y == 0) {
                return Optional.empty();
            } else {
                return Optional.of(byColumn.get(from.x).get(from.y - 1));
            }
        }

        Optional<Tree> south(Tree from) {
            if (from.y == height() - 1) {
                return Optional.empty();
            } else {
                return Optional.of(byColumn.get(from.x).get(from.y + 1));
            }
        }

        Optional<Tree> east(Tree from) {
            if (from.x == width() - 1) {
                return Optional.empty();
            } else {
                return Optional.of(byRow.get(from.y).get(from.x + 1));
            }
        }

        Optional<Tree> west(Tree from) {
            if (from.x == 0) {
                return Optional.empty();
            } else {
                return Optional.of(byRow.get(from.y).get(from.x - 1));
            }
        }

        private boolean onlyShorter(int height, Tree previous, Function<Tree, Optional<Tree>> nextGetter) {
            Optional<Tree> next = nextGetter.apply(previous);

            if (next.isEmpty()) {
                return true;
            } else if (next.get().height >= height) {
                return false;
            } else {
                return onlyShorter(height, next.get(), nextGetter);
            }
        }

        private int scenicScoreDirection(int score, int height, Tree previous, Function<Tree, Optional<Tree>> nextGetter) {
            Optional<Tree> next = nextGetter.apply(previous);

            if (next.isEmpty()) {
                return score;
            } else if (next.get().height >= height) {
                return score + 1;
            } else {
                return scenicScoreDirection(score + 1, height, next.get(), nextGetter);
            }
        }

        private void addRowToColumns(List<Tree> row) {
            if (width() < row.size()) {
                IntStream.range(width(), row.size())
                        .forEach(i -> byColumn.add(new ArrayList<>()));
            }

            IntStream.range(0, row.size())
                    .forEach(i -> byColumn.get(i).add(row.get(i)));
        }
    }

    private static class Tree {
        final int x;
        final int y;
        final int height;

        Tree(int x, int y, int height) {
            this.x = x;
            this.y = y;
            this.height = height;
        }
    }
}
