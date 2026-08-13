package ihh.neotpa;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

public class TPAManager {
    public static final String TPA_SENT_0 = "commands.neotpa.tpa.sent.0";
    public static final String TPA_SENT_1 = "commands.neotpa.tpa.sent.1";
    public static final String TPA_RECEIVED_0 = "commands.neotpa.tpa.received.0";
    public static final String TPA_RECEIVED_1 = "commands.neotpa.tpa.received.1";
    public static final String TPA_ALREADY_SENT = "commands.neotpa.tpa.already_sent";
    public static final String TPAHERE_SENT_0 = "commands.neotpa.tpahere.sent.0";
    public static final String TPAHERE_SENT_1 = "commands.neotpa.tpahere.sent.1";
    public static final String TPAHERE_RECEIVED_0 = "commands.neotpa.tpahere.received.0";
    public static final String TPAHERE_RECEIVED_1 = "commands.neotpa.tpahere.received.1";
    public static final String TPAHERE_ALREADY_SENT = "commands.neotpa.tpahere.already_sent";
    public static final String TPACCEPT_SUCCESS_FROM = "commands.neotpa.tpaccept.success_from";
    public static final String TPACCEPT_SUCCESS_TO = "commands.neotpa.tpaccept.success_to";
    public static final String TPACCEPT_FAILURE_DIMENSION = "commands.neotpa.tpaccept.failure_dimension";
    public static final String TPACCEPT_FAILURE_NO_TPA = "commands.neotpa.tpaccept.failure_no_tpa";
    public static final String TPACCEPT_FAILURE_NO_TPA_FROM_PLAYER = "commands.neotpa.tpaccept.failure_no_tpa_from_player";
    public static final String TPDENY_SUCCESS_FROM = "commands.neotpa.tpdeny.success_from";
    public static final String TPDENY_SUCCESS_TO = "commands.neotpa.tpdeny.success_to";
    public static final String TPDENY_FAILURE_NO_TPA = "commands.neotpa.tpdeny.failure_no_tpa";
    public static final String TPDENY_FAILURE_NO_TPA_FROM_PLAYER = "commands.neotpa.tpdeny.failure_no_tpa_from_player";
    public static final String TPCANCEL_SUCCESS_FROM = "commands.neotpa.tpcancel.success_from";
    public static final String TPCANCEL_SUCCESS_TO = "commands.neotpa.tpcancel.success_to";
    public static final String TPCANCEL_FAILURE_NO_TPA = "commands.neotpa.tpcancel.failure_no_tpa";
    public static final String TPCANCEL_FAILURE_NO_TPA_FROM_PLAYER = "commands.neotpa.tpcancel.failure_no_tpa_from_player";
    private static final LinkedList<TPARequest> REQUESTS = new LinkedList<>();

    public static void tpa(ServerPlayer from, ServerPlayer to) {
        TPARequest.Normal request = new TPARequest.Normal(from, to);
        if (!REQUESTS.contains(request)) {
            REQUESTS.addFirst(request);
            from.sendSystemMessage(Localization.translate(TPA_SENT_0, ChatFormatting.GOLD, nameColored(to)));
            from.sendSystemMessage(Localization.translate(TPA_SENT_1, ChatFormatting.GOLD, literalColored("/tpcancel", ChatFormatting.RED)));
            to.sendSystemMessage(Localization.translate(TPA_RECEIVED_0, ChatFormatting.GOLD, nameColored(from)));
            to.sendSystemMessage(Localization.translate(TPA_RECEIVED_1, ChatFormatting.GOLD, literalColored("/tpaccept", ChatFormatting.GREEN), literalColored("/tpdeny", ChatFormatting.RED)));
        } else {
            from.sendSystemMessage(Localization.translate(TPA_ALREADY_SENT, ChatFormatting.GOLD, nameColored(to)));
        }
    }

