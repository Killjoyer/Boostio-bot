package org.tbplusc.app.message.processing;

public enum MessageSender {
    discord,
    telegram;

    public boolean isPrefixed() {
        return this == MessageSender.discord;
    }
}
