package com.hamarb123.macos_input_fixes.client.mixin;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Options;
import com.mojang.blaze3d.platform.Window;
import org.lwjgl.glfw.GLFWNativeCocoa;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hamarb123.macos_input_fixes.client.Common;
import com.hamarb123.macos_input_fixes.client.MacOSInputFixesClientMod;

@Mixin(Minecraft.class)
public class MinecraftClientMixin
{
	@Shadow
	private Window window;

	@Shadow
	private MouseHandler mouseHandler;

	@Shadow
	private KeyboardHandler keyboardHandler;

	@Shadow
	private Options options;

	private boolean runOnce = false;

	//function that is called immediately after the window is created on both versions
	@Inject(at = @At("HEAD"), method = "onWindowFocusChanged(Z)V", cancellable = true)
	private void onWindowFocusChanged(boolean focused, CallbackInfo info)
	{
		if (Common.IS_SYSTEM_MAC)
		{
			if (!runOnce)
			{
				//register the native callback for scrolling
				long glfwWindow = window.getWindow();
				long cocoaWindow = GLFWNativeCocoa.glfwGetCocoaWindow(glfwWindow);
				MacOSInputFixesClientMod.registerCallbacks(this::scrollCallback, this::keyCallback, cocoaWindow);
				runOnce = true;
			}
		}
	}

	private void scrollCallback(double horizontal, double vertical, double horizontalWithMomentum, double verticalWithMomentum, double horizontalUngrouped, double verticalUngrouped)
	{
		//recieve the native scrolling callback & convert it into a scroll event

		//determine if discrete scroll is enabled
		boolean discreteScroll = options.discreteMouseScroll().get();

		//replace ungrouped values with grouped values if discrete scroll is enabled
		if (discreteScroll)
		{
			horizontalUngrouped = horizontalWithMomentum;
			verticalUngrouped = verticalWithMomentum;
		}

		//use ungrouped values if not scrolling on hotbar
		if (((Minecraft)(Object)this).getOverlay() != null || ((Minecraft)(Object)this).screen != null || ((Minecraft)(Object)this).player == null)
		{
			horizontal = horizontalUngrouped;
			vertical = verticalUngrouped;
		}

		//combine vertical & horizontal here since it's harder to do in the actual method (when scrolling for hotbar)
		else
		{
			vertical += horizontal;
			horizontal = 0;
		}

		//check if we actually have an event still
		if (horizontal == 0 && vertical == 0) return;

		double horizontalCopy = horizontal;
		double verticalCopy = vertical;
		Common.runOnRenderThreadHelper(() ->
		{
			//enable onMouseScroll
			Common.setAllowedInputOSX(true);

			//on 1.14 we need to use the window field, on 1.19 the field still exists
			((MouseInvokerMixin)mouseHandler).callOnMouseScroll(((MinecraftClientAccessor)Minecraft.getInstance()).getWindow().getWindow(), horizontalCopy, verticalCopy);

			//disable onMouseScroll
			Common.setAllowedInputOSX(false);
		});
	}

	private void keyCallback(int key, int scancode, int action, int modifiers)
	{
		Common.runOnRenderThreadHelper(() ->
		{
			//enable onKey
			Common.setAllowedInputOSX2(true);

            keyboardHandler.keyPress(Minecraft.getInstance().getWindow().getWindow(), key, scancode, action, modifiers);

			//disable onKey
			Common.setAllowedInputOSX2(false);
		});
	}
}
