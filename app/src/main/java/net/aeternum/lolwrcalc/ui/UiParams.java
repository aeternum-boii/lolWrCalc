package net.aeternum.lolwrcalc.ui;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.Scanner;

public record UiParams(@NotNull PrintStream ps, @NotNull Scanner sc, @NotNull UserInterface ui, Object... args) {
}
