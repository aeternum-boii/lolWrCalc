package net.lolwrcalc.aeternum.agentProfiles;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.lolwrcalc.aeternum.ui.Dialogue;
import net.lolwrcalc.aeternum.ui.UserInterface;
import net.lolwrcalc.aeternum.util.ErrorCodes;
import net.lolwrcalc.aeternum.util.StringMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProfileManager {
    public class ProfileManagerDialogue implements Dialogue {
        @Override
        public Dialogue run(@NotNull PrintStream ps, @NotNull InputStream is, @NotNull UserInterface ui, Object... args) {
            if(ui.getProfileManagerDialogue() == null) ui.setProfileManagerDialogue(this);

            ps.println("Available profiles:");
            profiles.forEach((k, v) -> ps.println("\t" + k));
            ps.println();

            ps.print("""
                    Options:
                    1. Load Profile
                    2. Create Profile
                    3. Delete Profile
                    4. Reload Profiles
                    5. Exit
                    
                    Enter your choice [1..3]:\s""");

            boolean valid = false;
            int choice = -1;
            while (!valid) {
                try {
                    Scanner sc = new Scanner(is);
                    choice = sc.nextInt();
                    valid = choice >= 1 && choice <= 5;
                } catch (Exception e) {
                    if(!(e instanceof NumberFormatException)) System.exit(ErrorCodes.INPUT_PARSING_ERROR);
                }
                if(!valid) ps.println("Invalid input!");
            }

            switch (choice) {
                case 1 -> {
                    try {
                        AgentProfile profile = loadProfile(getIdentifier(ps, is));
                        ui.run(profile.new AgentProfileDialogue());
                    } catch (IOException e) {
                        ps.println("Error loading profile!");
                        run(ps, is, ui, args);
                    }
                }
                case 2 -> {
                    AgentProfile profile = new AgentProfile(getIdentifier(ps, is));
                    ui.run(profile.new AgentProfileDialogue());
                }
                case 3 -> {
                    try {
                        AgentProfile profile = loadProfile(getIdentifier(ps, is));
                        Files.delete(path.resolve(profile.getIdentifier()));
                    } catch (IOException e) {
                        ps.println("Error loading profile!");
                    } finally {
                        run(ps, is, ui, args);
                    }
                }
                case 4 -> {
                    loadProfiles();
                    run(ps, is, ui, args);
                }
                case 5 -> System.exit(0);
            }

            return this;
        }

        private String getIdentifier(@NotNull PrintStream ps, InputStream is) {
            Scanner sc = new Scanner(is);
            ps.print("Profile name: ");
            return sc.nextLine();
        }
    }



    private final static Path path = Path.of("profiles");
    public static final ProfileManager instance = new ProfileManager();

    @NotNull
    private Map<String, FileProfile> profiles;

    public ProfileManager() {
        loadProfiles();
    }

    public void loadProfiles() {

        if(!path.toFile().exists()) if(!path.toFile().mkdirs()) System.exit(ErrorCodes.PROFILE_IO_ERROR);
        try (Stream<Path> stream = Files.list(path)) {
            profiles = stream.collect(Collectors.toMap(
                    Path::toString, (p) -> new FileProfile(p.toFile())
            ));
        } catch (IOException e) {
            System.exit(ErrorCodes.PROFILE_IO_ERROR);
            throw new RuntimeException(e); // unreachable, but static analysis does not know that
        }
    }

    public Optional<AgentProfile> getProfile(String identifier) throws IOException {
        FileProfile fp = profiles.get(identifier);
        if(fp == null) return Optional.empty();
        return Optional.of(fp.getProfile());
    }

    private @NotNull AgentProfile loadProfile(String identifier) throws IOException {
        AgentProfile profile = new AgentProfile(identifier);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<String>> map = mapper.readValue(path.resolve(identifier).toFile(), new TypeReference<Map<String, List<String>>>() {});

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


        File outputFile = path.resolve(profile.getIdentifier()).toFile();
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
