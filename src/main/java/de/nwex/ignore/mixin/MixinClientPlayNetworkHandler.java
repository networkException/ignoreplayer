package de.nwex.ignore.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import de.nwex.ignore.Chat;
import de.nwex.ignore.IgnorePlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.client.network.packet.CommandTreeS2CPacket;
import net.minecraft.client.network.packet.GameJoinS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler
{
    @Shadow
    private CommandDispatcher<CommandSource> commandDispatcher;

    @Shadow
    private MinecraftClient client;

    private Boolean once = true;

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(ChatMessageS2CPacket chatMessageS2CPacket, CallbackInfo ci)
    {
        String formatted = chatMessageS2CPacket.getMessage().asFormattedString();
        String escaped = formatted.replace("[<" + Arrays.stream(Formatting.values()).map(Formatting::toString).collect(Collectors.joining(", ")) + ">]", "");

        Pattern coolPattern = Pattern.compile("^§.§.§.([A-Z]+)§r §.([A-Za-z_0-9]{1,16})§.: §r(.+)$");
        Matcher coolMatcher = coolPattern.matcher(chatMessageS2CPacket.getMessage().asFormattedString().replace("\n", "").trim());

        Pattern simplyPattern = Pattern.compile("^<([A-Za-z_0-9]{1,16})> (.+)$");
        Matcher simplyMatcher = simplyPattern.matcher(chatMessageS2CPacket.getMessage().asFormattedString().replace("\n", "").trim());

        if((coolMatcher.matches() && IgnorePlayer.ignored.contains(coolMatcher.group(2))) || (simplyMatcher.matches() && IgnorePlayer.ignored.contains(simplyMatcher.group(1))))
        {
            System.out.println("Ignored: " + escaped);

            ci.cancel();
            return;
        }

        this.client.inGameHud.addChatMessage(chatMessageS2CPacket.getLocation(), chatMessageS2CPacket.getMessage());
        ci.cancel();
    }

    @Inject(method = "onGameJoin", at = @At("HEAD"))
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        if(once)
        {
            once = false;

            new Thread(() ->
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }

                Chat.print(new LiteralText("Loaded"), new LiteralText("")
                    .append(new LiteralText("Ignoring ").formatted(IgnorePlayer.BASE))
                    .append(new LiteralText(String.valueOf(IgnorePlayer.ignored.size())).formatted(IgnorePlayer.HIGHLIGHT))
                    .append(new LiteralText(" player(s)").formatted(IgnorePlayer.BASE))
                );
            }).start();
        }
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
