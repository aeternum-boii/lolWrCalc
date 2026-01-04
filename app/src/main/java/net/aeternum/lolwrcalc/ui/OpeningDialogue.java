package net.aeternum.lolwrcalc.ui;

import net.aeternum.lolwrcalc.agentProfiles.ProfileManager;
import net.aeternum.lolwrcalc.util.SelectionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class OpeningDialogue implements Dialogue {
    @Override
    public Dialogue run(@NotNull UiParams params) {
        AtomicReference<Dialogue> retValue = new AtomicReference<>(null);

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
                        Map.entry(1, () -> retValue.set(ui.getProfileManagerDialogue() != null ? ui.getProfileManagerDialogue() : new ProfileManagerDialogue(ProfileManager.instance))),
                        Map.entry(2, () -> retValue.set(ui.getProfileManagerDialogue() != null ? ui.getProfileManagerDialogue() : new ProfileManagerDialogue(ProfileManager.instance))),
                        Map.entry(3, () -> retValue.set(null))
                ), params.sc(), params.ps()
        );

        return retValue.get();
    }
}
