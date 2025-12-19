package net.lolwrcalc.aeternum.agentProfiles;

import net.lolwrcalc.aeternum.ui.Dialogue;
import net.lolwrcalc.aeternum.ui.UserInterface;
import net.lolwrcalc.aeternum.util.ErrorCodes;
import net.lolwrcalc.aeternum.util.StringMatcher;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class AgentProfile {
    public String getIdentifier() {
        return name;
    }

    public class AgentProfileDialogue implements Dialogue {

        @Override
        public Dialogue run(@NotNull PrintStream ps, @NotNull InputStream is, @NotNull UserInterface ui, Object... args) {
            ps.println("Profile name: " + name + "\nCurrent Choices: ");
            for(Choice c : choices) ps.println("\t" + c.toCall());
            ps.println();

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
                    if(!(e instanceof NumberFormatException)) System.exit(1);
                }
                if(!valid) ps.println("Invalid input!");
            }

            switch (choice) {
                case 1 -> {
                    try {
                        ProfileManager.instance.saveProfile(AgentProfile.this);
                    } catch (IOException e) {
                        ps.println("Error saving profile - exiting!");
                        System.exit(ErrorCodes.PROFILE_IO_ERROR);
                    }
                    ui.run(ui.getProfileManagerDialogue() != null ? ui.getProfileManagerDialogue() : ProfileManager.instance.new ProfileManagerDialogue());
                }
                case 2 -> {
                    AgentProfile.this.add(parseChoice(ps, is, ui));
                    run(ps, is, ui, args);
                }
                case 3 -> {
                    AgentProfile.this.remove(parseChoice(ps, is, ui));
                    run(ps, is, ui, args);
                }
                case 4 -> {}
                case 5 -> ui.run(ui.getProfileManagerDialogue() != null ? ui.getProfileManagerDialogue() : ProfileManager.instance.new ProfileManagerDialogue());
                case 6 -> System.exit(0);
            }

            return this;
        }

        @Contract("_, _, _ -> new")
        private @NotNull Choice parseChoice(@NotNull PrintStream ps, InputStream is, UserInterface ui) {
            ps.println("Please enter the champion and role (separated by a space):");

            boolean valid = false;
            String[] input = null;
            while (!valid) {
                Scanner sc = new Scanner(is);
                input = sc.nextLine().trim().split(" ");
                if(input.length != 2) {
                    ps.println("Invalid input!");
                } else valid = true;
            }

            String[] out = new String[1];
            new StringMatcher.StringMatcherDialogue().run(ps, is, ui, out, input[0], champions);
            Champion champion = Champion.valueOf(out[0]);
            new StringMatcher.StringMatcherDialogue().run(ps, is, ui, out, input[1], roles);
            Role role = Role.valueOf(out[0]);

            return new Choice(champion, role);
        }
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
