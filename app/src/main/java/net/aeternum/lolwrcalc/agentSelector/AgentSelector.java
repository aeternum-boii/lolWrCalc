package net.aeternum.lolwrcalc.agentSelector;

import net.aeternum.lolwrcalc.agentProfiles.AgentProfile;
import net.aeternum.lolwrcalc.wrapper.Lanes;
import net.aeternum.lolwrcalc.wrapper.Ranks;
import net.aeternum.lolwrcalc.wrapper.Wrapper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class AgentSelector {
    private final Wrapper w;
    public final AgentProfile p;

    public long matchups = 3L;
    public Ranks.WrappableRank rank = Wrapper.DEFAULT_RANK;

    public AgentSelector(@NotNull AgentProfile p) {
        w = new Wrapper();
        this.p = p;
    }

    public Wrapper.WinRateComparison[] getTopThreeMatchups(AgentProfile.Champion enemy) throws IOException {
        return Arrays.stream(w.matchup(
                new Wrapper.MatchupParamCommons(enemy, Lanes.getLane(p.getChoices().getFirst().role().name()), rank)
                        .expand(p.getChoices().stream().map(AgentProfile.Choice::champion))
                )).sorted(Comparator.comparing((wr) -> -wr.winrate()))
                .limit(matchups).toArray(Wrapper.WinRateComparison[]::new);
    }

}
