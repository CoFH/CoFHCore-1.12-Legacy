package cofh.core.render.customcharrendering;

import cofh.core.render.CoFHFontRender;

public interface ICustomCharRenderer {
	
	float renderChar(char letter, boolean italicFlag, float x, float y, CoFHFontRender coFHFontRender);

	int getCharWidth(char letter, CoFHFontRender coFHFontRender);
}
