package net.aeternum.lolwrcalc;

import net.aeternum.lolwrcalc.ui.OpeningDialogue;
import net.aeternum.lolwrcalc.ui.UserInterface;

public class Main {
    public static void main(String[] args) {
        new UserInterface().run(new OpeningDialogue());
    }
}
