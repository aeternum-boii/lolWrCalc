package net.aeternum.lolwrcalc.agentProfiles;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.aeternum.lolwrcalc.util.ErrorCodes;
import net.aeternum.lolwrcalc.util.StringMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProfileManager {

    public String getStringRep() {
        StringBuilder sb = new StringBuilder();
        sb.append("Available profiles:\n");
        profiles.forEach((k, v) -> sb.append("\t").append(k).append(": ").append(v).append("\n"));
        return sb.toString();
    }

    public final static Path PATH = Path.of("profiles");
    public static final ProfileManager instance = new ProfileManager();

    @NotNull
    private Map<String, FileProfile> profiles;

    public ProfileManager() {
        loadProfiles();
    }

    public void loadProfiles() {

        if(!PATH.toFile().exists()) if(!PATH.toFile().mkdirs()) System.exit(ErrorCodes.PROFILE_IO_ERROR);
        try (Stream<Path> stream = Files.list(PATH)) {
            profiles = stream.collect(Collectors.toMap(
                    Path::toString, (p) -> new FileProfile(p.toFile())
            ));
        } catch (IOException e) {
            System.exit(ErrorCodes.PROFILE_IO_ERROR);
            throw new RuntimeException(e); // unreachable, but static analysis does not know that
        }
    }

    public Optional<AgentProfile> getProfile(@NonNull String identifier) throws IOException {
        if(!identifier.contains(PATH.toString())) identifier = PATH.resolve(identifier).toString();
        FileProfile fp = profiles.get(identifier);
        if(fp == null) return Optional.empty();
        return Optional.of(fp.getProfile());
    }

    private @NotNull AgentProfile loadProfile(String identifier) throws IOException {
        AgentProfile profile = new AgentProfile(identifier);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<String>> map = mapper.readValue(PATH.resolve(identifier).toFile(), new TypeReference<Map<String, List<String>>>() {});

        map.forEach((champion, v) -> {
            v.forEach( (role) -> {
                Optional<String> closestChampion = StringMatcher.closestIfAbsent(AgentProfile.champions, champion);
                Optional<String> closestRole = StringMatcher.closestIfAbsent(AgentProfile.roles, role);

                profile.add(new AgentProfile.Choice(
                        AgentProfile.Champion.valueOf(closestChampion.orElse(champion)),
                        AgentProfile.Role.valueOf(closestRole.orElse(role))
                ));
            });
        });

        return profile;
    }

    public void saveProfile(@NotNull AgentProfile profile) throws IOException {
        List<AgentProfile.Choice> choices = profile.getChoices(); // unmodifiable list
        Map<String, List<String>> map = choices.stream()
                .collect(Collectors.groupingBy(
                        c -> c.champion().name(),
                        Collectors.mapping(c -> c.role().name(), Collectors.toList())
                ));

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);


        File outputFile = PATH.resolve(profile.getIdentifier()).toFile();
        mapper.writeValue(outputFile, map);
    }

    public int getProfileCount() {
        return profiles.size();
    }

    private class FileProfile {
        @NotNull
        private final File file;

        @Nullable
        private AgentProfile profile = null;

        public FileProfile(@NotNull File file) {
            this.file = file;
        }

        public @NotNull AgentProfile getProfile() throws IOException {
            if(profile == null) profile = loadProfile(file.getName());
            return profile;
        }
    }
}
