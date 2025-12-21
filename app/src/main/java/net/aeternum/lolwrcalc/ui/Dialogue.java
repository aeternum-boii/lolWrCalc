package net.aeternum.lolwrcalc.ui;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.PrintStream;

public interface Dialogue {
    Dialogue run(@NotNull PrintStream ps, @NotNull InputStream is, @NotNull UserInterface ui, Object... args);
}
