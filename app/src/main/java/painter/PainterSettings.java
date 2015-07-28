package painter;

import android.content.pm.ActivityInfo;

public class PainterSettings {
	public BrushPreset preset = null;
	public String lastPicture = null;
	public boolean forceOpenFile = false;
	public int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
}