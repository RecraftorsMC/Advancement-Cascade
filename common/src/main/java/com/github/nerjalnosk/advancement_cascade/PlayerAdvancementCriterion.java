package com.github.nerjalnosk.advancement_cascade;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerAdvancementCriterion extends AbstractCriterion<PlayerAdvancementCriterion.Conditions> {
    public final Identifier id;
    private static final String TK = "target";
    private static final String PK = "predicate";

    public PlayerAdvancementCriterion(Identifier id) {
        this.id = id;
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        TriggerType type;
        if (obj.has("type")) type = TriggerType.tryName(obj.get("type").getAsString()).orElse(TriggerType.ONLY);
        else type = TriggerType.ONLY;
        Identifier[] ids;
        AtomicInteger i = new AtomicInteger();
        if (obj.has(TK) && obj.get(TK).isJsonArray()) {
            ids = new Identifier[obj.getAsJsonArray(TK).size()];
            obj.getAsJsonArray(TK).forEach(e -> {
                Identifier c = Identifier.tryParse(e.getAsString());
                if (c != null) {
                    ids[i.getAndIncrement()] = c;
                }
            });
        } else ids = new Identifier[0];
        PlayerPredicate predicate;
        if (obj.has(PK)) {
            try {
                predicate = PlayerPredicate.fromJson(obj.getAsJsonObject(PK));
            } catch (Exception e) {
                predicate = PlayerPredicate.Builder.create().build();
            }
        } else predicate = PlayerPredicate.Builder.create().build();
        return new Conditions(id, playerPredicate, type, Arrays.copyOfRange(ids, 0, i.get()), predicate);
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, c -> c.test(player));
    }

    @SuppressWarnings("unused")
    public enum TriggerType {
        ONLY,
        ANY;

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

    public static class Conditions extends AbstractCriterionConditions {
        private final TriggerType type;
        private final Identifier[] targets;
        private final PlayerPredicate predicate;

        public Conditions(Identifier id, LootContextPredicate player, TriggerType type, Identifier[] targets, PlayerPredicate predicate) {
            super(id, player);
            this.type = type;
            this.targets = targets;
            this.predicate = predicate;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject object = super.toJson(predicateSerializer);
            object.add("type", new JsonPrimitive(type.getName()));
            JsonArray arr = new JsonArray();
            Arrays.stream(targets).forEach(target -> arr.add(target.toString()));
            object.add(TK, arr);
            object.add(PK, this.predicate.toJson());
            return object;
        }

        public boolean test(ServerPlayerEntity player) {
            if (this.type == TriggerType.ONLY) {
                for (Identifier id : this.targets) {
                    Advancement adv = player.server.getAdvancementLoader().get(id);
                    if (adv == null) continue;
                    if (!player.getAdvancementTracker().getProgress(adv).isDone()) return false;
                }
            }
            return true;
        }
    }
}
