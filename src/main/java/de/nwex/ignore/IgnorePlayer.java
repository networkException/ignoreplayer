package de.nwex.ignore;

import com.mojang.brigadier.CommandDispatcher;
import de.nwex.ignore.command.ClientCommandManager;
import de.nwex.ignore.command.ClientPlayerOrStringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class IgnorePlayer implements ClientModInitializer
{
    public static File configDir;

    public static List<String> ignored;

    public static Formatting BASE = Formatting.GRAY;
    public static Formatting ACCENT = Formatting.BLUE;
    public static Formatting HIGHLIGHT = Formatting.WHITE;
    public static Formatting DARK = Formatting.DARK_GRAY;
    public static Formatting WARN = Formatting.YELLOW;
    public static Formatting ERROR = Formatting.RED;

    @Override
    public void onInitializeClient()
    {
        configDir = new File(FabricLoader.getInstance().getConfigDirectory(), "ignoreplayer");
        //noinspection ResultOfMethodCallIgnored
        configDir.mkdirs();

        ignored = new ArrayList<>();

        ConfigManager.get();
    }

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        ClientCommandManager.addClientSideCommand("ignore");

        dispatcher.register(literal("ignore")
            .then(argument("player", ClientPlayerOrStringArgumentType.argument())
                .executes((context) ->
                {
                    String player = ClientPlayerOrStringArgumentType.getArgument(context, "player");

                    if(ignored.contains(player))
                    {
                        ignored.remove(player);
                        ConfigManager.set();

                        Chat.print(new LiteralText("Log"), new LiteralText("")
                            .append(new LiteralText("Unignored player ").formatted(BASE))
                            .append(ClientPlayerOrStringArgumentType.getArgument(context, "player")).formatted(HIGHLIGHT)
                                .setStyle(new Style()
                                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to copy")))
                                    .setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, ClientPlayerOrStringArgumentType.getArgument(context, "player")))));
                    }
                    else
                    {
                        ignored.add(player);
                        ConfigManager.set();

                        Chat.print(new LiteralText("Log"), new LiteralText("")
                            .append(new LiteralText("Ignored player ").formatted(BASE))
                            .append(ClientPlayerOrStringArgumentType.getArgument(context, "player")).formatted(HIGHLIGHT)
                            .setStyle(new Style()
                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to copy")))
                                .setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, ClientPlayerOrStringArgumentType.getArgument(context, "player")))));
                    }

                    return -1;
                }))
            .executes((context) ->
            {
                Chat.print("Currently ignored players: ");

                ignored.forEach((player) ->
                    Chat.print(new LiteralText("Log"), new LiteralText("")
                        .append(player).formatted(HIGHLIGHT)
                        .setStyle(new Style()
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to copy")))
                            .setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, player)))));

                return 1;
            }));
    }
}
