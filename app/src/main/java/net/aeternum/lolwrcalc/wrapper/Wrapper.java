package net.aeternum.lolwrcalc.wrapper;

import net.aeternum.lolwrcalc.agentProfiles.AgentProfile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.*;

public class Wrapper {
    private final Path scriptPath;
    public final Path OUTPATH;

    {
        try {
            if(Objects.equals(System.lineSeparator(), "/")) OUTPATH = Files.createTempFile("out", ".json", PosixFilePermissions.asFileAttribute(Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE)));
            else OUTPATH = Files.createTempFile("out", ".json");
            Files.writeString(OUTPATH, "[]", StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cleanup() {
        try {
            Files.writeString(OUTPATH, "[]", StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Wrapper() {
        try (InputStream in = Wrapper.class.getResourceAsStream(Path.of("/lolApi.py").toString())) {
            if (in == null) throw new RuntimeException("Resource not found");

            Path tempDir = Files.createTempDirectory("python");
            scriptPath = tempDir.resolve("lolApi.py");

            Files.copy(in, scriptPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final int DEFAULT_N = 10;
    public static final Lanes.WrappableLane DEFAULT_LANE = Lanes.WrappableLane.mid;
    public static final Ranks.WrappableRank DEFAULT_RANK = Ranks.WrappableRank.diamond_plus;

    @SafeVarargs
    private <T extends Argable> List<String> getParams(T ... args) {
        return Arrays.stream(args).map(T::toArg).collect(LinkedList::new, (list, strAr) -> list.addAll(Arrays.stream(strAr).toList()), LinkedList::addAll);
    }

    private interface Argable {
        @Contract(pure = true)
        public @NonNull String @NonNull [] toArg();
    }

    @SafeVarargs
    private <T extends Argable> void core(@NotNull ApiCall c, T ... args) {
        List<String> params = getParams(args);

        try {
            runScript(c, params.toArray(new String[0]));
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getTierlist(TierListParam @NonNull ... args) {
        core(ApiCall.get_tierlist, args);
    }

    public record TierListParam(int n, @NotNull Lanes.WrappableLane lane, @NotNull Ranks.WrappableRank rank) implements Argable {
        @Contract(pure = true)
        public @NonNull String @NonNull [] toArg() {
            int n = n() < 1 ? DEFAULT_N : n();

            return new String[]{Integer.toString(n), lane.name(), rank.name()};
        }
    }

    public void getCounters(CounterParam @NonNull ... args)  {
        core(ApiCall.get_counters, args);
    }

    public record CounterParam(int n, AgentProfile.Champion champion, Ranks.WrappableRank rank) implements Argable {
        @Contract(pure = true)
        public @NonNull String @NonNull [] toArg() {
            int n = n() < 1 ? DEFAULT_N : n();

            return new String[]{Integer.toString(n), champion.name().toLowerCase(), rank.name()};
        }
    }

    public void getChampionData(ChampDataParam @NonNull ... args) {
        core(ApiCall.get_champion_data, args);
    }

    public record ChampDataParam(AgentProfile.Champion champion, Lanes.WrappableLane lane ,Ranks.WrappableRank rank) implements Argable {
        @Contract(pure = true)
        public @NonNull String @NonNull [] toArg() {
            return new String[]{champion.name().toLowerCase(), lane.name(), rank.name()};
        }
    }

    public void matchup(MatchupParam @NonNull ... args) {
        core(ApiCall.matchup, args);
    }

    public record MatchupParam(AgentProfile.Champion champion1, AgentProfile.Champion champion2, Lanes.WrappableLane lane, Ranks.WrappableRank rank) implements Argable {
        @Contract(pure = true)
        public @NonNull String @NonNull [] toArg() {
            return new String[]{champion1.name().toLowerCase(), champion2.name().toLowerCase(), lane.name(), rank.name()};
        }
    }

    public void patchNotes(PatchNotesParam @NonNull ... args) {
        core(ApiCall.patch_notes, args);
    }

    public record PatchNotesParam(Ranks.WrappableRank rank) implements Argable {
        @Contract(pure = true)
        public @NonNull String @NonNull [] toArg() {
            return new String[]{rank.name()};
        }
    }

    private void runScript(@NonNull ApiCall c, String @NonNull ... params) throws IOException, InterruptedException {
        LinkedList<String> command = new LinkedList<>(Arrays.asList(
                Path.of(System.getProperty("user.home"), "lolalytics-venv", "bin", "python").toString(),
                scriptPath.toAbsolutePath().toString(),
                "-f", OUTPATH.toAbsolutePath().toString(),
                "-c", c.toString()
        ));
        for(String s : params) command.addLast(s);

        Process process = new ProcessBuilder(command).inheritIO().start();

        int exitCode = process.waitFor();
        System.out.println("[WRAPPER]: Process exited with code: " + exitCode);
    }

    private enum ApiCall {
        get_tierlist,
        get_counters,
        get_champion_data,
        matchup,
        patch_notes
    }
}
