package net.aeternum.lolwrcalc.ui;

import net.aeternum.lolwrcalc.util.StringMatcher;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class StringMatcherDialogue implements Dialogue {

    @Override
    public Dialogue run(@NotNull PrintStream ps, @NotNull InputStream is, @NotNull UserInterface ui, Object @NotNull ... args) {
        // args[0][0] = output (String), args[1] input, args[2] String Array to fuzzy match on
        String[] output = (String[]) args[0];
        AtomicReference<String> s = new AtomicReference<>((String) args[1]);
        String[] list = (String[]) args[2];

        AtomicBoolean valid = new AtomicBoolean(false);
        while (!valid.get()) {
            Optional<String> closest = StringMatcher.closestIfAbsent(list, s.get());
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
                } else ps.println("Invalid input!");
            }, () -> {
                output[0] = s.get();
                valid.set(true);
            });
        }

        return this;
    }
}
