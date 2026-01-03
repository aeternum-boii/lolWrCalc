package net.aeternum.lolwrcalc.wrapper;

import net.aeternum.lolwrcalc.util.StringMatcher;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class Ranks {
    public static String[] ranks = Arrays.stream(WrappableRank.values()).map(Enum::toString).toArray(String[]::new);
    public enum WrappableRank {
        challenger,
        grandmaster_plus,
        grandmaster,
        master_plus,
        master,
        diamond_plus,
        diamond,
        emerald,
        platinum_plus,
        platinum,
        gold_plus,
        gold,
        silver,
        bronze,
        iron,
        unranked,
        all,
        onetrick
    }

    public final static Map<String, WrappableRank> rankMap = Map.<String, WrappableRank>ofEntries(
            Map.entry("challenger", WrappableRank.challenger),
            Map.entry("chall", WrappableRank.challenger),
            Map.entry("c", WrappableRank.challenger),

            Map.entry("grandmaster_plus", WrappableRank.grandmaster_plus),
            Map.entry("grandmaster+", WrappableRank.grandmaster_plus),
            Map.entry("gm+", WrappableRank.grandmaster_plus),

            Map.entry("grandmaster", WrappableRank.grandmaster),
            Map.entry("grandm", WrappableRank.grandmaster),
            Map.entry("gm", WrappableRank.grandmaster),

            Map.entry("master_plus", WrappableRank.master_plus),
            Map.entry("master+", WrappableRank.master_plus),
            Map.entry("mast+", WrappableRank.master_plus),
            Map.entry("m+", WrappableRank.master_plus),

            Map.entry("master", WrappableRank.master),
            Map.entry("mast", WrappableRank.master),
            Map.entry("m", WrappableRank.master),

            Map.entry("diamond_plus", WrappableRank.diamond_plus),
            Map.entry("diamond+", WrappableRank.diamond_plus),
            Map.entry("diam+", WrappableRank.diamond_plus),
            Map.entry("dia+", WrappableRank.diamond_plus),
            Map.entry("d+", WrappableRank.diamond_plus),

            Map.entry("diamond", WrappableRank.diamond),
            Map.entry("diam", WrappableRank.diamond),
            Map.entry("dia", WrappableRank.diamond),
            Map.entry("d", WrappableRank.diamond),

            Map.entry("emerald", WrappableRank.emerald),
            Map.entry("eme", WrappableRank.emerald),
            Map.entry("em", WrappableRank.emerald),
            Map.entry("e", WrappableRank.emerald),

            Map.entry("platinum+", WrappableRank.platinum_plus),
            Map.entry("plat+", WrappableRank.platinum_plus),
            Map.entry("pl+", WrappableRank.platinum_plus),
            Map.entry("p+", WrappableRank.platinum_plus),

            Map.entry("platinum", WrappableRank.platinum),
            Map.entry("plat", WrappableRank.platinum),
            Map.entry("pl", WrappableRank.platinum),
            Map.entry("p", WrappableRank.platinum),

            Map.entry("gold_plus", WrappableRank.gold_plus),
            Map.entry("gold+", WrappableRank.gold_plus),
            Map.entry("g+", WrappableRank.gold_plus),

            Map.entry("gold", WrappableRank.gold),
            Map.entry("g", WrappableRank.gold),

            Map.entry("silver", WrappableRank.silver),
            Map.entry("silv", WrappableRank.silver),
            Map.entry("s", WrappableRank.silver),

            Map.entry("bronze", WrappableRank.bronze),
            Map.entry("br", WrappableRank.bronze),
            Map.entry("b", WrappableRank.bronze),

            Map.entry("iron", WrappableRank.iron),
            Map.entry("i", WrappableRank.iron),

            Map.entry("unranked", WrappableRank.unranked),
            Map.entry("unrank", WrappableRank.unranked),
            Map.entry("unr", WrappableRank.unranked),
            Map.entry("un", WrappableRank.unranked),
            Map.entry("none", WrappableRank.unranked),
            Map.entry("null", WrappableRank.unranked),
            Map.entry("-", WrappableRank.unranked),

            Map.entry("all", WrappableRank.all),

            Map.entry("otp", WrappableRank.onetrick),
            Map.entry("1trick", WrappableRank.onetrick),
            Map.entry("1-trick", WrappableRank.onetrick),
            Map.entry("1trickpony", WrappableRank.onetrick),
            Map.entry("onetrickpony", WrappableRank.onetrick),
            Map.entry("onetrick", WrappableRank.onetrick)
    );

    public static WrappableRank getRank(String rank) {
        Optional<String> s = StringMatcher.closestIfAbsent(rankMap.keySet().toArray(new String[0]), rank);
        AtomicReference<WrappableRank> ret = new AtomicReference<>();
        s.ifPresentOrElse(s1 -> ret.set(rankMap.get(s1)), () -> ret.set(rankMap.get(rank)));
        return ret.get();
    }
}
