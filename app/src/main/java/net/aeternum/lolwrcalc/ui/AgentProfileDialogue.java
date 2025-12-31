package net.aeternum.lolwrcalc.ui;

import net.aeternum.lolwrcalc.agentProfiles.AgentProfile;
import net.aeternum.lolwrcalc.agentProfiles.ProfileManager;
import net.aeternum.lolwrcalc.util.ErrorCodes;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class AgentProfileDialogue implements Dialogue {

    private final AgentProfile agentProfile;

    public AgentProfileDialogue(AgentProfile agentProfile) {
        this.agentProfile = agentProfile;
    }

    @Override
    public Dialogue run(@NotNull PrintStream ps, @NotNull InputStream is, @NotNull UserInterface ui, Object... args) {
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


        int choice = -1;
        boolean valid = false;

        while (!valid) {
            try {
                Scanner sc = new Scanner(is);
                choice = sc.nextInt();
                valid = choice >= 1 && choice <= 6;
            } catch (Exception e) {
                if (!(e instanceof NumberFormatException)) System.exit(1);
            }
            if (!valid) ps.println("Invalid input!");
        }

        switch (choice) {
            case 1 -> {
                try {
                    ProfileManager.instance.saveProfile(agentProfile);
                } catch (IOException e) {
                    ps.println("Error saving profile - exiting!");
                    System.exit(ErrorCodes.PROFILE_IO_ERROR);
                }
                ui.run(ui.getProfileManagerDialogue() != null ? ui.getProfileManagerDialogue() : new ProfileManagerDialogue(ProfileManager.instance));
            }
            case 2 -> {
                agentProfile.add(parseChoice(ps, is, ui));
                run(ps, is, ui, args);
            }
            case 3 -> {
                agentProfile.remove(parseChoice(ps, is, ui));
                run(ps, is, ui, args);
            }
            case 4 -> {
                new AgentSelectorDialogue(agentProfile).run(ps, is, ui, args);
            }
            case 5 ->
                    ui.run(ui.getProfileManagerDialogue() != null ? ui.getProfileManagerDialogue() : new ProfileManagerDialogue(ProfileManager.instance));
            case 6 -> System.exit(0);
        }

        return this;
    }

    @Contract("_, _, _ -> new")
    private @NotNull AgentProfile.Choice parseChoice(@NotNull PrintStream ps, InputStream is, UserInterface ui) {
        ps.println("Please enter the champion and role (separated by a space):");

        boolean valid = false;
        String[] input = null;
        while (!valid) {
            Scanner sc = new Scanner(is);
            input = sc.nextLine().trim().split(" ");
            if (input.length != 2) {
                ps.println("Invalid input!");
            } else valid = true;
        }

        String[] out = new String[1];
        new StringMatcherDialogue().run(ps, is, ui, out, input[0], AgentProfile.champions);
        AgentProfile.Champion champion = AgentProfile.Champion.valueOf(out[0]);
        new StringMatcherDialogue().run(ps, is, ui, out, input[1], AgentProfile.roles);
        AgentProfile.Role role = AgentProfile.Role.valueOf(out[0]);

        return new AgentProfile.Choice(champion, role);
    }
}
