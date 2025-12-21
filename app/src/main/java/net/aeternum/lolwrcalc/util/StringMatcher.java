package net.aeternum.lolwrcalc.util;

import net.aeternum.lolwrcalc.ui.Dialogue;
import net.aeternum.lolwrcalc.ui.UserInterface;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class StringMatcher {

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
