package com.eternalcode.core.chat.notification;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class Notification {

    protected final Set<NotificationType> types = new HashSet<>();
    protected final String message;

    protected Notification(String message, NotificationType... types) {
        this.message = message;
        this.types.addAll(Arrays.asList(types));
    }

    protected Notification(String message, Set<NotificationType> types) {
        this.message = message;
        this.types.addAll(types);
    }

    public Notification edit(Function<String, String> edit) {
        return new Notification(edit.apply(this.message), types);
    }

    public Set<NotificationType> getTypes() {
        return Collections.unmodifiableSet(types);
    }

    public String getMessage() {
        return message;
    }

    public static Notification of(String message, NotificationType... types) {
        return new Notification(message, types);
    }

}