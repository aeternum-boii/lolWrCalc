package net.aeternum.lolwrcalc.agentSelector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.aeternum.lolwrcalc.agentProfiles.AgentProfile;
import net.aeternum.lolwrcalc.wrapper.Lanes;
import net.aeternum.lolwrcalc.wrapper.Wrapper;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class AgentSelector {
    private final Wrapper w;
    public final AgentProfile p;

    public AgentSelector(@NotNull AgentProfile p) {
        w = new Wrapper();
        this.p = p;
    }

    public record WinRateComparison(AgentProfile.Champion champion, double winrate, int matches) {}

    public WinRateComparison[] getTopThreeMatchups(AgentProfile.Champion enemy) throws IOException {
        List<WinRateComparison> winRateComparisonList = new LinkedList<>();

        for (AgentProfile.Choice choice : p.getChoices()) {
            w.matchup(new Wrapper.MatchupParam(choice.champion(), enemy, Lanes.getLane(choice.role().name()), Wrapper.DEFAULT_RANK));

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(w.OUTPATH.toFile());
            JsonNode firstElement = root.get(0);
            JsonNode dataNode = firstElement.get("data");
            double winrate = dataNode.get("winrate").asDouble();
            int matches = Integer.parseInt(dataNode.get("number_of_games").asText().replace(",", ""));

            winRateComparisonList.addLast(new WinRateComparison(choice.champion(), winrate, matches));
            w.cleanup();
        }

        return winRateComparisonList.stream().sorted(Comparator.comparing((WinRateComparison wr) -> -wr.winrate())).limit(3L).toArray(WinRateComparison[]::new);
    }

}
