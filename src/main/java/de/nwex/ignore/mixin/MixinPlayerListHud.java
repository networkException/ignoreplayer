package de.nwex.ignore.mixin;

import de.nwex.ignore.IgnorePlayer;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud
{
    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    public void getPlayerName(PlayerListEntry playerListEntry, CallbackInfoReturnable<Text> ci)
    {
        Text original = playerListEntry.getDisplayName() != null ? playerListEntry.getDisplayName() : Team.modifyText(playerListEntry.getScoreboardTeam(), new LiteralText(playerListEntry.getProfile().getName()));

        Text add = new LiteralText("");

        if(IgnorePlayer.ignored.contains(playerListEntry.getProfile().getName()))
        {
            add.append(" (ignored)").formatted(Formatting.GRAY).formatted(Formatting.ITALIC);
        }

        ci.setReturnValue(new LiteralText("").append(original).append(add));
    }
}
