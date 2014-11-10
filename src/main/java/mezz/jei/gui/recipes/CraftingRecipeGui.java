package mezz.jei.gui.recipes;

import mezz.jei.api.gui.IRecipeGui;
import mezz.jei.config.Constants;
import mezz.jei.gui.GuiItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public abstract class CraftingRecipeGui extends Gui implements IRecipeGui {

	private static final int craftOutputSlot = 0;
	private static final int craftInputSlot1 = 1;
	private static final int craftInputSlotCount = 9;

	private ResourceLocation backgroundTexture;

	protected boolean hasRecipe;
	protected ArrayList<GuiItemStack> items = new ArrayList<GuiItemStack>();
	protected int posX;
	protected int posY;

	public CraftingRecipeGui() {
		this.backgroundTexture = new ResourceLocation(Constants.RESOURCE_DOMAIN, Constants.TEXTURE_GUI_PATH + "recipeCrafting.png");

		items.add(new GuiItemStack(124-30+1, 35-17+1, 0));

		for (int y = 0; y < 3; ++y)
			for (int x = 0; x < 3; ++x)
				items.add(new GuiItemStack(1 + x * 18, 1 + y * 18, 0));

		hasRecipe = false;
	}

	protected void setOutput(Object output) {
		items.get(craftOutputSlot).setItemStacks(output, null);
	}

	protected void setInput(Object[] recipeItems, ItemStack focusStack, int width, int height) {
		for (int i = 0; i < recipeItems.length; i++) {
			Object recipeItem = recipeItems[i];
			if (width == 1) {
				if (height == 3)
					setInput(recipeItem, focusStack, (i * 3) + 1);
				else if (height == 2)
					setInput(recipeItem, focusStack, (i * 3) + 4);
				else
					setInput(recipeItem, focusStack, 4);
			} else if (height == 1) {
				setInput(recipeItem, focusStack, i + 6);
			} else if (width == 2) {
				setInput2wide(recipeItem, focusStack, i);
			}else if (height == 2) {
				setInput(recipeItem, focusStack, i + 3);
			} else {
				setInput(recipeItem, focusStack, i);
			}
		}
	}

	protected void setInput(Object input, ItemStack focusStack, int inputIndex) {
		items.get(craftInputSlot1 + inputIndex).setItemStacks(input, focusStack);
	}

	protected void setInput2wide(Object input, ItemStack focusStack, int inputIndex) {
		if (inputIndex > 1)
			inputIndex++;
		if (inputIndex > 4)
			inputIndex++;
		setInput(input, focusStack, inputIndex);
	}

	/* IRecipeGui */

	@Override
	public void setRecipe(Object recipe, ItemStack itemStack) {
		clearItems();
		if (recipe != null) {
			setItemsFromRecipe(recipe, itemStack);
			hasRecipe = true;
		} else {
			hasRecipe = false;
		}
	}

	protected void clearItems() {
		for (GuiItemStack guiItemStack : items) {
			guiItemStack.clearItemStacks();
		}
	}

	abstract protected void setItemsFromRecipe(Object recipe, ItemStack itemStack);

	@Override
	public boolean hasRecipe() {
		return hasRecipe;
	}

	@Override
	public ItemStack getStackUnderMouse(int mouseX, int mouseY) {
		mouseX -= posX;
		mouseY -= posY;

		for (GuiItemStack item : items) {
			if (item.isMouseOver(mouseX, mouseY))
				return item.getItemStack();
		}
		return null;
	}

	@Override
	public int getWidth() {
		return 116;
	}

	@Override
	public int getHeight() {
		return 54;
	}

	@Override
	public void setPosition(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}

	@Override
	public void draw(Minecraft minecraft, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glTranslatef(posX, posY, 0.0F);
		mouseX -= posX;
		mouseY -= posY;

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(backgroundTexture);
		this.drawTexturedModalRect(0, 0, 0, 0, getWidth(), getHeight());

		GuiItemStack hovered = null;
		for (GuiItemStack item : items) {
			if (hovered == null && item.isMouseOver(mouseX, mouseY))
				hovered = item;
			else
				item.draw(minecraft);
		}
		if (hovered != null)
			hovered.drawHovered(minecraft, mouseX, mouseY);

		GL11.glPopMatrix();
	}
}
