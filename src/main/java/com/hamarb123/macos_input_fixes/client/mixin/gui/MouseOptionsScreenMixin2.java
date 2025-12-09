package com.hamarb123.macos_input_fixes.client.mixin.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import net.minecraft.client.gui.screens.options.MouseSettingsScreen;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.OptionsList;
import com.hamarb123.macos_input_fixes.client.ModOptions;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

@Mixin(MouseSettingsScreen.class)
public class MouseOptionsScreenMixin2
{
	@ModifyArgs(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/OptionsList;addSmall([Lnet/minecraft/client/OptionInstance;)V"))
	private void init(Args args)
	{
		Object[] modOptions = ModOptions.getModOptions();
		if (modOptions == null) return;

        OptionInstance<?>[] currentOptions = args.get(0);

        List<OptionInstance<?>> allOptions = new ArrayList<>(Arrays.asList(currentOptions));
        for (Object o : modOptions) {
            allOptions.add((OptionInstance<?>)o);
        }

        args.set(0, allOptions.toArray(new OptionInstance[0]));
	}
}
