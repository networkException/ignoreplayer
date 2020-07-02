package de.nwex.ignore.mixin;

import static de.nwex.ignore.util.chat.ChatBuilder.base;
import static de.nwex.ignore.util.chat.ChatBuilder.highlight;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import de.nwex.ignore.IgnorePlayer;
import de.nwex.ignore.util.chat.Chat;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    private final Pattern coolPattern = Pattern.compile("§.§.§.([A-Z]+)§r §.([A-Za-z_0-9]{1,16})§.: §r(.+)$");
    private final Pattern simplyPattern = Pattern.compile("<([A-Za-z_0-9]{1,16})> (.+)$");

    @Shadow
    private CommandDispatcher<CommandSource> commandDispatcher;

    @Shadow
    private MinecraftClient client;

    private Boolean once = true;

    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        String formatted = packet.getMessage().getString();
        String escaped = formatted
            .replace("[<" + Arrays.stream(Formatting.values()).map(Formatting::toString).collect(Collectors.joining(", ")) + ">]", "");

        Matcher coolMatcher = coolPattern.matcher(escaped.replace("\n", "").trim());
        Matcher simplyMatcher = simplyPattern.matcher(escaped.replace("\n", "").trim());

        if ((coolMatcher.matches() && IgnorePlayer.ignored.contains(coolMatcher.group(2))) || (simplyMatcher.matches() && IgnorePlayer.ignored
            .contains(simplyMatcher.group(1)))) {
            System.out.println("Ignored: " + escaped);

            ci.cancel();
            return;
        }

        this.client.inGameHud.addChatMessage(
            packet.getLocation(),
            packet.getMessage(),
            packet.getSenderUuid()
        );

        ci.cancel();
    }

    @Inject(method = "onGameJoin", at = @At("HEAD"))
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        if (once) {
            once = false;

            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Chat.print(
                    "Loaded",
                    base("Ignoring "),
                    highlight(String.valueOf(IgnorePlayer.ignored.size())),
                    base(IgnorePlayer.ignored.size() == 1 ? " player" : " players")
                );
            }).start();
        }
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(MinecraftClient mc, Screen screen, ClientConnection connection, GameProfile profile, CallbackInfo ci) {
        IgnorePlayer.registerCommands((CommandDispatcher<ServerCommandSource>) (Object) commandDispatcher);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "onCommandTree", at = @At("TAIL"))
    public void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci) {
        IgnorePlayer.registerCommands((CommandDispatcher<ServerCommandSource>) (Object) commandDispatcher);
    }
}
