package seedu.coursepilot.logic.parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides context-aware autocomplete suggestions for the command box.
 * Suggestions are based on the current input text and follow the command syntax.
 */
public class CommandAutoCompleter {

    private static final List<String> COMMAND_WORDS = List.of(
            "add", "clear", "delete", "edit", "exit", "find", "help", "list", "select");

    private static final List<String> ADD_FLAGS = List.of("-student", "-tutorial");
    private static final List<String> DELETE_FLAGS = List.of("-student", "-tutorial");
    private static final List<String> LIST_FLAGS = List.of("-student", "-tutorial");
    private static final List<String> FIND_FLAGS = List.of("/phone", "/email", "/matric");

    private static final List<String> STUDENT_PREFIXES = List.of(
            "/name", "/phone", "/email", "/matric", "/tag");
    private static final List<String> TUTORIAL_PREFIXES = List.of(
            "/code", "/day", "/timeslot", "/capacity");
    private static final List<String> EDIT_PREFIXES = List.of(
            "/name", "/phone", "/email", "/matric", "/tag");

    /**
     * Returns autocomplete suggestions based on the current input text.
     *
     * @param text The current text in the command box.
     * @return A list of suggestion strings to display, may be empty.
     */
    public List<String> getSuggestions(String text) {
        if (text == null || text.isEmpty()) {
            return COMMAND_WORDS;
        }

        String trimmed = text.stripLeading();
        String[] parts = trimmed.split("\\s+");

        if (parts.length == 0) {
            return COMMAND_WORDS;
        }

        String commandWord = parts[0].toLowerCase();

        // State 1: Typing the command word (no space after yet)
        if (!text.contains(" ")) {
            return filterByPrefix(COMMAND_WORDS, commandWord);
        }

        // State 2+: Command word is complete
        if (!COMMAND_WORDS.contains(commandWord)) {
            return Collections.emptyList();
        }

        return getSuggestionsForCommand(commandWord, trimmed, parts);
    }

    /**
     * Returns suggestions for a specific command based on what has been typed after the command word.
     */
    private List<String> getSuggestionsForCommand(String command, String fullText, String[] parts) {
        switch (command) {
        case "add":
            return getAddSuggestions(fullText, parts);
        case "delete":
            return getFlagSuggestions(DELETE_FLAGS, parts);
        case "list":
            return getFlagSuggestions(LIST_FLAGS, parts);
        case "find":
            return getFlagSuggestions(FIND_FLAGS, parts);
        case "edit":
            return getEditSuggestions(fullText, parts);
        case "clear":
        case "help":
        case "exit":
        case "select":
            return Collections.emptyList();
        default:
            return Collections.emptyList();
        }
    }

    /**
     * Returns suggestions for the add command, which has sub-flags and then prefixes.
     */
    private List<String> getAddSuggestions(String fullText, String[] parts) {
        if (parts.length < 2) {
            return ADD_FLAGS;
        }

        String flag = parts[1];

        // Still typing the flag
        if (parts.length == 2 && !fullText.endsWith(" ")) {
            return filterByPrefix(ADD_FLAGS, flag);
        }

        // Flag is complete, suggest prefixes
        if ("-student".equals(flag)) {
            return getUnusedPrefixes(STUDENT_PREFIXES, fullText);
        } else if ("-tutorial".equals(flag)) {
            return getUnusedPrefixes(TUTORIAL_PREFIXES, fullText);
        }

        return Collections.emptyList();
    }

    /**
     * Returns suggestions for the edit command (index + prefixes).
     */
    private List<String> getEditSuggestions(String fullText, String[] parts) {
        // Need at least the index before suggesting prefixes
        if (parts.length < 2) {
            return Collections.emptyList();
        }
        return getUnusedPrefixes(EDIT_PREFIXES, fullText);
    }

    /**
     * Returns flag suggestions, filtering by what the user has started typing.
     */
    private List<String> getFlagSuggestions(List<String> flags, String[] parts) {
        if (parts.length == 1) {
            return flags;
        }
        if (parts.length == 2) {
            return filterByPrefix(flags, parts[1]);
        }
        return Collections.emptyList();
    }

    /**
     * Returns prefixes that have not yet been used in the current input.
     * The /tag prefix is always suggested since it can be used multiple times.
     */
    private List<String> getUnusedPrefixes(List<String> allPrefixes, String fullText) {
        Set<String> usedPrefixes = Arrays.stream(fullText.split("\\s+"))
                .filter(word -> word.startsWith("/"))
                .collect(Collectors.toSet());

        return allPrefixes.stream()
                .filter(prefix -> "/tag".equals(prefix) || !usedPrefixes.contains(prefix))
                .collect(Collectors.toList());
    }

    /**
     * Filters a list of candidates to those that start with the given prefix.
     */
    private List<String> filterByPrefix(List<String> candidates, String prefix) {
        if (prefix.isEmpty()) {
            return candidates;
        }
        return candidates.stream()
                .filter(c -> c.startsWith(prefix) && !c.equals(prefix))
                .collect(Collectors.toList());
    }
}
