package net.aeternum.lolwrcalc.ui;

import net.aeternum.lolwrcalc.wrapper.Wrapper;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.function.Supplier;

public class UserInterface {
    private final PrintStream ps;
    private final Scanner sc;
    private Dialogue profileManagerDialogue = null;
    private Dialogue agentSelectorDialogue = null;

    public UserInterface(@NotNull PrintStream ps, @NotNull InputStream is) {
        this.ps = ps;
        this.sc = new Scanner(is);
    }

    public UserInterface() {
        this(System.out, System.in);
    }

    public void run(@NotNull Dialogue dialogue) {
        dialogue.run(new UiParams(ps, sc, this));
    }

    public Dialogue getProfileManagerDialogue() {
        return profileManagerDialogue;
    }

    public void setProfileManagerDialogue(Dialogue profileManagerDialogue) {
        this.profileManagerDialogue = profileManagerDialogue;
    }

    public Dialogue getAgentSelectorDialogue() {
        return agentSelectorDialogue;
    }

    public void setAgentSelectorDialogue(Dialogue agentSelectorDialogue) {
        this.agentSelectorDialogue = agentSelectorDialogue;
    }
}
