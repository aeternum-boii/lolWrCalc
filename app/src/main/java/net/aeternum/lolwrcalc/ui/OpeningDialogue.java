package net.aeternum.lolwrcalc.ui;

import net.aeternum.lolwrcalc.util.ErrorCodes;
import net.aeternum.lolwrcalc.agentProfiles.ProfileManager;
import net.aeternum.lolwrcalc.util.SelectionHandler;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;

public class OpeningDialogue implements Dialogue {
    @Override
    public void run(@NotNull UiParams params) {
        params.ps().print("""
                    Options:
                    1. Load Agent Profile (available\s""" + ProfileManager.instance.getProfileCount() + """
                    )
                    2. Create Agent Profile
                    3. Exit
                    
                    Enter your choice [1..3]:\s"""
        );

        UserInterface ui = params.ui();
        SelectionHandler.handle(
                Map.ofEntries(
                        Map.entry(1, () -> ui.run(ui.getProfileManagerDialogue() != null ? ui.getProfileManagerDialogue() : new ProfileManagerDialogue(ProfileManager.instance))),
                        Map.entry(2, () -> ui.run(ui.getProfileManagerDialogue() != null ? ui.getProfileManagerDialogue() : new ProfileManagerDialogue(ProfileManager.instance))),
                        Map.entry(3, () -> System.exit(0))
                ), params.sc(), params.ps()
        );
    }
}
