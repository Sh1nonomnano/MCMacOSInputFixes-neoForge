package com.hamarb123.macos_input_fixes.client;

import net.neoforged.fml.common.Mod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import java.io.IOException;

@Mod("macos_input_fixes")
public class MacOSInputFixesClientMod
{
	public MacOSInputFixesClientMod()
	{
		if (FMLEnvironment.dist == Dist.CLIENT) {
			ModOptions.loadOptions();
		}
	}

	//these functions are defined in Objective C++
	public static native void registerCallbacks(ScrollCallback scrollCallback, KeyCallback keyCallback, long window);
	public static native void setTrackpadSensitivity(double sensitivity);
	public static native void setMomentumScrolling(boolean option);
	public static native void setInterfaceSmoothScroll(boolean option);

	static
	{
		if (Common.IS_SYSTEM_MAC && FMLEnvironment.dist == Dist.CLIENT)
		{
			try
			{
				//load the Objective C++ function's library
				NativeUtils.loadLibraryFromJar("/natives/macos_input_fixes.dylib");
			}
			catch (IOException e2)
			{
				//uncomment below line and replace with project path if it fails to load from jar e.g. you're running in an ide. also comment the throw line if you do this
				//System.load("<path to project>/native/macos_input_fixes.dylib");
				e2.printStackTrace();
				throw new RuntimeException(e2);
			}
		}
	}
}
