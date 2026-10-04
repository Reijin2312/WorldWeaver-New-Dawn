package org.betterx.wover.biome.mixin;

import java.util.Set;
import net.minecraft.core.Holder.Reference;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Reference.class})
public interface HolderReferenceAccessor<T> {
   @Accessor("tags")
   Set<TagKey<T>> wover_getTags();

   @Accessor("tags")
   void wover_setTags(Set<TagKey<T>> var1);
}
