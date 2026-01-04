package net.aeternum.lolwrcalc.ui;

import net.aeternum.lolwrcalc.agentProfiles.AgentProfile;
import net.aeternum.lolwrcalc.agentProfiles.ProfileManager;
import net.aeternum.lolwrcalc.util.ErrorCodes;
import net.aeternum.lolwrcalc.util.SelectionHandler;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class ProfileManagerDialogue implements Dialogue {
    private final ProfileManager profileManager;

    public ProfileManagerDialogue(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public Dialogue run(@NotNull UiParams params) {
        AtomicReference<Dialogue> retValue = new AtomicReference<>();

        UserInterface ui = params.ui();
        PrintStream ps = params.ps();
        Scanner sc = params.sc();

        if (ui.getProfileManagerDialogue() == null) ui.setProfileManagerDialogue(this);

        ps.println(profileManager.getStringRep());

        ps.print("""
                Options:
                1. Load Profile
                2. Create Profile
                3. Delete Profile
                4. Reload Profiles
                5. Exit
                
                Enter your choice [1..5]:\s""");

        SelectionHandler.handle(
                Map.ofEntries(
                        Map.entry(1, () -> {
                            try {
                                Optional<AgentProfile> profile = profileManager.getProfile(getIdentifier(ps, sc));
                                profile.ifPresentOrElse(agentProfile -> retValue.set(new AgentProfileDialogue(agentProfile)),
                                        () -> retValue.set(this));
                            } catch (IOException e) {
                                ps.println("Error loading profile!");
                                retValue.set(this);
                            }
                        }),
                        Map.entry(2, () -> {
                            AgentProfile profile = new AgentProfile(getIdentifier(ps, sc));
                            ui.run(new AgentProfileDialogue(profile));
                        }),
                        Map.entry(3, () -> {
                            try {
                                Optional<AgentProfile> profile = profileManager.getProfile(getIdentifier(ps, sc));
                                if (profile.isPresent()) Files.delete(ProfileManager.PATH.resolve(profile.get().getIdentifier()));
                            } catch (IOException e) {
                                ps.println("Error loading profile!");
                            } finally {
                                retValue.set(this);
                            }
                        }),
                        Map.entry(4, () -> {
                            profileManager.loadProfiles();
                            retValue.set(this);
                        }),
                        Map.entry(5, () -> retValue.set(null))
                ), sc, ps
        );

        return retValue.get();
    }

    private String getIdentifier(@NotNull PrintStream ps, @NonNull Scanner sc) {
        ps.print("Profile name: ");
        return sc.nextLine();
    }
}
