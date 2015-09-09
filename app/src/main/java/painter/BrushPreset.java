package painter;

import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Color;

public class BrushPreset {
	public float size = 2;
	public int color = Color.BLACK;
	public int color2 = Color.BLUE;
	public Blur blurStyle = null;
	public int blurRadius = 0;
	public int type = CUSTOM;

	public static final int PENCIL = 1;
	public static final int BRUSH = 2;
	public static final int MARKER = 3;
	public static final int ERASER = 4;
	public static final int CUSTOM = 5;
	public static final int BLACK = 6;
	public static final int RED = 7;
	public static final int ORANGE = 8;
	public static final int YELLOW = 9;
	public static final int GREEN = 10;
	public static final int BLUE = 11;
	public static final int PURPLE = 12;
	public static final int PEN = 13;

	public static final int BLUR_NORMAL = 1;
	public static final int BLUR_SOLID = 2;
	public static final int BLUR_OUTER = 3;
	public static final int BLUR_INNER = 4;


	public BrushPreset(int type, int color) {
		switch (type) {
			case PENCIL:
				if(this.color==0xFFFFFFFF){
					this.color=this.color2;
				}
				set(2, color, Blur.INNER, 10);
				break;
			case BRUSH:
				if(this.color==0xFFFFFFFF){
					this.color=this.color2;
				}
				set(15, color, Blur.NORMAL, 18);
				break;
			case MARKER:
				if(this.color==0xFFFFFFFF){
					this.color=this.color2;
				}
				set(20, color);
				break;
			case PEN:
				if(this.color==0xFFFFFFFF){
					this.color=this.color2;
				}
				set(20, color);
				break;
			case ERASER:
				this.color2=this.color;
				set(10, 0xFFFFFFFF);
				break;
			case CUSTOM:
				setColor(color);
				break;
			case BLACK:
				setColor(0xFF000000);
				break;
			case RED:
				setColor(0xFFFF0000);
				break;
			case ORANGE:
				setColor(0xFFFFA500);
				break;
			case YELLOW:
				setColor(0xFFFFFF00);
				break;
			case GREEN:
				setColor(0xFF008000);
				break;
			case BLUE:
				setColor(0xFF0000FF);
				break;
			case PURPLE:
				setColor(0xFF8b008b);
				break;
		}
		setType(type);
	}

	public BrushPreset(float size) {
		set(size);
	}

	public BrushPreset(float size, int color) {
		set(size, color);
	}

	public BrushPreset(float size, Blur blurStyle, int blurRadius) {
		set(size, blurStyle, blurRadius);
	}

	public BrushPreset(float size, int blurStyle, int blurRadius) {
		set(size, blurStyle, blurRadius);
	}

	public BrushPreset(float size, int color, Blur blurStyle, int blurRadius) {
		set(size, color, blurStyle, blurRadius);
	}

	public BrushPreset(float size, int color, int blurStyle, int blurRadius) {
		set(size, color, blurStyle, blurRadius);
	}

	public void set(float size) {
		setSize(size);
	}

	public void set(float size, int color) {
		setSize(size);
		setColor(color);
	}

	public void set(float size, Blur blurStyle, int blurRadius) {
		setSize(size);
		setBlur(blurStyle, blurRadius);
	}

	public void set(float size, int blurStyle, int blurRadius) {
		setSize(size);
		setBlur(blurStyle, blurRadius);
	}

	public void set(float size, int color, Blur blurStyle, int blurRadius) {
		setSize(size);
		setBlur(blurStyle, blurRadius);
		setColor(color);
	}

	public void set(float size, int color, int blurStyle, int blurRadius) {
		setSize(size);
		setBlur(blurStyle, blurRadius);
		setColor(color);
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setSize(float size) {
		if (this.size != size) {
			setType(CUSTOM);
		}
		this.size = (size > 0) ? size : 1;
	}

	public void setBlur(Blur blurStyle, int blurRadius) {
		if (this.blurStyle != blurStyle || this.blurRadius != blurRadius) {
			setType(CUSTOM);
		}
		this.blurStyle = blurStyle;
		this.blurRadius = blurRadius;
	}

	public void setBlur(int blurStyle, int blurRadius) {
		int style = (this.blurStyle != null) ? this.blurStyle.ordinal() + 1 : 0;
		if (style != blurStyle || this.blurRadius != blurRadius) {
			setType(CUSTOM);
		}

		switch (blurStyle) {
			case BLUR_NORMAL:
				this.blurStyle = Blur.NORMAL;
				break;
			case BLUR_SOLID:
				this.blurStyle = Blur.SOLID;
				break;
			case BLUR_OUTER:
				this.blurStyle = Blur.OUTER;
				break;
			case BLUR_INNER:
				this.blurStyle = Blur.INNER;
				break;
			default:
				this.blurStyle = null;
				break;
		}
		this.blurRadius = blurRadius;
	}
}