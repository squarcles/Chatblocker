package squarcles.project.chatblocker.mixin;

import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squarcles.project.chatblocker.Chatblocker;
import squarcles.project.chatblocker.ChatblockerConfig;

@Mixin(MsgCommand.class)
public class MsgCommandMixin {

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private static void chatblocker$onSendMessage(CommandSourceStack source,
                                                  Collection<ServerPlayer> players, PlayerChatMessage message, CallbackInfo ci) {

        ChatblockerConfig config = Chatblocker.getConfig();
        if (!config.modEnabled) {
            return;
        }

        ServerPlayer player = source.getPlayer();
        if (player == null || Chatblocker.hasBypass(player)) {
            return;
        }

        String reason = null;
        if (!config.allowPrivateMessages) {
            reason = config.privateMessagesBlockedMessage;
        } else if (!config.allowPrivateMessagesToMultiplePlayers && players.size() > 1) {
            reason = config.multiplePlayersBlockedMessage;
        } else {
            return;
        }

        ci.cancel();

        if (config.notifyPlayer && reason != null && !reason.isEmpty()) {
            source.sendFailure(Component.literal(reason));
        }
    }
}