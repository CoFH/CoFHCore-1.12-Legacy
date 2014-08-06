package cofh.core.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiLimitedTextField extends GuiTextField {

	public String listOfValidCharacters = "";

	public GuiLimitedTextField(FontRenderer fontRenderer, int xPos, int yPos, int width, int height, String validChars) {

		super(fontRenderer, xPos, yPos, width, height);
		listOfValidCharacters = validChars;
	}

	@Override
	public boolean textboxKeyTyped(char par1, int par2) {

		switch (par1) {
		case 1:
			return super.textboxKeyTyped(par1, par2);
		case 3:
			return super.textboxKeyTyped(par1, par2);
		case 22:
			return false;
		case 24:
			return super.textboxKeyTyped(par1, par2);
		default:
			switch (par2) {
			case 14:
				return super.textboxKeyTyped(par1, par2);
			case 199:
				return super.textboxKeyTyped(par1, par2);
			case 203:
				return super.textboxKeyTyped(par1, par2);
			case 205:
				return super.textboxKeyTyped(par1, par2);
			case 207:
				return super.textboxKeyTyped(par1, par2);
			case 211:
				return super.textboxKeyTyped(par1, par2);
			default:
				if (listOfValidCharacters.indexOf(par1) >= 0) {
					return super.textboxKeyTyped(par1, par2);
				}
				return false;
			}
		}
	}

}
