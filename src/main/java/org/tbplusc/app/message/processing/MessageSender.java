package org.tbplusc.app.message.processing;

import org.tbplusc.app.util.EnvWrapper;

public enum MessageSender {
    discord(EnvWrapper.getValue("DISCORD_PREFIX")),
    telegram("/");

    public final String prefix;

    MessageSender(String prefix) {
        this.prefix = prefix;
    }

    public boolean hasPrefix() {
        return this.prefix != null;
    }
}