    public static void tpaHere(ServerPlayer from, ServerPlayer to) {
        TPARequest.Here request = new TPARequest.Here(from, to);
        if (!REQUESTS.contains(request)) {
            REQUESTS.addFirst(request);
            from.sendSystemMessage(Localization.translate(TPAHERE_SENT_0, ChatFormatting.GOLD, nameColored(to)));
            from.sendSystemMessage(Localization.translate(TPAHERE_SENT_1, ChatFormatting.GOLD, literalColored("/tpcancel", ChatFormatting.RED)));
            to.sendSystemMessage(Localization.translate(TPAHERE_RECEIVED_0, ChatFormatting.GOLD, nameColored(from)));
            to.sendSystemMessage(Localization.translate(TPAHERE_RECEIVED_1, ChatFormatting.GOLD, literalColored("/tpaccept", ChatFormatting.GREEN), literalColored("/tpdeny", ChatFormatting.RED)));
        } else {
            from.sendSystemMessage(Localization.translate(TPAHERE_ALREADY_SENT, ChatFormatting.GOLD, nameColored(to)));
        }
    }

    public static void tpAccept(ServerPlayer to, @Nullable ServerPlayer from) {
        TPARequest request = getRequest(from, to);
        if (request != null) {
            if (request.getSender().level() == request.getReceiver().level()) {
                request.execute();
                REQUESTS.remove(request);
                request.getSender().sendSystemMessage(Localization.translate(TPACCEPT_SUCCESS_FROM, ChatFormatting.GOLD, nameColored(request.getReceiver())));
                request.getReceiver().sendSystemMessage(Localization.translate(TPACCEPT_SUCCESS_TO, ChatFormatting.GOLD, nameColored(request.getSender())));
            } else {
                to.sendSystemMessage(Localization.translate(TPACCEPT_FAILURE_DIMENSION, ChatFormatting.GOLD, nameColored(request.getSender())));
            }
        } else if (from != null) {
            to.sendSystemMessage(Localization.translate(TPACCEPT_FAILURE_NO_TPA_FROM_PLAYER, ChatFormatting.GOLD, nameColored(from)));
        } else {
            to.sendSystemMessage(Localization.translate(TPACCEPT_FAILURE_NO_TPA, ChatFormatting.GOLD));
        }
    }

    public static void tpDeny(ServerPlayer to, @Nullable ServerPlayer from) {
        TPARequest request = getRequest(from, to);
        if (request != null) {
            REQUESTS.remove(request);
            request.getSender().sendSystemMessage(Localization.translate(TPDENY_SUCCESS_FROM, ChatFormatting.GOLD, nameColored(request.getReceiver())));
            request.getReceiver().sendSystemMessage(Localization.translate(TPDENY_SUCCESS_TO, ChatFormatting.GOLD, nameColored(request.getSender())));
        } else if (from != null) {
            to.sendSystemMessage(Localization.translate(TPDENY_FAILURE_NO_TPA_FROM_PLAYER, ChatFormatting.GOLD, nameColored(from)));
        } else {
            to.sendSystemMessage(Localization.translate(TPDENY_FAILURE_NO_TPA, ChatFormatting.GOLD));
        }
    }

    public static void tpCancel(ServerPlayer from, @Nullable ServerPlayer to) {
        TPARequest request = getRequest(from, to);
        if (request != null) {
            REQUESTS.remove(request);
            request.getSender().sendSystemMessage(Localization.translate(TPCANCEL_SUCCESS_FROM, ChatFormatting.GOLD, nameColored(request.getReceiver())));
            request.getReceiver().sendSystemMessage(Localization.translate(TPCANCEL_SUCCESS_TO, ChatFormatting.GOLD, nameColored(request.getSender())));
        } else if (to != null) {
            from.sendSystemMessage(Localization.translate(TPCANCEL_FAILURE_NO_TPA_FROM_PLAYER, ChatFormatting.GOLD, nameColored(to)));
        } else {
            from.sendSystemMessage(Localization.translate(TPCANCEL_FAILURE_NO_TPA, ChatFormatting.GOLD));
        }
    }

    @Nullable
    private static TPARequest getRequest(@Nullable ServerPlayer from, @Nullable ServerPlayer to) {
        for (TPARequest e : REQUESTS) {
            if ((from == null || e.getSender().equals(from)) && (to == null || e.getReceiver().equals(to))) {
                return e;
            }
        }
        return null;
    }

    private static MutableComponent literalColored(String text, ChatFormatting color) {
        return Component.literal(text).withStyle(color);
    }
    
    private static MutableComponent nameColored(Player player) {
        return literalColored(player.getGameProfile().getName(), ChatFormatting.GREEN);
    }
}
