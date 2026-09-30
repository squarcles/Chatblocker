package squarcles.project.chatblocker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ChatblockerConfig {

    public boolean modEnabled = true;
    public boolean allowPrivateMessages = true;
    public boolean allowPrivateMessagesToMultiplePlayers = false;
    public boolean allowTeamMessages = false;

    public String blockedMessage = "§cChat is disabled on this server.";
    public String privateMessagesBlockedMessage = "§cPrivate messages are disabled on this server.";
    public String multiplePlayersBlockedMessage = "§cYou can not send a private message to several players at once.";
    public String teamMessagesBlockedMessage = "§cTeam messages are disabled on this server.";

    public boolean notifyPlayer = true;

    private static final Logger LOGGER = LoggerFactory.getLogger("chatblocker");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static ChatblockerConfig load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("chatblocker.json");

        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                ChatblockerConfig config = GSON.fromJson(reader, ChatblockerConfig.class);
                if (config != null) {
                    return config;
                }
            } catch (IOException | JsonParseException e) {
                LOGGER.error("Can not read chatblocker.json, default value will be used.", e);
            }
            return new ChatblockerConfig();
        }

        ChatblockerConfig config = new ChatblockerConfig();
        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            LOGGER.error("Can not create chatblocker.json", e);
        }
        return config;
    }
}