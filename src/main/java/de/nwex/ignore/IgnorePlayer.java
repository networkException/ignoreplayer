package de.nwex.ignore;

import static de.nwex.ignore.util.chat.ChatBuilder.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import de.nwex.ignore.command.ClientCommandManager;
import de.nwex.ignore.command.ClientPlayerOrStringArgumentType;
import de.nwex.ignore.util.chat.Chat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;

public class IgnorePlayer implements ClientModInitializer {

    public static File configDir;

    public static List<String> ignored;

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        ClientCommandManager.addClientSideCommand("ignore");

        dispatcher.register(literal("ignore")
            .then(argument("player", ClientPlayerOrStringArgumentType.argument())
                .executes((context) -> {
                    String player = ClientPlayerOrStringArgumentType.getArgument(context, "player");

                    if (ignored.contains(player)) {
                        ignored.remove(player);
                        ConfigManager.set();

                        Chat.print(
                            "Log",
                            base("Unignored player "),
                            copy(
                                hover(
                                    highlight(ClientPlayerOrStringArgumentType.getArgument(context, "player")),
                                    text("Click to copy")
                                ),
                                ClientPlayerOrStringArgumentType.getArgument(context, "player")
                            )
                        );
                    }
                    else {
                        ignored.add(player);
                        ConfigManager.set();

                        Chat.print(
                            "Log",
                            base("Ignored player "),
                            copy(
                                hover(
                                    highlight(ClientPlayerOrStringArgumentType.getArgument(context, "player")),
                                    text("Click to copy")
                                ),
                                ClientPlayerOrStringArgumentType.getArgument(context, "player")
                            )
                        );
                    }

                    return -1;
                }))
            .executes((context) -> {
                Chat.print("Log", base("Currently ignored players:"));

                ignored.forEach((player) ->
                    Chat.print("Log", copy(
                        hover(
                            highlight(player),
                            text("Click to copy")
                        ),
                        player
                    )));

                return 1;
            }));
    }

    @Override
    public void onInitializeClient() {
        configDir = new File(FabricLoader.getInstance().getConfigDirectory(), "ignoreplayer");
        //noinspection ResultOfMethodCallIgnored
        configDir.mkdirs();

        ignored = new ArrayList<>();

        ConfigManager.get();
    }
}
