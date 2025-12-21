package net.aeternum.lolwrcalc.ui;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.function.Supplier;

public class UserInterface {
    private final PrintStream ps;
    private final InputStream is;
    private Dialogue profileManagerDialogue = null;

    public UserInterface(@NotNull PrintStream ps, @NotNull InputStream is) {
        this.ps = ps;
        this.is = is;
    }

    public UserInterface() {
        this(System.out, System.in);
    }

    public void run(@NotNull Dialogue dialogue) {
        dialogue.run(ps, is, this);
    }

    public Dialogue getProfileManagerDialogue() {
        return profileManagerDialogue;
    }

    public void setProfileManagerDialogue(Dialogue profileManagerDialogue) {
        this.profileManagerDialogue = profileManagerDialogue;
    }
}
