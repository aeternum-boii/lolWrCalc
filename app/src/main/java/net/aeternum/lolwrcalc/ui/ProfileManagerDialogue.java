package net.aeternum.lolwrcalc.ui;

import net.aeternum.lolwrcalc.agentProfiles.AgentProfile;
import net.aeternum.lolwrcalc.agentProfiles.ProfileManager;
import net.aeternum.lolwrcalc.util.ErrorCodes;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Optional;
import java.util.Scanner;

public class ProfileManagerDialogue implements Dialogue {
    private final ProfileManager profileManager;

    public ProfileManagerDialogue(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public Dialogue run(@NotNull PrintStream ps, @NotNull InputStream is, @NotNull UserInterface ui, Object... args) {
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

        boolean valid = false;
        int choice = -1;
        while (!valid) {
            try {
                Scanner sc = new Scanner(is);
                choice = sc.nextInt();
                valid = choice >= 1 && choice <= 5;
            } catch (Exception e) {
                if (!(e instanceof NumberFormatException)) System.exit(ErrorCodes.INPUT_PARSING_ERROR);
            }
            if (!valid) ps.println("Invalid input!");
        }

        switch (choice) {
            case 1 -> {
                try {
                    Optional<AgentProfile> profile = profileManager.getProfile(getIdentifier(ps, is));
                    profile.ifPresentOrElse(agentProfile -> ui.run(new AgentProfileDialogue(agentProfile)),
                            () -> run(ps, is, ui, args));
                } catch (IOException e) {
                    ps.println("Error loading profile!");
                    run(ps, is, ui, args);
                }
            }
            case 2 -> {
                AgentProfile profile = new AgentProfile(getIdentifier(ps, is));
                ui.run(new AgentProfileDialogue(profile));
            }
            case 3 -> {
                try {
                    Optional<AgentProfile> profile = profileManager.getProfile(getIdentifier(ps, is));
                    if (profile.isPresent()) Files.delete(ProfileManager.PATH.resolve(profile.get().getIdentifier()));
                } catch (IOException e) {
                    ps.println("Error loading profile!");
                } finally {
                    run(ps, is, ui, args);
                }
            }
            case 4 -> {
                profileManager.loadProfiles();
                run(ps, is, ui, args);
            }
            case 5 -> System.exit(0);
        }

        return this;
    }

    private String getIdentifier(@NotNull PrintStream ps, InputStream is) {
        Scanner sc = new Scanner(is);
        ps.print("Profile name: ");
        return sc.nextLine();
    }
}
