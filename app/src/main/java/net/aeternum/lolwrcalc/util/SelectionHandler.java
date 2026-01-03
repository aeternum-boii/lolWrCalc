package net.aeternum.lolwrcalc.util;

import net.aeternum.lolwrcalc.ui.UiParams;

import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

public class SelectionHandler {
    public static void handle(Map<Integer, Runnable> choices, Scanner sc, PrintStream ps, int lowerValid, int higherValid) {
        int choice = -1;
        boolean valid = false;

        while (!valid) {
            try {
                choice = sc.nextInt();
                valid = choice >= lowerValid && choice <= higherValid;
            } catch (InputMismatchException _) {
                sc.nextLine();
            } finally {
                if(sc.hasNextLine()) sc.nextLine();
            }
            if (!valid) ps.print("Invalid input! Try again: ");
        }

        Runnable r = choices.getOrDefault(choice, () -> {throw new IllegalArgumentException("Map does not contain valid choice!");});
        r.run();
    }

    public static void handle(Map<Integer, Runnable> choices, Scanner sc, PrintStream ps) {
        handle(choices, sc, ps, 1, choices.size());
    }

}
