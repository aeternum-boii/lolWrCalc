package net.aeternum.lolwrcalc.ui;

import net.aeternum.lolwrcalc.util.ErrorCodes;
import net.aeternum.lolwrcalc.agentProfiles.ProfileManager;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class OpeningDialogue implements Dialogue {
    @Override
    public Dialogue run(@NotNull PrintStream ps, @NotNull InputStream is, @NotNull UserInterface ui, Object... args) {
        Scanner sc = new Scanner(is);
        boolean valid = false;
        int choice = -1;

        while (!valid) {
            ps.print("""
                    Options:
                    1. Load Agent Profile (available\s""" + ProfileManager.instance.getProfileCount() + """
                    )
                    2. Create Agent Profile
                    3. Exit
                    
                    Enter your choice [1..3]:\s"""
            );

            try {
                choice = sc.nextInt();
                valid = choice >= 1 && choice <= 3;
            } catch (Exception e) {
                if(!(e instanceof NumberFormatException)) System.exit(ErrorCodes.INPUT_PARSING_ERROR);
            }
            if(!valid) ps.println("Invalid input!");
        }

        switch (choice) {
            case 1,2 -> ui.run(ui.getProfileManagerDialogue() != null ? ui.getProfileManagerDialogue() : new ProfileManagerDialogue(ProfileManager.instance));
            case 3 -> System.exit(0);
        }

        return this;
    }
}
