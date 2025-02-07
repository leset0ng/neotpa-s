package ihh.neotpa;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class TPACommands {
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("tpa")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(TPACommands::tpa)));
        dispatcher.register(Commands.literal("tpahere")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(TPACommands::tpaHere)));
        dispatcher.register(Commands.literal("tpaccept")
                .executes(TPACommands::tpAcceptLast)
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(TPACommands::tpAccept)));
        dispatcher.register(Commands.literal("tpdeny")
                .executes(TPACommands::tpDenyLast)
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(TPACommands::tpDeny)));
        dispatcher.register(Commands.literal("tpcancel")
                .executes(TPACommands::tpCancelLast)
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(TPACommands::tpCancel)));
    }

    private static int tpa(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        TPAManager.tpa(thisPlayer(context), thatPlayer(context));
        return 1;
    }

    private static int tpaHere(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        TPAManager.tpaHere(thisPlayer(context), thatPlayer(context));
        return 1;
    }

    private static int tpAcceptLast(CommandContext<CommandSourceStack> context) {
        TPAManager.tpAccept(thisPlayer(context), null);
        return 1;
    }

    private static int tpAccept(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        TPAManager.tpAccept(thisPlayer(context), thatPlayer(context));
        return 1;
    }

    private static int tpDenyLast(CommandContext<CommandSourceStack> context) {
        TPAManager.tpDeny(thisPlayer(context), null);
        return 1;
    }

    private static int tpDeny(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        TPAManager.tpDeny(thisPlayer(context), thatPlayer(context));
        return 1;
    }

    private static int tpCancelLast(CommandContext<CommandSourceStack> context) {
        TPAManager.tpCancel(thisPlayer(context), null);
        return 1;
    }

    private static int tpCancel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        TPAManager.tpCancel(thisPlayer(context), thatPlayer(context));
        return 1;
    }

    private static ServerPlayer thisPlayer(CommandContext<CommandSourceStack> context) {
        return context.getSource().getPlayer();
    }

    private static ServerPlayer thatPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return EntityArgument.getPlayer(context, "player");
    }
}
