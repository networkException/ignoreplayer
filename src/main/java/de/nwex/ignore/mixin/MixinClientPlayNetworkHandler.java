package de.nwex.ignore.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import de.nwex.ignore.IgnorePlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.client.network.packet.CommandTreeS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.stream.Collectors;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler
{
    @Shadow
    private CommandDispatcher<CommandSource> commandDispatcher;

    @Shadow
    private MinecraftClient client;

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(ChatMessageS2CPacket chatMessageS2CPacket, CallbackInfo ci)
    {
        String formatted = chatMessageS2CPacket.getMessage().asFormattedString();
        String escaped = formatted.replace("[<" + Arrays.stream(Formatting.values()).map(Formatting::toString).collect(Collectors.joining(", ")) + ">]", "");


    }

    @SuppressWarnings("unchecked")
    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(MinecraftClient mc, Screen screen, ClientConnection connection, GameProfile profile, CallbackInfo ci)
    {
        IgnorePlayer.registerCommands((CommandDispatcher<ServerCommandSource>) (Object) commandDispatcher);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "onCommandTree", at = @At("TAIL"))
    public void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci)
    {
        IgnorePlayer.registerCommands((CommandDispatcher<ServerCommandSource>) (Object) commandDispatcher);
    }
}
