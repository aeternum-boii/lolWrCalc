package net.lolwrcalc.aeternum.util;

import net.lolwrcalc.aeternum.ui.Dialogue;
import net.lolwrcalc.aeternum.ui.UserInterface;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class StringMatcher {

    public static class StringMatcherDialogue implements Dialogue {

        @Override
        public Dialogue run(@NotNull PrintStream ps, @NotNull InputStream is, @NotNull UserInterface ui, Object @NotNull ... args) {
            // args[0][0] = output (String), args[1] input, args[2] String Array to fuzzy match on
            String[] output = (String[]) args[0];
            AtomicReference<String> s = new AtomicReference<>((String) args[1]);
            String[] list = (String[]) args[2];

            AtomicBoolean valid = new AtomicBoolean(false);
            while(!valid.get()) {
                Optional<String> closest = closestIfAbsent(list, s.get());
                closest.ifPresentOrElse(string -> {
                    ps.println("Could not find \"" + s + "\", did you mean \"" + string + "\"?");
                    ps.print("Enter your choice [Y/N]:");
                    Scanner sc = new Scanner(is);

                    String choice = sc.nextLine();
                    if (choice.equalsIgnoreCase("y")) {
                        output[0] = string;
                        valid.set(true);
                    } else if (choice.equalsIgnoreCase("n")) {
                        ps.print("What did you mean then: ");
                        s.set(sc.nextLine());
                    }
                    else ps.println("Invalid input!");
                }, () -> {
                    output[0] = s.get();
                    valid.set(true);
                });
            }

            return this;
        }
    }

    public static Optional<String> closestIfAbsent(@NotNull String @NotNull [] array, @NotNull String s) {
        for (String v : array) {
            if (v.equals(s)) {
                return Optional.empty();
            }
        }

        String best = null;
        int bestScore = Integer.MAX_VALUE;

        for (String v : array) {
            int score = score(s, v);
            if (score < bestScore) {
                bestScore = score;
                best = v;
            }
        }

        return Optional.ofNullable(best);
    }

    private static int score(@NotNull String a, String b) {
        if (a.equalsIgnoreCase(b)) {
            return caseDifferencePenalty(a, b);
        }

        int distance = levenshtein(a.toLowerCase(), b.toLowerCase());
        return distance * 10 + caseDifferencePenalty(a, b);
    }

    private static int caseDifferencePenalty(@NotNull String a, @NotNull String b) {
        int penalty = 0;
        int len = Math.min(a.length(), b.length());
        for (int i = 0; i < len; i++) {
            if (Character.toLowerCase(a.charAt(i)) ==
                    Character.toLowerCase(b.charAt(i)) &&
                    a.charAt(i) != b.charAt(i)) {
                penalty++;
            }
        }
        return penalty;
    }

    private static int levenshtein(@NotNull String a, @NotNull String b) {
        int[] prev = new int[b.length() + 1];
        int[] curr = new int[b.length() + 1];

        for (int j = 0; j <= b.length(); j++) {
            prev[j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
            curr[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                curr[j] = Math.min(
                        Math.min(curr[j - 1] + 1, prev[j] + 1),
                        prev[j - 1] + cost
                );
            }
            int[] tmp = prev;
            prev = curr;
            curr = tmp;
        }

        return prev[b.length()];
    }
}
