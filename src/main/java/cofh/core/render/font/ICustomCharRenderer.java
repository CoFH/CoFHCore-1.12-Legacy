package cofh.core.render.font;

import cofh.core.render.FontRendererCore;

public interface ICustomCharRenderer {

	float renderChar(char letter, boolean italicFlag, float x, float y, FontRendererCore fontRenderer);

	int getCharWidth(char letter, FontRendererCore fontRenderer);

}
