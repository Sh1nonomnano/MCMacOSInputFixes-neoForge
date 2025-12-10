package com.hamarb123.macos_input_fixes.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.Window;

@Mixin(Minecraft.class)
public interface MinecraftClientAccessor
{
	@Accessor("window")
	public Window getWindow();

	@Accessor("font")
	public Font getTextRenderer();
}
