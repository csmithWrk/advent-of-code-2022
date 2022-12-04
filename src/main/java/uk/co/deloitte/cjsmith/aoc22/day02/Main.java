package uk.co.deloitte.cjsmith.aoc22.day02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.co.deloitte.cjsmith.aoc22.utils.IOUtils.*;
import static uk.co.deloitte.cjsmith.aoc22.utils.Constants.*;

//https://adventofcode.com/2022/day/2
public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("Points from game 1: {}", new Day02().task1());
        LOGGER.info("Points from game 2: {}", new Day02().task2());
    }

    private static class Day02 {
        List<String> values = readFileToList(DAY_2_DIR, "1.txt")
                .stream().filter(str -> !str.isBlank()).toList();

        int task1() {
            int outcome = 0;
            for (String value : values) {
                outcome += pointsFromTask1(value);
            }
            return outcome;
        }

        int task2() {
            int outcome = 0;
            for (String value : values) {
                outcome += pointsFromTask2(value);
            }
            return outcome;
        }

        private int pointsFromTask1(String line) {
            String[] values = line.split("\\s+");
            GameOption opponent = GameOption.getOption(values[0]);
            GameOption you = GameOption.getOption(values[1]);
            return you.points + you.getOutcome(opponent).points;
        }

        private int pointsFromTask2(String line) {
            String[] values = line.split("\\s+");
            GameOption opponent = GameOption.getOption(values[0]);
            Outcome expectedResult = Outcome.getOutcome(values[1]);
            return expectedResult.points + opponent.getOptionForOutcome(expectedResult).points;
        }
    }

    private enum Outcome {
        LOSE(0, "X"),
        DRAW(3, "Y"),
        WIN(6, "Z");

        final int points;
        final String symbol;

        Outcome(int points, String symbol) {
            this.points = points;
            this.symbol = symbol;
        }

        static Outcome getOutcome(String symbol) {
            return Arrays.stream(values()).filter(outcome -> outcome.symbol.equalsIgnoreCase(symbol))
                    .findFirst().orElse(null);
        }
    }

    private enum GameOption {
        ROCK(1, Arrays.asList("A","X")),
        PAPER(2, Arrays.asList("B","Y")),
        SCISSORS(3, Arrays.asList("C","Z"));

        final int points;
        final List<String> symbols;

        final static Map<String, GameOption> symbolToOption = generateSymbolMapping();

        GameOption(int points, List<String> symbols) {
            this.points = points;
            this.symbols = symbols;
        }

        Outcome getOutcome(GameOption opponent) {
            if (this == ROCK) {
                return getOutcome(opponent, SCISSORS, PAPER);
            } else if (this == PAPER) {
                return getOutcome(opponent, ROCK, SCISSORS);
            } else {
                return getOutcome(opponent, PAPER, ROCK);
            }
        }

        GameOption getOptionForOutcome(Outcome outcome) {
            if (this == ROCK) {
                return getOptionForOutcome(outcome, PAPER, SCISSORS);
            } else if (this == PAPER) {
                return getOptionForOutcome(outcome, SCISSORS, ROCK);
            } else {
                return getOptionForOutcome(outcome, ROCK, PAPER);
            }
        }

        private Outcome getOutcome(GameOption opponent, GameOption win, GameOption lose) {
            if (opponent == win) {
                return Outcome.WIN;
            } else if (opponent == lose) {
                return Outcome.LOSE;
            } else {
                return Outcome.DRAW;
            }
        }

        private GameOption getOptionForOutcome(Outcome outcome, GameOption win, GameOption lose) {
            if (outcome == Outcome.DRAW) {
                return this;
            } else if (outcome == Outcome.WIN) {
                return win;
            } else {
                return lose;
            }
        }

        static GameOption getOption(String symbol) {
            return symbolToOption.get(symbol.toUpperCase());
        }

        private static Map<String, GameOption> generateSymbolMapping() {
            Map<String, GameOption> output = new HashMap<>();
            Arrays.stream(values())
                    .forEach(option -> addToMapping(output, option));
            return output;
        }

        private static void addToMapping(Map<String, GameOption> output, GameOption option) {
            option.symbols.forEach(symbol -> output.put(symbol, option));
        }
    }
}
