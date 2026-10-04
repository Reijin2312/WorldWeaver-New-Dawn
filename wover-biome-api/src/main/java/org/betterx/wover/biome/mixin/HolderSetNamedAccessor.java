package org.betterx.wover.biome.mixin;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.Named;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Named.class})
public interface HolderSetNamedAccessor<T> {
   @Accessor("contents")
   List<Holder<T>> wover_getContents();

   @Accessor("contents")
   void wover_setContents(List<Holder<T>> var1);
}
