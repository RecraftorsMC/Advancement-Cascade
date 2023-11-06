package com.github.nerjalnosk.advancement_cascade;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

@SuppressWarnings("unused")
public enum TriggerType {
    ONLY,
    ANY,
    ANY_OF,
    SOME_OF;

    public @NotNull String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public static @NotNull Optional<TriggerType> tryName(@NotNull String s) {
        try {
            return Optional.of(TriggerType.valueOf(s.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
