package ihh.neotpa;

import net.minecraft.server.level.ServerPlayer;

public abstract sealed class TPARequest permits TPARequest.Normal, TPARequest.Here {
    private final ServerPlayer sender;
    private final ServerPlayer receiver;

    public TPARequest(ServerPlayer sender, ServerPlayer receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public ServerPlayer getSender() {
        return sender;
    }

    public ServerPlayer getReceiver() {
        return receiver;
    }

    public abstract void execute();

    public static final class Normal extends TPARequest {
        public Normal(ServerPlayer sender, ServerPlayer receiver) {
            super(sender, receiver);
        }

        @Override
        public void execute() {
            getSender().teleportTo(getReceiver().getX(), getReceiver().getY(), getReceiver().getZ());
        }
    }

    public static final class Here extends TPARequest {
        public Here(ServerPlayer sender, ServerPlayer receiver) {
            super(sender, receiver);
        }

        @Override
        public void execute() {
            getReceiver().teleportTo(getSender().getX(), getSender().getY(), getSender().getZ());
        }
    }
}
