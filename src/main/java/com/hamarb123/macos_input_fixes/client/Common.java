package com.hamarb123.macos_input_fixes.client;

import com.hamarb123.macos_input_fixes.client.mixin.MinecraftClientAccessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class Common
{
	public static boolean hasControlDownInjector()
	{
		//disable the injector for this call but put back after
		boolean oldValue = injectHasControlDown();
		setInjectHasControlDown(false);
		boolean returnValue;

		//if not on macOS, use normal implementation
		if (!IS_SYSTEM_MAC)
		{
			returnValue = Screen.hasControlDown();
		}
		else
		{
			//replace hasControlDown() on macOS with hasControlDown() (which tests command) or 'actual control down' for this function only
			returnValue = Screen.hasControlDown() ||
				//ctrl key check
				InputConstants.isKeyDown(((MinecraftClientAccessor)Minecraft.getInstance()).getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL) ||
				InputConstants.isKeyDown(((MinecraftClientAccessor)Minecraft.getInstance()).getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_CONTROL);
		}

		//restore injector and return
		setInjectHasControlDown(oldValue);
		return returnValue;
	}

	//enable and disable the onMouseScroll function
	private static ThreadLocal<Boolean> _allowInputOSX = new ThreadLocal<Boolean>();
	public static boolean allowInputOSX()
	{
		Boolean value = _allowInputOSX.get();
		return value != null && value;
	}
	public static void setAllowedInputOSX(boolean value)
	{
		_allowInputOSX.set(value);
	}

	//enable and disable the onKey function (for some specific key codes)
	private static ThreadLocal<Boolean> _allowInputOSX2 = new ThreadLocal<Boolean>();
	public static boolean allowInputOSX2()
	{
		Boolean value = _allowInputOSX2.get();
		return value != null && value;
	}
	public static void setAllowedInputOSX2(boolean value)
	{
		_allowInputOSX2.set(value);
	}

	//enable and disable the addAll parameter modification mixin
	private static ThreadLocal<Boolean> _modifyAddAllParameter = new ThreadLocal<Boolean>();
	public static boolean modifyAddAllParameter()
	{
		Boolean value = _modifyAddAllParameter.get();
		return value != null && value;
	}
	public static void setModifyAddAllParameter(boolean value)
	{
		_modifyAddAllParameter.set(value);
	}

	//enable and disable the hasControlDown mixin
	private static ThreadLocal<Boolean> _injectHasControlDown = new ThreadLocal<Boolean>();
	public static boolean injectHasControlDown()
	{
		Boolean value = _injectHasControlDown.get();
		return value != null && value;
	}
	public static void setInjectHasControlDown(boolean value)
	{
		_injectHasControlDown.set(value);
	}

	//enable and disable the CyclingButtonWidgetMixin3 builder mixin
	private static ThreadLocal<Boolean> _omitBuilderKeyText = new ThreadLocal<Boolean>();
	public static boolean omitBuilderKeyText()
	{
		Boolean value = _omitBuilderKeyText.get();
		return value != null && value;
	}
	public static void setOmitBuilderKeyText(boolean value)
	{
		_omitBuilderKeyText.set(value);
	}

	//assumes we are on the main/event thread
	public static void runOnRenderThreadHelper(Runnable runnable)
	{
        runnable.run();
	}

	//helper for when java struggles with undefined types
	public static Object asObject(Object o)
	{
		return o;
	}

	public static final boolean IS_SYSTEM_MAC = Util.getPlatform() == Util.OS.OSX;
}
