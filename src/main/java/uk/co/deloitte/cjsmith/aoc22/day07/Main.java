package uk.co.deloitte.cjsmith.aoc22.day07;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import static uk.co.deloitte.cjsmith.aoc22.utils.IOUtils.*;
import static uk.co.deloitte.cjsmith.aoc22.utils.Constants.*;

//https://adventofcode.com/2022/day/7
public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final int NO_SIZE = -1;

    private static final int FS_SIZE_T2 = 70_000_000;
    private static final int NEEDED_SPACE_T2 = 30_000_000;


    public static void main(String[] args) {
        LOGGER.info("Total size of all dirs under 100k in size: {}", new Day07().task1());
        LOGGER.info("Smallest dir than can be deleted: {}", new Day07().task2());
    }

    private static class Day07 {
        List<Command> commands = readFileToList(DAY_7_DIR, "1.txt").stream()
                .filter(str -> !str.isBlank())
                .map(Command::new)
                .toList();
        FileSystem fileSystem = fillFileSystem();

        int task1() {
            return fileSystem.allContents.stream()
                    .filter(entry -> entry.type.isDir())
                    .mapToInt(Entry::getSize)
                    .filter(size -> size <= 100_000)
                    .sum();
        }

        int task2() {
            int totalUsedSpace = fileSystem.root.getSize();
            final int neededSavings = totalUsedSpace - (FS_SIZE_T2 - NEEDED_SPACE_T2);
            return fileSystem.allContents.stream()
                    .filter(entry -> entry.type.isDir())
                    .filter(entry -> entry.getSize() >= neededSavings)
                    .min(Comparator.comparingInt(Entry::getSize))
                    .get().getSize();
        }

        FileSystem fillFileSystem() {
            FileSystem output = new FileSystem();
            commands.forEach(output::execute);
            return output;
        }
    }

    private static class FileSystem {
        Entry root = new Entry("/", FileType.DIR);
        Entry cd = root;
        List<Entry> allContents = new ArrayList<>();

        void execute(Command command) {
            if (command.type == CommandType.FILE || command.type == CommandType.DIR) {
                Entry newEntry = command.makeEntry();
                newEntry.setParent(cd);
                cd.addEntry(newEntry);
                allContents.add(newEntry);
            } else if (command.type == CommandType.CD) {
                if (command.cdLoc().equals("/")) {
                    cd = root;
                } else if (command.cdLoc().equals("..")) {
                    cd = cd.getParent();
                } else {
                    cd = cd.contentsMap.get(command.cdLoc());
                }
            }
        }
    }

    private static class Entry {
        final String name;
        final FileType type;
        final int size;

        Entry parent;

        List<Entry> contents = new ArrayList<>();
        Map<String, Entry> contentsMap = new HashMap<>();

        Entry(String name, FileType type) {
            this.name = name;
            this.type = type;
            this.size = NO_SIZE;
        }

        Entry(String name, FileType type, int size) {
            this.name = name;
            this.type = type;
            this.size = size;
        }

        void setParent(Entry parent) {
            this.parent = parent;
        }

        Entry getParent() {
            return parent;
        }

        int getSize() {
            if (type == FileType.DIR) {
                return contents.stream().mapToInt(Entry::getSize).sum();
            } else {
                return size;
            }
        }

        void addEntry(Entry newEntry) {
            contents.add(newEntry);
            contentsMap.put(newEntry.name, newEntry);
        }
    }

    private static class Command {
        final CommandType type;
        final String[] args;

        Command(String input) {
            this.type = CommandType.findType(input);
            args = input.split("\\s+");
        }

        String cdLoc() {
            if (type == CommandType.CD) {
                return args[2];
            }
            throw new RuntimeException("Not cd can't get location");
        }

        String name() {
            if (type == CommandType.DIR || type == CommandType.FILE) {
                return args[1];
            }
            throw new RuntimeException("Not file or dir can't get name");
        }

        int fileSize() {
            if (type == CommandType.FILE) {
                return Integer.parseInt(args[0]);
            }
            throw  new RuntimeException("Not file can't get size");
        }

        Entry makeEntry() {
            if (type == CommandType.FILE) {
                return new Entry(name(), FileType.FILE, fileSize());
            } else if (type == CommandType.DIR) {
                return new Entry(name(), FileType.DIR);
            }
            throw new RuntimeException("Not file or dir can't amke entry");
        }
    }

    private enum FileType {
        FILE(true, false),
        DIR(false, true);

        final boolean isFile;
        final boolean isDir;

        FileType(boolean isFile, boolean isDir) {
            this.isFile = isFile;
            this.isDir = isDir;
        }

        boolean isDir() {
            return isDir;
        }

        boolean isFile() {
            return isFile;
        }
    }

    private enum CommandType {
        CD(CommandType::isCd),
        LS(CommandType::isLs),
        DIR(CommandType::isDir),
        FILE(CommandType::isFile);

        final Predicate<String> isCommand;

        CommandType(Predicate<String> isCommand) {
            this.isCommand = isCommand;
        }

        boolean is(String input) {
            return isCommand.test(input);
        }

        static CommandType findType(String input) {
            Optional<CommandType> output = Arrays.stream(values())
                    .filter(type -> type.is(input))
                    .findFirst();

            if (output.isEmpty()) {
                throw new RuntimeException("Can't find command for input '%s'".formatted(input));
            }
            return output.get();
        }

        static boolean isCd(String input) {
            return input.startsWith("$ cd");
        }

        static boolean isLs(String input) {
            return input.startsWith("$ ls");
        }

        static boolean isDir(String input) {
            return input.startsWith("dir");
        }

        static boolean isFile(String input) {
            return input.split("\\s+")[0].matches("[0-9]+");
        }
    }
}
