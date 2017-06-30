package cofh.lib.gui.element;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiColor;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class ElementTextField extends ElementBase {

	public int borderColor = new GuiColor(55, 55, 55).getColor();
	public int backgroundColor = new GuiColor(139, 139, 139).getColor();
	public int disabledColor = new GuiColor(198, 198, 198).getColor();
	public int selectedLineColor = new GuiColor(160, 160, 224).getColor();
	public int textColor = new GuiColor(224, 224, 224).getColor();
	public int selectedTextColor = new GuiColor(224, 224, 224).getColor();
	public int defaultCaretColor = new GuiColor(255, 255, 255).getColor();

	@Deprecated
	// dummy variable to avoid crashes with older implementation
	protected int renderStart;

	protected char[] text;
	protected int textLength;
	protected int selectionStart, selectionEnd;
	protected int renderStartX, renderStartY;
	protected int caret, prevCaret, caretX;

	private boolean isFocused;
	private boolean canFocusChange = true;

	private boolean selecting, pressed;

	private byte caretCounter, counterOffset;
	protected boolean caretInsert;
	protected boolean smartCaret = true;
	protected boolean smartCaretCase = true;

	protected boolean multiline = false;

	protected boolean enableStencil = true;

	public ElementTextField(GuiBase gui, int posX, int posY, int width, int height) {

		this(gui, posX, posY, width, height, (short) 32);
	}

	public ElementTextField(GuiBase gui, int posX, int posY, int width, int height, short limit) {

		super(gui, posX, posY, width, height);
		setMaxLength(limit);
	}

	public ElementTextField setTextColor(Number textColor, Number selectedTextColor) {

		if (textColor != null) {
			this.textColor = textColor.intValue();
		}
		if (selectedTextColor != null) {
			this.selectedTextColor = selectedTextColor.intValue();
		}
		return this;
	}

	public ElementTextField setSelectionColor(Number selectedLineColor, Number defaultCaretColor) {

		if (selectedLineColor != null) {
			this.selectedLineColor = selectedLineColor.intValue();
		}
		if (defaultCaretColor != null) {
			this.defaultCaretColor = defaultCaretColor.intValue();
		}
		return this;
	}

	public ElementTextField setBackgroundColor(Number backgroundColor, Number disabledColor, Number borderColor) {

		if (backgroundColor != null) {
			this.backgroundColor = backgroundColor.intValue();
		}
		if (disabledColor != null) {
			this.disabledColor = disabledColor.intValue();
		}
		if (borderColor != null) {
			this.borderColor = borderColor.intValue();
		}
		return this;
	}

	public ElementTextField setMultiline(boolean multi) {

		multiline = multi;
		return this;
	}

	public ElementTextField setFocusable(boolean focusable) {

		canFocusChange = focusable;
		return this;
	}

	public ElementTextField setFocused(boolean focused) {

		if (isFocusable()) {
			isFocused = focused;
			resetCaretFlash();
		}
		return this;
	}

	public ElementTextField setText(String text) {

		selectionStart = 0;
		selectionEnd = textLength;
		writeText(text);
		return this;
	}

	public ElementTextField setMaxLength(short limit) {

		char[] oldText = text;
		text = new char[limit];
		textLength = Math.min(limit, textLength);
		if (oldText != null) {
			System.arraycopy(oldText, 0, text, 0, textLength);
		}
		findRenderStart();
		return this;
	}

	public int getContentLength() {

		return textLength;
	}

	public int getMaxLength() {

		return text.length;
	}

	/**
	 * @deprecated Use <tt>getMaxLength</tt>
	 */
	@Deprecated
	public int getMaxStringLength() {

		return text.length;
	}

	public boolean isFocused() {

		return isEnabled() && isFocused;
	}

	public boolean isFocusable() {

		return canFocusChange;
	}

	public int getContentHeight() {

		FontRenderer font = getFontRenderer();
		int height = font.FONT_HEIGHT;
		if (multiline) {
			for (int i = 0; i < textLength; ++i) {
				if (text[i] == '\n') {
					height += font.FONT_HEIGHT;
				}
			}
		}
		return height;
	}

	public int getVisibleHeight() {

		FontRenderer font = getFontRenderer();
		int height = font.FONT_HEIGHT;
		if (multiline) {
			for (int i = 0; i < textLength; ++i) {
				if (text[i] == '\n') {
					height += font.FONT_HEIGHT;
				}
			}
		}
		return Math.min(height - renderStartY, sizeY);
	}

	public int getContentWidth() {

		FontRenderer font = getFontRenderer();
		int width = 0;
		for (int i = 0; i < textLength; ++i) {
			width += font.getCharWidth(text[i]);
		}
		return width;
	}

	public int getVisibleWidth() {

		FontRenderer font = getFontRenderer();
		int width = 0, endX = sizeX - 1, maxWidth = 0;
		if (multiline) {
			for (int i = 0; i < textLength; ++i) {
				char c = text[i];
				int charW = font.getCharWidth(c);
				if (c == '\n') {
					maxWidth = Math.max(maxWidth, width);
					width = 0;
				} else {
					width += charW;
				}
				if ((width - renderStartX) >= endX) {
					maxWidth = endX + renderStartX;
					break;
				}
			}
			maxWidth -= renderStartX;
		} else {
			for (int i = renderStartX; i < textLength; ++i) {
				char c = text[i];
				int charW = font.getCharWidth(c);
				maxWidth += charW;
				if (maxWidth >= endX) {
					maxWidth = endX;
					break;
				}
			}
		}
		return maxWidth;
	}

	public String getText() {

		return new String(text, 0, textLength);
	}

	public String getSelectedText() {

		if (selectionStart != selectionEnd) {
			return new String(text, selectionStart, selectionEnd);
		}
		return getText();
	}

	public void writeText(String text) {

		int i = 0;
		for (int e = text.length(); i < e; ++i) {
			if (!insertCharacter(text.charAt(i))) {
				break;
			}
		}
		clearSelection();
		findRenderStart();
		onCharacterEntered(i > 0);
	}

	public boolean isAllowedCharacter(char charTyped) {

		return (multiline && charTyped == '\n') || ChatAllowedCharacters.isAllowedCharacter(charTyped);
	}

	protected boolean onEnter() {

		if (multiline) {
			boolean typed;
			if (caretInsert && selectionStart == selectionEnd) {
				caretInsert = false;
				typed = insertCharacter('\n');
				caretInsert = true;
			} else {
				typed = insertCharacter('\n');
			}
			clearSelection();
			findRenderStart();
			onCharacterEntered(typed);

			resetCaretFlash();
			return true;
		}
		return false;
	}

	protected void onFocusLost() {

	}

	protected void onCharacterEntered(boolean success) {

	}

	protected void resetCaretFlash() {

		int v = Minecraft.getMinecraft().ingameGUI.getUpdateCounter();
		counterOffset = (byte) ((v - counterOffset) & 63);
		counterOffset += (byte) ((v - counterOffset) & 63);
		caretCounter = 0;
	}

	protected boolean insertCharacter(char charTyped) {

		if (isAllowedCharacter(charTyped)) {

			if (selectionStart != selectionEnd) {
				if (caret == selectionStart) {
					++caret;
				}
				text[selectionStart++] = charTyped;
				return true;
			}

			int len = getMaxLength();
			if ((caretInsert && caret == len) || textLength == len) {
				return false;
			}

			if (!caretInsert || (multiline && text[caret] == '\n')) {
				if (caret < textLength) {
					System.arraycopy(text, caret, text, caret + 1, textLength - caret);
				}
				++textLength;
			}
			text[caret++] = charTyped;
			return true;
		} else {
			return true;
		}
	}

	protected void findRenderStart() {

		caret = MathHelper.clamp(caret, 0, textLength);
		if (selectionStart == selectionEnd) {
			selectionStart = selectionEnd = caret;
		}

		if (multiline) {
			findRenderStartML();
			return;
		}

		renderStartY = 0;

		if (caret < renderStartX) {
			renderStartX = caret;
			return;
		}

		FontRenderer font = getFontRenderer();
		int endX = sizeX - 2;

		for (int i = renderStartX, width = 0; i < caret; ++i) {
			width += font.getCharWidth(text[i]);
			while (width >= endX) {
				width -= font.getCharWidth(text[renderStartX++]);
				if (renderStartX >= textLength) {
					return;
				}
			}
		}
	}

	protected void findRenderStartML() {

		if (caret == textLength && textLength == 0) {
			renderStartX = renderStartY = 0;
			return;
		}
		FontRenderer font = getFontRenderer();
		int widthLeft = 0;
		int breaksAbove = 0;
		for (int i = caret; i-- > 0; ) {
			char c = text[i];
			if (c == '\n') {
				for (; i > 0; --i) {
					c = text[i];
					if (c == '\n') {
						breaksAbove += font.FONT_HEIGHT;
					}
				}
				break;
			}
			widthLeft += font.getCharWidth(c);
		}
		caretX = widthLeft;

		int pos = Math.max(0, (sizeY - 2) / font.FONT_HEIGHT) * font.FONT_HEIGHT;
		if (caret > 0 && text[caret - 1] == '\n') {
			renderStartX = 0;
			if (caret == textLength) {
				renderStartY -= pos;
				renderStartY &= ~renderStartY >> 31;
			}
		}

		while ((breaksAbove - renderStartY) < 0) {
			renderStartY -= font.FONT_HEIGHT;
		}
		while ((breaksAbove - renderStartY) >= pos) {
			renderStartY += font.FONT_HEIGHT;
		}

		int dir = prevCaret > caret ? 1 : -1;
		for (int i = 0; (widthLeft - renderStartX) < 0; i += dir) {
			char c = text[caret + i];
			if (c == '\n') {
				break;
			}
			renderStartX -= font.getCharWidth(c);
		}
		renderStartX &= ~renderStartX >> 31;
		pos = sizeX - 2 - 3;
		for (int i = 0; (widthLeft - renderStartX) >= pos; ++i) {
			renderStartX += font.getCharWidth(text[caret - i]);
		}
		prevCaret = caret;
	}

	protected void clearSelection() {

		if (selectionStart != selectionEnd) {
			if (selectionEnd < textLength) {
				System.arraycopy(text, selectionEnd, text, selectionStart, textLength - selectionEnd);
			}
			textLength -= selectionEnd - selectionStart;

			selectionEnd = caret = selectionStart;
			findRenderStart();
			onCharacterEntered(true);
		}
	}

	protected final int seekNextCaretLocation(int pos) {

		return seekNextCaretLocation(pos, true);
	}

	protected int seekNextCaretLocation(int pos, boolean forward) {

		int dir = forward ? 1 : -1;
		int e = forward ? textLength : 0;
		if (pos != e) {
			pos += dir;
		}
		char prevChar = pos == textLength ? 0 : text[pos];
		if (!forward) {
			if (pos != e && Character.isSpaceChar(prevChar)) {
				pos += !Character.isSpaceChar(text[pos + dir]) ? dir : 0;
			}
		} else if (pos != e && Character.isSpaceChar(prevChar)) {
			pos -= !Character.isSpaceChar(text[pos - dir]) ? dir : 0;
		}
		prevChar = text[pos];
		int i = pos;

		if (smartCaret) {
			boolean originalCase = Character.isUpperCase(prevChar);
			for (; i != e; i += dir) {
				char curChar = text[i];
				boolean dig = Character.isLetterOrDigit(curChar) != Character.isLetterOrDigit(prevChar);
				boolean caze = !dig && Character.isUpperCase(curChar) != Character.isUpperCase(prevChar);
				boolean space = Character.isWhitespace(prevChar) != Character.isWhitespace(curChar);
				if (dig || (caze & smartCaretCase) || space) {
					int o = 0;
					if (smartCaretCase && caze) {
						o = originalCase && Character.isUpperCase(prevChar) ? -dir : 0;
					} else {
						if (space) {
							if (forward) {
								if (i != e && !Character.isWhitespace(text[i + dir])) {
									o = Character.isWhitespace(curChar) ? 1 : 0;
								}
							} else {
								o = 1;
							}
						}
					}
					return i + o;
				}
				prevChar = curChar;
			}
		} else {
			for (; i != e; i += dir) {
				char curChar = text[i];
				if (Character.isSpaceChar(curChar) != Character.isSpaceChar(prevChar)) {
					return i;
				}
			}
		}
		return forward ? textLength : 0;
	}

	@Override
	public boolean onKeyTyped(char charTyped, int keyTyped) {

		if (!isFocused()) {
			return false;
		}

		switch (charTyped) {
			case 1: // ^A
				selectionEnd = caret = textLength;
				selectionStart = 0;
				findRenderStart();
				return true;
			case 3: // ^C
				if (selectionStart != selectionEnd) {
					GuiScreen.setClipboardString(getSelectedText());
				}
				return true;
			case 24: // ^X
				if (selectionStart != selectionEnd) {
					GuiScreen.setClipboardString(getSelectedText());
					clearSelection();

					resetCaretFlash();
				}

				return true;
			case 22: // ^V
				writeText(GuiScreen.getClipboardString());

				resetCaretFlash();

				return true;
			default:
				switch (keyTyped) {
					case Keyboard.KEY_ESCAPE:
						setFocused(false);
						return !isFocused();
					case Keyboard.KEY_RETURN:
					case Keyboard.KEY_NUMPADENTER:
						return onEnter();
					case Keyboard.KEY_INSERT:
						if (GuiScreen.isShiftKeyDown()) {
							writeText(GuiScreen.getClipboardString());

							resetCaretFlash();
						} else {
							caretInsert = !caretInsert;
						}

						return true;
					case Keyboard.KEY_CLEAR: // mac only (clear selection)
						clearSelection();

						resetCaretFlash();

						return true;
					case Keyboard.KEY_DELETE: // delete
						boolean changed = false;
						if (!GuiScreen.isShiftKeyDown()) {
							if (selectionStart != selectionEnd) {
								clearSelection();
							} else if (GuiScreen.isCtrlKeyDown()) {
								int size = seekNextCaretLocation(caret, true) - caret;
								selectionStart = caret;
								selectionEnd = caret + size;
								clearSelection();
							} else {
								if (caret < textLength && textLength > 0) {
									--textLength;
									System.arraycopy(text, caret + 1, text, caret, textLength - caret);
									changed = true;
								}
								findRenderStart();

								onCharacterEntered(changed);
							}

							resetCaretFlash();

							return true;
						}
						// continue.. (shift+delete = backspace)
					case Keyboard.KEY_BACK: // backspace
						changed = false;
						boolean calledEntered = true, onBreak = false;
						if (selectionStart != selectionEnd) {
							clearSelection();
						} else if (GuiScreen.isCtrlKeyDown()) {
							int size = seekNextCaretLocation(caret, false) - caret;
							selectionStart = caret + size;
							selectionEnd = caret;
							clearSelection();
						} else {
							calledEntered = false;
							if (caret > 0 && textLength > 0) {
								if (caret != textLength) {
									System.arraycopy(text, caret, text, caret - 1, textLength - caret);
								}
								onBreak = text[--caret] == '\n';
								--textLength;
								changed = true;
							}
						}
						int old = caret;
						if (!onBreak) {
							for (int i = 3; i-- > 0 && caret > 1 && text[caret - 1] != '\n'; --caret) {

							}
						}
						findRenderStart();
						caret = old;

						if (!calledEntered) {
							onCharacterEntered(changed);
						}

						resetCaretFlash();

						return true;
					case Keyboard.KEY_HOME: // home
						int begin = 0;
						if (!GuiScreen.isCtrlKeyDown()) {
							for (int i = caret - 1; i > 0; --i) {
								if (text[i] == '\n') {
									begin = Math.min(i + 1, textLength);
									break;
								}
							}
						}

						if (GuiScreen.isShiftKeyDown()) {
							if (caret >= selectionEnd) {
								selectionEnd = selectionStart;
							}
							selectionStart = begin;
						} else {
							selectionStart = selectionEnd = begin;
						}
						caret = begin;
						findRenderStart();

						resetCaretFlash();

						return true;
					case Keyboard.KEY_END: // end
						int end = textLength;
						if (!GuiScreen.isCtrlKeyDown()) {
							for (int i = caret; i < textLength; ++i) {
								if (text[i] == '\n') {
									end = i;
									break;
								}
							}
						}

						if (GuiScreen.isShiftKeyDown()) {
							if (caret <= selectionStart) {
								selectionStart = selectionEnd;
							}
							selectionEnd = end;
						} else {
							selectionStart = selectionEnd = end;
						}
						caret = end;
						findRenderStart();

						resetCaretFlash();

						return true;
					case Keyboard.KEY_LEFT: // left arrow
					case Keyboard.KEY_RIGHT: // right arrow
						int size = keyTyped == 203 ? -1 : 1;
						boolean shiftCaret = false;
						if (GuiScreen.isCtrlKeyDown()) {
							size = seekNextCaretLocation(caret, keyTyped == 205) - caret;
						} else if (StringHelper.isAltKeyDown() && GuiScreen.isShiftKeyDown()) {
							caret = seekNextCaretLocation(caret, keyTyped == 205);
							selectionStart = selectionEnd = caret;
							size = seekNextCaretLocation(caret, keyTyped != 205) - caret;
							shiftCaret = true;
						}

						if (!GuiScreen.isShiftKeyDown()) {
							selectionStart = selectionEnd = caret;
						}

					{
						int t = caret;
						caret = MathHelper.clamp(caret + size, 0, textLength);
						size = caret - t;
					}

					if (GuiScreen.isShiftKeyDown()) {
						if (caret == selectionStart + size) {
							selectionStart = caret;
						} else if (caret == selectionEnd + size) {
							selectionEnd = caret;
						}

						if (selectionStart > selectionEnd) {
							int t = selectionStart;
							selectionStart = selectionEnd;
							selectionEnd = t;
						}
					}

					if (shiftCaret) {
						caret = caret - size;
					}
					findRenderStart();

					resetCaretFlash();

					return true;
					case Keyboard.KEY_UP:
					case Keyboard.KEY_DOWN:
						if (!multiline) {
							return false;
						}

						if (!GuiScreen.isShiftKeyDown()) {
							selectionStart = selectionEnd = caret;
						}
						int dir = keyTyped == Keyboard.KEY_UP ? -1 : 1;
						end = dir == -1 ? 0 : textLength;
						int i = caret, pos = caretX;
						old = i;
						for (; i != end; i += dir) {
							if ((dir != -1 || i != caret) && text[i] == '\n') {
								if (i != end) {
									i += dir;
								} else {
									return true;
								}
								break;
							}
						}
						l:
						if (dir == -1) {
							for (; i > 0 && text[i] != '\n'; --i) {

							}
							if (i == 0) {
								if (text[0] == '\n') {
									caret = 0;
									findRenderStart();
									caretX = pos;
								}
								break l;
							}
							++i;
						}
						FontRenderer font = getFontRenderer();
						for (int width = 0; i <= textLength; ++i) {
							char c = i < textLength ? text[i] : 0;
							if (i == textLength || c == '\n' || width >= pos) {
								caret = i;
								findRenderStart();
								caretX = pos;
								break;
							} else {
								width += font.getCharWidth(c);
							}
						}

						size = caret - old;

						if (GuiScreen.isShiftKeyDown()) {
							if (selectionStart == selectionEnd) {
								selectionStart = selectionEnd = old;
							}
							if (caret == selectionStart + size) {
								selectionStart = caret;
							} else if (caret == selectionEnd + size) {
								selectionEnd = caret;
							}

							if (selectionStart > selectionEnd) {
								int t = selectionStart;
								selectionStart = selectionEnd;
								selectionEnd = t;
							}
						}

						resetCaretFlash();

						return true;
					default:
						if (isAllowedCharacter(charTyped)) {
							boolean typed = insertCharacter(charTyped);
							clearSelection();
							findRenderStart();
							onCharacterEntered(typed);
							return true;
						} else {
							return false;
						}
				}
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		pressed = mouseButton == 0;
		selecting = mouseButton == 0 && isFocused();
		l:
		if (selecting) {
			if (textLength == 0) {
				selectionStart = selectionEnd = caret = 0;
				break l;
			}
			FontRenderer font = getFontRenderer();
			int posX = mouseX - this.posX - 1, posY = mouseY - this.posY - 1;
			s:
			if (!multiline) {
				for (int i = renderStartX, width = 0; ; ) {
					int charW = font.getCharWidth(text[i]);
					if ((width += charW) > posX || ++i >= textLength) {
						selectionStart = selectionEnd = caret = i;
						break;
					}
				}
			} else {
				posX += renderStartX;
				posY += renderStartY;
				int maxX = 0;
				boolean found = false;
				for (int i = 0, width = 0, height = font.FONT_HEIGHT; i < textLength; ) {
					char c = text[i];
					int charW = 0;
					if (c == '\n') {
						if (height > posY) {
							maxX = i;
							break;
						}
						found = false;
						width = 0;
						height += font.FONT_HEIGHT;
					} else {
						charW = font.getCharWidth(c);
					}
					if (!found) {
						maxX = i;
					}
					if ((width += charW) > posX || ++i >= textLength) {
						if (posY < height || i >= textLength) {
							selectionStart = selectionEnd = caret = i;
							break s;
						} else {
							++i;
							found = true;
						}
					}
				}
				selectionStart = selectionEnd = caret = maxX;
			}
			findRenderStart();
		}

		setFocused(true);
		return true;
	}

	@Override
	public void update(int mouseX, int mouseY) {

		caretCounter = (byte) ((Minecraft.getMinecraft().ingameGUI.getUpdateCounter() - counterOffset) & 63);
		// if (selecting) {
		// FontRenderer font = getFontRenderer();
		// int pos = mouseX - posX - 1;
		// for (int i = renderStart, width = 0; i < textLength; ++i) {
		// }
		// }
	}

	@Override
	public void onMouseReleased(int mouseX, int mouseY) {

		if (!pressed) {
			boolean focus = isFocused();
			setFocused(false);
			if (focus && !isFocused()) {
				onFocusLost();
			}
		}
		pressed = selecting = false;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		drawModalRect(posX - 1, posY - 1, posX + sizeX + 1, posY + sizeY + 1, borderColor);
		drawModalRect(posX, posY, posX + sizeX, posY + sizeY, isEnabled() ? backgroundColor : disabledColor);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

		boolean enableStencil = this.enableStencil;
		int bit = -1;
		l:
		if (enableStencil) {
			bit = MinecraftForgeClient.reserveStencilBit();
			if (bit == -1) {
				enableStencil = false;
				break l;
			}
			GL11.glEnable(GL11.GL_STENCIL_TEST);
			drawStencil(posX + 1, posY + 1, posX + sizeX - 1, posY + sizeY - 1, 1 << bit);
		}

		FontRenderer font = getFontRenderer();
		char[] text = this.text;
		int startX = posX + 1 - (multiline ? renderStartX : 0), endX = sizeX - 1;
		int startY = posY + 1 - renderStartY, endY = startY + font.FONT_HEIGHT;
		int drawY = renderStartY + Math.max(0, (sizeY - 2) / font.FONT_HEIGHT) * font.FONT_HEIGHT;
		if (enableStencil && sizeY - (drawY - renderStartY) > 2) {
			drawY += font.FONT_HEIGHT;
		}
		int drawX = endX + (multiline ? renderStartX : 0);
		for (int i = multiline ? 0 : renderStartX, width = 0, height = 0; i <= textLength; ++i) {
			boolean end = i == textLength, draw = height >= renderStartY && width < drawX && height < drawY;
			int charW = 2;
			char c = 0;
			if (!end) {
				c = text[i];
				if (draw) {
					charW = multiline && c == '\n' ? 2 : font.getCharWidth(c);
				}
				int tWidth = width + charW;
				if (multiline) {
					if (!enableStencil) {
						draw &= width >= renderStartX;
					}
					draw &= tWidth > renderStartX;
				}
				l:
				if (!enableStencil && tWidth > endX) {
					draw = false;
					if (multiline) {
						if (c == '\n') {
							break l;
						}
						continue;
					}
					break;
				}
			}

			boolean drawCaret = draw && i == caret && (caretCounter & 31) < 16 && isFocused();
			if (drawCaret) {
				int caretEnd = width + 2;
				if (caretInsert) {
					caretEnd = width + charW;
				}
				drawModalRect(startX + width, startY - 1 + height, startX + caretEnd, endY + height, (0xFF000000 & defaultCaretColor) | (~defaultCaretColor & 0xFFFFFF));
			}

			if (draw && !end) {
				boolean selected = i >= selectionStart & i < selectionEnd;
				if (selected) {
					drawModalRect(startX + width, startY + height, startX + width + charW, endY + height, selectedLineColor);
				}
				if (c != '\n') {
					font.drawString(String.valueOf(c), startX + width, startY + height, selected ? selectedTextColor : textColor);
				}
			}

			if (drawCaret) {
				int caretEnd = width + 2;
				if (caretInsert) {
					caretEnd = width + charW;
				}

				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
				gui.drawSizedRect(startX + width, startY - 1 + height, startX + caretEnd, endY + height, -1);
				GlStateManager.disableBlend();
			}

			if (c == '\n') {
				height += font.FONT_HEIGHT;
				charW = width = 0;
				if (height > drawY) {
					break;
				}
			}

			width += charW;
			if (!multiline && width > endX) {
				break;
			}
		}

		if (enableStencil) {
			GL11.glDisable(GL11.GL_STENCIL_TEST);
			MinecraftForgeClient.releaseStencilBit(bit);
		}
	}

}
