package net.aeternum.lolwrcalc.wrapper;

import net.aeternum.lolwrcalc.util.StringMatcher;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class Lanes {
    public enum WrappableLane {
        top,
        jungle,
        mid,
        bot,
        sup
    }

    private final static Map<String, WrappableLane> laneMap = Map.ofEntries(
            Map.entry("top", WrappableLane.top),
            Map.entry("jg", WrappableLane.jungle),
            Map.entry("jng", WrappableLane.jungle),
            Map.entry("jungle", WrappableLane.jungle),
            Map.entry("mid", WrappableLane.mid),
            Map.entry("middle", WrappableLane.mid),
            Map.entry("bottom", WrappableLane.bot),
            Map.entry("adc", WrappableLane.bot),
            Map.entry("support", WrappableLane.sup),
            Map.entry("sup", WrappableLane.sup),
            Map.entry("supp", WrappableLane.sup)
    );

    public static WrappableLane getLane(String lane) {
        Optional<String> s = StringMatcher.closestIfAbsent(laneMap.keySet().toArray(new String[0]), lane);
        AtomicReference<WrappableLane> ret = new AtomicReference<>();
        s.ifPresentOrElse(s1 -> ret.set(laneMap.get(s1)), () -> ret.set(laneMap.get(lane)));
        return ret.get();
    }
}
