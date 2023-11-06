package com.github.nerjalnosk.advancement_cascade;

import com.google.gson.*;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.PlayerPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

public class PlayerAdvancementCriterion extends AbstractCriterion<PlayerAdvancementCriterion.Conditions> {
    public final Identifier id;
    private static final String TK = "target";
    private static final String PK = "predicate";
    private static final String IDK = "id";
    private static final String RK = "required";
    private static final String MK = "min";
    private static final Gson GSON = new Gson();
    private static final BiFunction<String, String, UnsupportedOperationException> EX = (s1, s2) -> new UnsupportedOperationException("Couldn't parse target "+s1+": "+s2);

    public PlayerAdvancementCriterion(Identifier id) {
        this.id = id;
    }

    @Contract("_ -> new")
    private static @NotNull AdvancementTarget parsePrimitiveTarget(@NotNull JsonElement e) {
        Identifier c = Identifier.tryParse(e.getAsString());
        if (c == null) {
            throw EX.apply(e.getAsString(), "is not a valid ID");
        }
        return new AdvancementTarget(c);
    }

    public static @NotNull AdvancementTarget parseObjectTarget(@NotNull JsonObject object) {
        if (!object.has(IDK)) throw EX.apply(GSON.toJson(object), "does not have ID key");
        if (!object.has(RK)) return parsePrimitiveTarget(object.get(IDK));
        Identifier c = Identifier.tryParse(object.get(IDK).getAsString());
        boolean req = Boolean.getBoolean(object.get(RK).getAsString());
        return new AdvancementTarget(c, req);
    }

    private static AdvancementTarget @NotNull [] parseTargets(@NotNull JsonElement targets) {
        if (targets.isJsonPrimitive()) {
            return new AdvancementTarget[]{parsePrimitiveTarget(targets)};
        }
        if (targets.isJsonObject()) {
            return new AdvancementTarget[]{parseObjectTarget(targets.getAsJsonObject())};
        }
        JsonArray array = targets.getAsJsonArray();
        AtomicInteger i = new AtomicInteger();
        AdvancementTarget[] out = new AdvancementTarget[array.size()];
        array.forEach(e -> out[i.getAndIncrement()] = e.isJsonObject() ? parseObjectTarget(e.getAsJsonObject()) : parsePrimitiveTarget(e));
        return out;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        TriggerType type;
        if (obj.has("type")) type = TriggerType.tryName(obj.get("type").getAsString()).orElse(TriggerType.ONLY);
        else type = TriggerType.ONLY;
        AdvancementTarget[] targets;
        AtomicInteger i = new AtomicInteger();
        if (obj.has(TK)) {
            targets = parseTargets(obj.get(TK));
        } else {
            targets = new AdvancementTarget[0];
        }
        PlayerPredicate predicate;
        if (obj.has(PK)) {
            try {
                predicate = PlayerPredicate.fromJson(obj.getAsJsonObject(PK));
            } catch (Exception e) {
                predicate = PlayerPredicate.Builder.create().build();
            }
        } else predicate = PlayerPredicate.Builder.create().build();
        int min = 1;
        if (obj.has(MK) && obj.get(MK).isJsonPrimitive() && obj.get(MK).getAsJsonPrimitive().isNumber()) {
            min = obj.get(MK).getAsJsonPrimitive().getAsInt();
        }
        return new Conditions(id, playerPredicate, type, Arrays.copyOfRange(targets, 0, i.get()), predicate, min);
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, c -> c.test(player));
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final TriggerType type;
        private final AdvancementTarget[] targets;
        private final PlayerPredicate predicate;
        private final int min;

        public Conditions(Identifier id, EntityPredicate.Extended player, TriggerType type, AdvancementTarget[] targets, PlayerPredicate predicate, int min) {
            super(id, player);
            this.type = type;
            this.targets = targets;
            this.predicate = predicate;
            this.min = min;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject object = super.toJson(predicateSerializer);
            object.add("type", new JsonPrimitive(type.getName()));
            JsonArray arr = new JsonArray();
            Arrays.stream(targets).forEach(target -> arr.add(target.toJson()));
            object.add(TK, arr);
            object.add(PK, this.predicate.toJson());
            if (type == TriggerType.SOME_OF) object.add(MK, new JsonPrimitive(min));
            return object;
        }

        private boolean testOnly(ServerPlayerEntity player) {
            for (AdvancementTarget target : this.targets) {
                Advancement adv = player.server.getAdvancementLoader().get(target.id());
                if (adv == null) {
                    if (target.required()) return false;
                    continue;
                }
                if (!player.getAdvancementTracker().getProgress(adv).isDone()) return false;
            }
            return true;
        }

        private boolean testAnyOf(ServerPlayerEntity player) {
            for (AdvancementTarget target : this.targets) {
                Advancement adv = player.server.getAdvancementLoader().get(target.id());
                if (adv == null) {
                    if (target.required()) return false;
                    continue;
                }
                if (player.getAdvancementTracker().getProgress(adv).isDone()) return true;
            }
            return false;
        }

        private boolean testSomeOf(ServerPlayerEntity player) {
            AtomicInteger i = new AtomicInteger();
            for (AdvancementTarget target : this.targets) {
                Advancement adv = player.server.getAdvancementLoader().get(target.id());
                if (adv == null) {
                    if (target.required()) return false;
                    continue;
                }
                if (player.getAdvancementTracker().getProgress(adv).isDone() && i.incrementAndGet() >= min) return true;
            }
            return false;
        }

        public boolean test(ServerPlayerEntity player) {
            return switch (type) {
                case ONLY -> testOnly(player);
                case ANY_OF -> testAnyOf(player);
                case SOME_OF -> testSomeOf(player);
                default -> true;
            };
        }
    }
}
