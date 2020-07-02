package de.nwex.ignore.mixin;

import static de.nwex.ignore.util.chat.ChatBuilder.base;
import static de.nwex.ignore.util.chat.ChatBuilder.text;

import de.nwex.ignore.IgnorePlayer;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    public void getPlayerName(PlayerListEntry playerListEntry, CallbackInfoReturnable<Text> ci) {
        Text original = playerListEntry.getDisplayName() != null ? playerListEntry.getDisplayName()
            : Team.modifyText(playerListEntry.getScoreboardTeam(), new LiteralText(playerListEntry.getProfile().getName()));

        MutableText add = text("");

        if (IgnorePlayer.ignored.contains(playerListEntry.getProfile().getName())) {
            add.append(base(" (ignored)")).formatted(Formatting.ITALIC);
        }

        ci.setReturnValue(text("").append(original).append(add));
    }
}
