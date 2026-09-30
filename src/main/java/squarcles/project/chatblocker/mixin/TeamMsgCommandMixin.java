package squarcles.project.chatblocker.mixin;

import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.TeamMsgCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squarcles.project.chatblocker.Chatblocker;
import squarcles.project.chatblocker.ChatblockerConfig;

@Mixin(TeamMsgCommand.class)
public class TeamMsgCommandMixin {

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private static void chatblocker$onSendMessage(CommandSourceStack source, Entity entity,
                                                  PlayerTeam team, List<ServerPlayer> players,
                                                  PlayerChatMessage message, CallbackInfo ci) {

        ChatblockerConfig config = Chatblocker.getConfig();
        if (!config.modEnabled || config.allowTeamMessages) {
            return;
        }

        ServerPlayer player = source.getPlayer();
        if (player == null || Chatblocker.hasBypass(player)) {
            return;
        }

        ci.cancel();

        if (config.notifyPlayer
                && config.teamMessagesBlockedMessage != null && !config.teamMessagesBlockedMessage.isEmpty()) {
            source.sendFailure(Component.literal(config.teamMessagesBlockedMessage));
        }
    }
}