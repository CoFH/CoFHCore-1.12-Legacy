package cofh.core.render.customcharrendering;

import cofh.core.render.CoFHFontRenderer;

public interface ICustomCharRenderer {

    float renderChar(char letter, boolean italicFlag, float x, float y, CoFHFontRenderer coFHFontRender);

    int getCharWidth(char letter, CoFHFontRenderer coFHFontRender);

}
