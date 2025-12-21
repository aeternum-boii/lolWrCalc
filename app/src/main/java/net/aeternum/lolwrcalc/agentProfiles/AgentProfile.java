package net.aeternum.lolwrcalc.agentProfiles;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgentProfile {
    public String getIdentifier() {
        return name;
    }

    public String stringRep() {
        StringBuilder sb = new StringBuilder();

        sb.append("Profile name: ").append(name).append("\nCurrent Choices: \n");
        for(Choice c : choices) sb.append("\t").append(c.toCall()).append("\n");

        return sb.toString();
    }

    private final String name;
    private final List<Choice> choices = new ArrayList<>();

    public AgentProfile(String name) {
        this.name = name;
    }

    public List<Choice> getChoices() {
        return List.copyOf(choices);
    }

    public void add(Choice choice) {
        choices.add(choice);
        choices.sort(null);
    }

    public void remove(Choice choice) {
        choices.remove(choice);
    }

    public record Choice(Champion champion, Role role) implements Comparable<Choice> {
        public @NotNull String toCall() {
            return champion.name().toLowerCase() + " " + role.name().toLowerCase();
        }

        @Override
        public int compareTo(@NotNull Choice o) {
            int cmp = role.compareTo(o.role);
            if(cmp != 0) return cmp;
            return champion.compareTo(o.champion);
        }
    }

    public static final String[] champions = Arrays.stream(Champion.values()).map(Enum::toString).toArray(String[]::new);
    public static final String[] roles = Arrays.stream(Role.values()).map(Enum::toString).toArray(String[]::new);

    public enum Champion {
        AATROX,
        AHRI,
        AKALI,
        AKSHAN,
        ALISTAR,
        AMBESSA,
        AMUMU,
        ANIVIA,
        ANNIE,
        APHELIOS,
        ASHE,
        AURELION_SOL,
        AURORA,
        AZIR,
        BARD,
        BEL_VETH,
        BLITZCRANK,
        BRAND,
        BRAUM,
        BRIAR,
        CAITLYN,
        CAMILLE,
        CASSIOPEIA,
        CHO_GATH,
        CORKI,
        DARIUS,
        DIANA,
        DR_MUNDO,
        DRAVEN,
        EKKO,
        ELISE,
        EVELYNN,
        EZREAL,
        FIDDLESTICKS,
        FIORA,
        FIZZ,
        GALIO,
        GANGPLANK,
        GAREN,
        GNAR,
        GRAGAS,
        GRAVES,
        GWEN,
        HECARIM,
        HEIMERDINGER,
        HWEI,
        ILLAOI,
        IRELIA,
        IVERN,
        JANNA,
        JARVAN_IV,
        JAX,
        JAYCE,
        JHIN,
        JINX,
        KAI_SA,
        KALISTA,
        KARMA,
        KARTHUS,
        KASSADIN,
        KATARINA,
        KAYLE,
        KAYN,
        KENNEN,
        KHA_ZIX,
        KINDERED,
        KLED,
        KOG_MAW,
        K_SANTE,
        LEBLANC,
        LEE_SIN,
        LEONA,
        LILLIA,
        LISSANDRA,
        LUCIAN,
        LULU,
        LUX,
        MALPHITE,
        MALZAHAR,
        MAOKAI,
        MASTER_YI,
        MILIO,
        MISS_FORTUNE,
        MORDEKAISER,
        MORGANA,
        NAAFIRI,
        NAMI,
        NASUS,
        NAUTILUS,
        NEEKO,
        NIDALEE,
        NILAH,
        NOCTURNE,
        NUNU_WILLUMP,
        OLAF,
        ORIANNA,
        ORNN,
        PANTHEON,
        POPPY,
        PYKE,
        QIYANA,
        QUINN,
        RAKAN,
        RAMMUS,
        REK_SAI,
        RELL,
        RENATA_GLASC,
        RENEKTON,
        RENGAR,
        RIVEN,
        RUMBLE,
        RYZE,
        SAMIRA,
        SEJUANI,
        SENNA,
        SERAPHINE,
        SETT,
        SHACO,
        SHEN,
        SHYVANA,
        SINGED,
        SION,
        SIVIR,
        SKARNER,
        SMOLDER,
        SONA,
        SORAKA,
        SWAIN,
        SYLAS,
        SYNDRA,
        TAHM_KENCH,
        TALIYAH,
        TALON,
        TARIC,
        TEEMO,
        THRESH,
        TRISTANA,
        TRUNDLE,
        TRYNDAMERE,
        TWISTED_FATE,
        TWITCH,
        UDYR,
        URGOT,
        VARUS,
        VAYNE,
        VEIGAR,
        VEL_KOZ,
        VEX,
        VI,
        VIEGO,
        VIKTOR,
        VLADIMIR,
        VOLIBEAR,
        WARWICK,
        WUKONG,
        XAYAH,
        XERATH,
        XIN_ZHAO,
        YASUO,
        YONE,
        YORICK,
        YUUMI,
        ZAC,
        ZED,
        ZERI,
        ZIGGS,
        ZILEAN,
        ZOE,
        ZYRA,
        ZAAHEN,
        MEL
    }

    public enum Role {
        TOP, JUNGLE, MID, BOT, SUPPORT
    }
}
