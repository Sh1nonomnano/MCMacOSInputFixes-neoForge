package com.hamarb123.macos_input_fixes.client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Splitter;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLPaths;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.apache.commons.io.IOUtils;

@OnlyIn(Dist.CLIENT)
public class ModOptions
{
    public static double trackpadSensitivity = 20.0;
    public static boolean reverseHotbarScrolling = false;
    public static boolean reverseScrolling = false;
    public static boolean momentumScrolling = false;
    public static boolean interfaceSmoothScroll = false;
    public static boolean disableCtrlClickFix = false;

    public static OptionInstance<Integer> TRACKPAD_SENSITIVITY;
    public static OptionInstance<Boolean> REVERSE_HOTBAR_SCROLLING;
    public static OptionInstance<Boolean> REVERSE_SCROLLING;
    public static OptionInstance<Boolean> MOMENTUM_SCROLLING;
    public static OptionInstance<Boolean> INTERFACE_SMOOTH_SCROLL;
    public static OptionInstance<Boolean> DISABLE_CTRL_CLICK_FIX;

	public static Object[] getModOptions()
	{
		loadInterface(); //load the elements if they are not loaded yet
		if (Common.IS_SYSTEM_MAC)
		{
			//on macOS show reverse scrolling, reverse hotbar scrolling, trackpad sensitivity, momentum scrolling, interface smooth scroll options, disable ctrl+click fix
			return new Object[] {
                REVERSE_SCROLLING,
                REVERSE_HOTBAR_SCROLLING,
                TRACKPAD_SENSITIVITY,
                MOMENTUM_SCROLLING,
                INTERFACE_SMOOTH_SCROLL,
                DISABLE_CTRL_CLICK_FIX
            };
		}
		else
		{
			//otherwise show reverse scrolling, and reverse hotbar scrolling options only
			return new Object[] {
                REVERSE_SCROLLING,
                REVERSE_HOTBAR_SCROLLING
            };
		}
	}

	private static boolean loadedInterface = false;
	private static void loadInterface()
	{
		if (loadedInterface) return;

        if (Common.IS_SYSTEM_MAC)
        {
            TRACKPAD_SENSITIVITY = new OptionInstance<>(
                "options.macos_input_fixes.trackpad_sensitivity",
                OptionInstance.cachedConstantTooltip(Component.literal("The grouping feature only affects hotbar scrolling.\nThis feature only affects scrolling from the trackpad (and other high precision devices).\nDefault: 20.0\n0.0: Disable custom trackpad scroll processing.\nOther: group scrolls together to make scrolling speed much more reasonable on hotbar, scroll amount is divided by the value chosen here.")),
                (optionText, value) -> Component.literal("Trackpad Sensitivity: " + value),
                new OptionInstance.IntRange(0, 100),
                20,
                (value) -> {
                    setTrackpadSensitivity(value.doubleValue());
                    saveOptions();
                }
            );

            MOMENTUM_SCROLLING = OptionInstance.createBoolean(
                "options.macos_input_fixes.momentum_scrolling",
                OptionInstance.cachedConstantTooltip(Component.literal("Only affects hotbar scrolling.\nA momentum scroll is when macOS keeps scrolling after you release the wheel.\nDefault: OFF\nOFF: ignore 'momentum scroll' events.\nON: process 'momentum scroll' events.")),
                momentumScrolling,
                (value) -> {
                    setMomentumScrolling(value);
                    saveOptions();
                }
            );

            INTERFACE_SMOOTH_SCROLL = OptionInstance.createBoolean(
                "options.macos_input_fixes.smooth_scroll",
                OptionInstance.cachedConstantTooltip(Component.literal("Affects all scrolling from legacy input devices (except for the hotbar).\nmacOS sometimes adjusts how much a single scroll does to make it feel 'smoother', but this can cause scroll amounts to feel random sometimes.\nDefault: OFF\nOFF: Modify smooth scrolling events to all be the same scroll amount.\nON: Keep smooth scrolling events as-is.")),
                interfaceSmoothScroll,
                (value) -> {
                    setInterfaceSmoothScroll(value);
                    saveOptions();
                }
            );

            DISABLE_CTRL_CLICK_FIX = OptionInstance.createBoolean(
                "options.macos_input_fixes.disable_ctrl_click_fix",
                OptionInstance.cachedConstantTooltip(Component.literal("When enabled, disables the fix for the bug which causes Minecraft\nto map Control + Left Click to Right Click.")),
                disableCtrlClickFix,
                (value) -> {
                    disableCtrlClickFix = value;
                    saveOptions();
                }
            );
        }

        REVERSE_HOTBAR_SCROLLING = OptionInstance.createBoolean(
            "options.macos_input_fixes.reverse_hotbar_scrolling",
            OptionInstance.cachedConstantTooltip(Component.literal("Reverses the direction that scrolling goes for the hotbar when enabled.")),
            reverseHotbarScrolling,
            (value) -> {
                reverseHotbarScrolling = value;
                saveOptions();
            }
        );

        REVERSE_SCROLLING = OptionInstance.createBoolean(
            "options.macos_input_fixes.reverse_scrolling",
            OptionInstance.cachedConstantTooltip(Component.literal("Reverses the direction of all scrolling when enabled.")),
            reverseScrolling,
            (value) -> {
                reverseScrolling = value;
                saveOptions();
            }
        );

        loadedInterface = true;
	}

	public static Path optionsFile;

