package cofh.core.gui.element;

import cofh.core.gui.GuiCore;
import cofh.core.util.CharacterSingleton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElementTextFieldFiltered extends ElementTextField {

	protected boolean includeVanilla = true;
	protected CharacterSingleton seq = new CharacterSingleton();
	protected Matcher filter;

	public ElementTextFieldFiltered(GuiCore gui, int posX, int posY, int width, int height) {

		super(gui, posX, posY, width, height);
	}

	public ElementTextFieldFiltered(GuiCore gui, int posX, int posY, int width, int height, short limit) {

		super(gui, posX, posY, width, height, limit);
	}

	/**
	 * @param pattern        Regex limit what characters can be typed
	 * @param includeVanilla Include vanilla disallowed characters
	 * @return this
	 */
	public ElementTextFieldFiltered setFilter(Pattern pattern, boolean includeVanilla) {

		filter = pattern.matcher(seq);
		this.includeVanilla = includeVanilla;
		return this;
	}

	@Override
	public boolean isAllowedCharacter(char charTyped) {

		seq.character = charTyped;
		return (!includeVanilla || super.isAllowedCharacter(charTyped)) && (filter == null || filter.reset().matches());
	}

}
