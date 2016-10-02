package cofh.core.render.customcharrendering;

import cofh.core.render.FontRendererCoFH;

public interface ICustomCharRenderer {

	float renderChar(char letter, boolean italicFlag, float x, float y, FontRendererCoFH coFHFontRender);

	int getCharWidth(char letter, FontRendererCoFH coFHFontRender);

}