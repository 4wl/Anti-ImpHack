package mod.imphack.util.render;

import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class ColorUtil extends Color {

    private static final long serialVersionUID = 1L;


    
    
    public static int rainbow(int delay) {
		double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 50.0);
		rainbowState %= 360;
		return Color.getHSBColor((float) (rainbowState / 360.0f), 0.5f, 1f).getRGB();
	}
    
    	public ColorUtil (int rgb) {
    		super(rgb);
    	}
    	
    	public ColorUtil (int rgba, boolean hasalpha) {
    		super(rgba,hasalpha);
    	}
    	
    	public ColorUtil (int r, int g, int b) {
    		super(r,g,b);
    	}
    	
    	public ColorUtil (int r, int g, int b, int a) {
    		super(r,g,b,a);
    	}
    	
    	public ColorUtil (Color color) {
    		super(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
    	}
    	
    	public ColorUtil (ColorUtil color, int a) {
    		super(color.getRed(),color.getGreen(),color.getBlue(),a);
    	}
    	
    	public static ColorUtil fromHSB(float hue, float saturation, float brightness) {
    		return new ColorUtil(Color.getHSBColor(hue,saturation,brightness));
    	}
    	
    	public float getHue() {
    		return RGBtoHSB(getRed(),getGreen(),getBlue(),null)[0];
    	}
    	
    	public float getSaturation() {
    		return RGBtoHSB(getRed(),getGreen(),getBlue(),null)[1];
    	}
    	
    	public float getBrightness() {
    		return RGBtoHSB(getRed(),getGreen(),getBlue(),null)[2];
    	}
    	
    	public void glColor() {
    		GlStateManager.color(getRed()/255.0f,getGreen()/255.0f,getBlue()/255.0f,getAlpha()/255.0f);
    	}
    
    public static int color(int r, int g, int b, int a) {
		return new Color(r, g, b, a).getRGB();
	}
	
	public static int color(float r, float g, float b, float a) {
		return new Color(r, g, b, a).getRGB();
	}
	 public static int toHex(int r, int g, int b){
        return  (0xff << 24) | ((r&0xff) << 16) | ((g&0xff) << 8) | (b&0xff);
    }

   
 
}
