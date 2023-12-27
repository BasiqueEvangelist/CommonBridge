package me.basiqueevangelist.commonbridge.util;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AdventureUtils {
    public static Component toAdventure(Text text) {
        return GsonComponentSerializer.gson().deserializeFromTree(Text.Serializer.toJsonTree(text));
    }

    public static Key toAdventure(Identifier id) {
        //noinspection PatternValidation
        return Key.key(id.getNamespace(), id.getPath());
    }
}
