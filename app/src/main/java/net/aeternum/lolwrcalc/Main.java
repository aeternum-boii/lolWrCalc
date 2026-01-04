package net.aeternum.lolwrcalc;

import net.aeternum.lolwrcalc.ui.OpeningDialogue;
import net.aeternum.lolwrcalc.ui.UserInterface;
import net.aeternum.lolwrcalc.util.ErrorCodes;
import org.jspecify.annotations.NonNull;

public class Main {
    private static int EXIT_CODE = 0;

    public static void main(String[] args) {
        new UserInterface().run(new OpeningDialogue());
        System.exit(EXIT_CODE);
    }

    public static void setExitCode(int exitCode) {
        EXIT_CODE = exitCode;
    }

    public static void setExitCode(ErrorCodes.@NonNull EXIT_CODE exitCode) {
        EXIT_CODE = exitCode.ordinal();
    }
}
