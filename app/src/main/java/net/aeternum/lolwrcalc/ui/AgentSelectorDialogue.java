package net.aeternum.lolwrcalc.ui;

import net.aeternum.lolwrcalc.agentProfiles.AgentProfile;
import net.aeternum.lolwrcalc.agentSelector.AgentSelector;
import net.aeternum.lolwrcalc.util.SelectionHandler;
import net.aeternum.lolwrcalc.wrapper.Ranks;
import net.aeternum.lolwrcalc.wrapper.Wrapper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class AgentSelectorDialogue implements Dialogue {
    private final AgentSelector selector;

    public AgentSelectorDialogue(@NotNull AgentProfile p) {
        selector = new AgentSelector(p);
    }

    @Override
    public Dialogue run(@NotNull UiParams params) {
        AtomicReference<Dialogue> retValue = new AtomicReference<>(null);
        UserInterface ui = params.ui();
        PrintStream ps = params.ps();
        Scanner sc = params.sc();

        if(ui.getAgentSelectorDialogue() == null) ui.setAgentSelectorDialogue(this);

        ps.println("Stats for Top " + selector.matchups + " Agents. From Rank " + selector.rank + ".");
        ps.print("""
                Options:
                1. Provide the enemy Champion
                2. Set Number of Matchups
                3. Set Rank
                4. Back
                5. Exit
                
                Enter your choice [1..5]:\s""");

        SelectionHandler.handle(
                Map.ofEntries(
                        Map.entry(1, () -> {
                            ps.print("Select a Matchup: ");
                            String s = sc.nextLine();

                            String[] out = new String[] {""};
                            new StringMatcherDialogue().run(new UiParams(ps, sc, ui, out, s, AgentProfile.champions));

                            ps.println("Finding optimal matchups against " + out[0]);
                            try {
                                Wrapper.WinRateComparison[] matchups = selector.getTopThreeMatchups(AgentProfile.Champion.valueOf(out[0]));

                                for (Wrapper.WinRateComparison matchup : matchups) {
                                    ps.println("Matchup for " + matchup.champion() + " has a winrate of " + matchup.winrate() + "(at " + matchup.matches() + " matches)");
                                }
                            } catch (IOException e) {
                                ps.println("Failed I/O while trying to find matchups");
                            } finally {
                                retValue.set(this);
                            }
                        }),
                        Map.entry(2, () -> {
                            ps.print("Select number of displayed Matchups [0 < x < 2^31]: ");

                            boolean valid = false;
                            long n = selector.matchups;
                            while (!valid) {
                                try {
                                    n = Integer.parseInt(sc.nextLine());
                                    valid = n > 0;
                                }  catch (NumberFormatException _) {
                                    ps.println("Please provide a proper integer: ");
                                }
                            }
                            selector.matchups = n;

                            retValue.set(this);
                        }),
                        Map.entry(3, () -> {
                            ps.print("Select a Rank: ");
                            String s = sc.nextLine();

                            String[] out = new String[] {""};
                            new StringMatcherDialogue().run(new UiParams(ps, sc, ui, out, s, Ranks.rankMap.keySet().toArray(new String[0])));

                            selector.rank = Ranks.rankMap.get(s);

                            retValue.set(this);
                        }),
                        Map.entry(4, () -> retValue.set(new AgentProfileDialogue(selector.p))),
                        Map.entry(5, () -> retValue.set(null))
                ), sc, ps
        );

        return retValue.get();
    }
}
