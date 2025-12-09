package com.hamarb123.macos_input_fixes.client;

public interface ScrollCallback
{
	void invoke(double horizontal, double vertical, double horizontalWithMomentum, double verticalWithMomentum, double horizontalUngrouped, double verticalUngrouped);
}
