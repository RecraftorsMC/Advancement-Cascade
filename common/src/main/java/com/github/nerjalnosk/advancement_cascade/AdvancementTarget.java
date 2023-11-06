package com.github.nerjalnosk.advancement_cascade;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.Identifier;

public record AdvancementTarget(Identifier id, boolean required) {
    public AdvancementTarget(Identifier id) {
        this(id, false);
    }

    public JsonElement toJson() {
        JsonObject o = new JsonObject();
        o.add("id", new JsonPrimitive(this.id.toString()));
        o.add("required", new JsonPrimitive(this.required));
        return o;
    }
}
