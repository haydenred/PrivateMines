package me.hayden.privatemines.storage;

import me.hayden.privatemines.Main;
import me.hayden.privatemines.utils.ChatUtil;

public enum Messages {

    PERMISSION_DENIED("permission-denied"),
    TELEPORT_SUCCESS("teleport-success"),
    CREATE_SUCCESS("create-success"),
    DELETE_SUCCESS("delete-success"),
    ALREADY_HAS_MINE("already-has-mine"),
    DOESNT_OWN_MINE("doesnt-own-mine");




    private String path;
    Messages(String path) {
        this.path = path;
    }
    @Override
    public String toString() {
        return ChatUtil.color(Main.plugin.getMessagesConfig().getString(this.path));
    }
}
