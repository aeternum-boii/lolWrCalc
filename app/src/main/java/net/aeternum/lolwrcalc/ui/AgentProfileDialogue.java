package net.aeternum.lolwrcalc.ui;

import net.aeternum.lolwrcalc.agentProfiles.AgentProfile;
import net.aeternum.lolwrcalc.agentProfiles.ProfileManager;
import net.aeternum.lolwrcalc.util.SelectionHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class AgentProfileDialogue implements Dialogue {

    @NotNull
    private final AgentProfile agentProfile;

    public AgentProfileDialogue(@NotNull AgentProfile agentProfile) {
        this.agentProfile = agentProfile;
    }

    @Override
    public Dialogue run(@NotNull UiParams params) {
        AtomicReference<Dialogue> retValue = new AtomicReference<>(null);
        PrintStream ps = params.ps();
        Scanner sc = params.sc();
        UserInterface ui = params.ui();

        ps.println(agentProfile.stringRep());
        ps.print("""
                Options:
                1. Save Profile
                2. Add Champion/Role
                3. Remove Champion/Role
                4. Calculate WR against Champion/Role
                5. Back
                6. Exit
                
                Enter your choice [1..6]:\s""");

        SelectionHandler.handle(
                Map.ofEntries(
                        Map.entry(1, () -> {
                            try {
                                ProfileManager.instance.saveProfile(agentProfile);
                            } catch (IOException e) {
                                ps.println("Error saving profile!");
                                retValue.set(this);
                                return;
                            }
                            retValue.set(ui.getProfileManagerDialogue() != null ? ui.getProfileManagerDialogue() : new ProfileManagerDialogue(ProfileManager.instance));
                        }),
                        Map.entry(2, () -> {
                            agentProfile.add(parseChoice(ps, sc, ui));
                            retValue.set(this);
                        }),
                        Map.entry(3, () -> {
                            agentProfile.remove(parseChoice(ps, sc, ui));
                            retValue.set(this);
                        }),
                        Map.entry(4, () -> {
                            new AgentSelectorDialogue(agentProfile).run(params);
                        }),
                        Map.entry(5, () -> {
                            retValue.set(ui.getProfileManagerDialogue() != null ? ui.getProfileManagerDialogue() : new ProfileManagerDialogue(ProfileManager.instance));
                        }),
                        Map.entry(6, () -> retValue.set(null))
                ), sc, ps
        );

        return retValue.get();
    }

    @Contract("_, _, _ -> new")
    private @NotNull AgentProfile.Choice parseChoice(@NotNull PrintStream ps, Scanner sc, UserInterface ui) {
        ps.println("Please enter the champion and role (separated by a space):");

        boolean valid = false;
        String[] input = null;
        while (!valid) {
            input = sc.nextLine().trim().split(" ");
            if (input.length != 2) {
                ps.println("Invalid input!");
            } else valid = true;
        }

        String[] out = new String[1];
        new StringMatcherDialogue().run(new UiParams(ps, sc, ui, out, input[0], AgentProfile.champions));
        AgentProfile.Champion champion = AgentProfile.Champion.valueOf(out[0]);
        new StringMatcherDialogue().run(new UiParams(ps, sc, ui, out, input[1], AgentProfile.roles));
        AgentProfile.Role role = AgentProfile.Role.valueOf(out[0]);

        return new AgentProfile.Choice(champion, role);
    }
}
