package net.aeternum.lolwrcalc.ui;

import net.aeternum.lolwrcalc.agentProfiles.AgentProfile;
import net.aeternum.lolwrcalc.agentProfiles.ProfileManager;
import net.aeternum.lolwrcalc.agentSelector.AgentSelector;
import net.aeternum.lolwrcalc.util.ErrorCodes;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class AgentSelectorDialogue implements Dialogue {
    private final AgentSelector selector;

    public AgentSelectorDialogue(@NotNull AgentProfile p) {
        selector = new AgentSelector(p);
    }

    @Override
    public Dialogue run(@NotNull PrintStream ps, @NotNull InputStream is, @NotNull UserInterface ui, Object... args) {
        if(ui.getAgentSelectorDialogue() == null) ui.setAgentSelectorDialogue(this);

        ps.print("""
                Please provide the enemy Champion:\s""");

        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();

        String[] out = new String[] {""};
        new StringMatcherDialogue().run(ps, is, ui, out, s, AgentProfile.champions);

        ps.println("Finding optimal matchups against " + out[0]);
        try {
            AgentSelector.WinRateComparison[] matchups = selector.getTopThreeMatchups(AgentProfile.Champion.valueOf(out[0]));

            for (AgentSelector.WinRateComparison matchup : matchups) {
                ps.println("Matchup for " + matchup.champion() + " has a winrate of " + matchup.winrate() + "(at " + matchup.matches() + " matches)");
            }
        } catch (IOException e) {
            ps.println("Failed I/O while trying to find matchups");
        } finally {
            new AgentProfileDialogue(selector.p).run(ps,is,ui,args);
        }

        return this;
    }
}