	@SuppressWarnings("resource")
	public static void loadOptions()
	{
		//locate options file
		optionsFile = FMLPaths.CONFIGDIR.get().resolve("macos_input_fixes.txt");

		//check if we need to migrate from the old path
		if (!Files.exists(optionsFile))
		{
			Path oldFile = Paths.get(Minecraft.getInstance().gameDirectory.getAbsolutePath(), "options_macos_input_fixes.txt");
			if (Files.exists(oldFile))
			{
				try
				{
					Files.createDirectories(optionsFile.getParent());
					Files.copy(oldFile, optionsFile);
					Files.deleteIfExists(oldFile);
				}
				catch (IOException e)
				{
					throw new RuntimeException("Failed to migrate old macos input fixes options file from " + oldFile + " to " + optionsFile, e);
				}
			}
		}

		//load options similarly to how minecraft does
		try
		{
			if (!Files.exists(optionsFile))
			{
				return;
			}
			List<String> lines = IOUtils.readLines(Files.newInputStream(optionsFile), StandardCharsets.UTF_8); //split by lines
			CompoundTag compoundTag = new CompoundTag();
			for (String line : lines) //read the lines into a tag
			{
				try
				{
					Iterator<String> iterator = Splitter.on(':').omitEmptyStrings().limit(2).split(line).iterator();
					compoundTag.putString(iterator.next(), iterator.next());
				}
				catch (Exception ex1)
				{
					ex1.printStackTrace(System.err); //failed to parse
				}
			}
			if (compoundTag.contains("trackpadSensitivity")) //read trackpadSensitivity option
			{
				double actualValue = 20.0; //default value
				try
				{
					Double value = Double.parseDouble(compoundTag.getString("trackpadSensitivity"));
					actualValue = value;
				}
				catch (Exception ex1)
				{
					ex1.printStackTrace(System.err); //failed to parse
				}
				setTrackpadSensitivity(actualValue);
			}
			if (compoundTag.contains("reverseHotbarScrolling")) //read reverseHotbarScrolling option
			{
				boolean actualValue = false; //default value
				try
				{
					Boolean value = Boolean.parseBoolean(compoundTag.getString("reverseHotbarScrolling"));
					actualValue = value;
				}
				catch (Exception ex1)
				{
					ex1.printStackTrace(System.err); //failed to parse
				}
				reverseHotbarScrolling = actualValue;
			}
			if (compoundTag.contains("reverseScrolling")) //read reverseScrolling option
			{
				boolean actualValue = false; //default value
				try
				{
					Boolean value = Boolean.parseBoolean(compoundTag.getString("reverseScrolling"));
					actualValue = value;
				}
				catch (Exception ex1)
				{
					ex1.printStackTrace(System.err); //failed to parse
				}
				reverseScrolling = actualValue;
			}
			if (compoundTag.contains("momentumScrolling")) //read momentumScrolling option
			{
				boolean actualValue = false; //default value
				try
				{
					Boolean value = Boolean.parseBoolean(compoundTag.getString("momentumScrolling"));
					actualValue = value;
				}
				catch (Exception ex1)
				{
					ex1.printStackTrace(System.err); //failed to parse
				}
				setMomentumScrolling(actualValue);
			}
			if (compoundTag.contains("interfaceSmoothScroll")) //read interfaceSmoothScroll option
			{
				boolean actualValue = false; //default value
				try
				{
					Boolean value = Boolean.parseBoolean(compoundTag.getString("interfaceSmoothScroll"));
					actualValue = value;
				}
				catch (Exception ex1)
				{
					ex1.printStackTrace(System.err); //failed to parse
				}
				setInterfaceSmoothScroll(actualValue);
			}
			if (compoundTag.contains("disableCtrlClickFix")) //read disableCtrlClickFix option
			{
				boolean actualValue = false; //default value
				try
				{
					Boolean value = Boolean.parseBoolean(compoundTag.getString("disableCtrlClickFix"));
					actualValue = value;
				}
				catch (Exception ex1)
				{
					ex1.printStackTrace(System.err); //failed to parse
				}
				disableCtrlClickFix = actualValue;
			}

			loadedInterface = false;
		}
		catch (Exception ex2)
		{
			ex2.printStackTrace(System.err); //failed to do some sort of IO or something
		}
	}

	public static void saveOptions()
	{
		//write the options to the file in a similar way to minecraft
		try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(optionsFile), StandardCharsets.UTF_8)))
		{
			printWriter.println("trackpadSensitivity:" + trackpadSensitivity);
			printWriter.println("reverseHotbarScrolling:" + reverseHotbarScrolling);
			printWriter.println("reverseScrolling:" + reverseScrolling);
			printWriter.println("momentumScrolling:" + momentumScrolling);
			printWriter.println("interfaceSmoothScroll:" + interfaceSmoothScroll);
			printWriter.println("disableCtrlClickFix:" + disableCtrlClickFix);
		}
		catch (Exception ex2)
		{
			ex2.printStackTrace(System.err); //failed to do some sort of IO or something
		}
	}

	public static void setTrackpadSensitivity(double value)
	{
		trackpadSensitivity = value;
		if (!Common.IS_SYSTEM_MAC) return;

		//set the value in the native library also, ensure the value is clamped here
		if (value < 0) value = 0.0;
		else if (value > 100.0) value = 100.0;
		MacOSInputFixesClientMod.setTrackpadSensitivity(value);
	}

	public static void setMomentumScrolling(boolean value)
	{
		momentumScrolling = value;
		if (!Common.IS_SYSTEM_MAC) return;

		//set the value in the native library also
		MacOSInputFixesClientMod.setMomentumScrolling(value);
	}

	public static void setInterfaceSmoothScroll(boolean value)
	{
		interfaceSmoothScroll = value;
		if (!Common.IS_SYSTEM_MAC) return;

		//set the value in the native library also
		MacOSInputFixesClientMod.setInterfaceSmoothScroll(value);
	}
}
