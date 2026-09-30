package squarcles.project.chatblocker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.level.ServerPlayer;


// IF YOU READ THIS <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 <3 TO YOU :)
// FEEL FREE TO USE THE CODE HOWEVER YOU WANT

public class Chatblocker implements ModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger("chatblocker");
    private static ChatblockerConfig config;

    @Override

    public void onInitialize() {

        config = ChatblockerConfig.load();


        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, boundChatType) -> {

            if (!config.modEnabled) {
                return true;
            }

            boolean isPlainChat = boundChatType.chatType().is(ChatType.CHAT);
            boolean isOp = sender.permissions().hasPermission(Permissions.COMMANDS_MODERATOR);
            boolean blocked = isPlainChat && !isOp;

            if (blocked && config.notifyPlayer
                    && config.blockedMessage != null && !config.blockedMessage.isEmpty()) {
                sender.sendSystemMessage(Component.literal(config.blockedMessage));
            }

            return !blocked;
        });


        ServerMessageEvents.ALLOW_COMMAND_MESSAGE.register((message, source, boundChatType) -> {

            if (!config.modEnabled || config.allowPrivateMessagesToMultiplePlayers) {
                return true;
            }

            if (!boundChatType.chatType().is(ChatType.EMOTE_COMMAND)) {
                return true;
            }

            ServerPlayer player = source.getPlayer();
            if (player == null || hasBypass(player)) {
                return true;
            }

            if (config.notifyPlayer
                    && config.multiplePlayersBlockedMessage != null
                    && !config.multiplePlayersBlockedMessage.isEmpty()) {
                source.sendFailure(Component.literal(config.multiplePlayersBlockedMessage));
            }
            return false;
        });
    }

    public static ChatblockerConfig getConfig() {
        return config;
    }

    public static boolean hasBypass(ServerPlayer player) {
        return player.permissions().hasPermission(Permissions.COMMANDS_MODERATOR);
    }
}