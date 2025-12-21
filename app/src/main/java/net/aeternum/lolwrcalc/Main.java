package net.lolwrcalc.aeternum;

import net.lolwrcalc.aeternum.ui.OpeningDialogue;
import net.lolwrcalc.aeternum.ui.UserInterface;

public class Main {
    public static void main(String[] args) {
        new UserInterface().run(new OpeningDialogue());
    }
}
