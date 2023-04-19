package grezde.pillagertrading.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import grezde.pillagertrading.PTMod;
import grezde.pillagertrading.recipe.ITredingScreenEntry;
import grezde.pillagertrading.network.GetPillagerTradingRecipesPacket;
import grezde.pillagertrading.network.LockManuscriptPacket;
import grezde.pillagertrading.network.PTPackets;
import grezde.pillagertrading.recipe.PillagerTradingRecipe;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class IllagerManuscriptScreen extends GenericTredingScreen {

    private Button signButton;
    private Button backButton;

    private Player player;
    private ItemStack itemstack;
    private InteractionHand interactionHand;

    private static final Component TITLE = Component.translatable("gui.pillagertrading.manuscript_title");

    private static IllagerManuscriptScreen INSTANCE = null;
    private static List<? extends ITredingScreenEntry> globalRecipes = null;

    public IllagerManuscriptScreen(Player player, ItemStack itemstack, InteractionHand hand) {
        super(new ResourceLocation(PTMod.MODID, "textures/gui/manuscript2.png"), globalRecipes == null ? new ArrayList<PillagerTradingRecipe>() : globalRecipes);
        INSTANCE = this;
        PTPackets.sendToServer(new GetPillagerTradingRecipesPacket());

        this.player = player;
        this.itemstack = itemstack;
        this.interactionHand = hand;
    }

    public static void updateRecipes(List<PillagerTradingRecipe> recipes) {
        globalRecipes = recipes;
        if(INSTANCE == null)
            return;
        INSTANCE.entries = globalRecipes;
        INSTANCE.setScrollOff(0);
        INSTANCE.setShopItem(-1);
    }

    protected void init() {
        super.init();
        int i = (this.width - BACKGROUND_WIDTH) / 2;
        int j = (this.height + BACKGROUND_HEIGHT) / 2;
        this.signButton = this.addRenderableWidget(new Button(i + 5, j-49, BACKGROUND_WIDTH-10, 20, Component.translatable("book.finalizeButton"), (button) -> {
            PTPackets.sendToServer(new LockManuscriptPacket(interactionHand,  ((PillagerTradingRecipe)(entries.get(shopItem))).getId() ));
            player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE);
            this.minecraft.setScreen((Screen)null);
        }));
        signButton.active = false;
        this.backButton = this.addRenderableWidget(new Button(i + 5, j-27, BACKGROUND_WIDTH-10, 20, CommonComponents.GUI_CANCEL, (button) -> {
            this.minecraft.setScreen((Screen)null);
        }));
    }

    private void setSelectedButton(boolean active, int scrolloff, int shopitem) {
        int sc2 = Mth.clamp(scrolloff, 0, entries.size() + 1 - 7);
        if(shopitem - sc2 >= 0 && shopitem - sc2 < 7)
            tradeOfferButtons[shopitem-sc2].active = active;
    }

    @Override
    protected void setScrollOff(int newScrollOff) {
        if(newScrollOff == scrollOff)
            return;
        setSelectedButton(false, newScrollOff, shopItem);
        setSelectedButton(true, scrollOff, shopItem);
        this.scrollOff = newScrollOff;
    }

    @Override
    protected void setShopItem(int newShopItem) {
        if(shopItem == newShopItem)
            return;
        setSelectedButton(true, scrollOff, shopItem);
        setSelectedButton(false, scrollOff, newShopItem);
        if(newShopItem != -1)
            signButton.active = true;
        this.shopItem = newShopItem;
    }

    public void render(PoseStack poseStack, int int1, int int2, float float1) {
        super.render(poseStack, int1, int2, float1);
        int l = this.font.width(TITLE);
        int i = (this.width - BACKGROUND_WIDTH) / 2;
        int j = (this.height - BACKGROUND_HEIGHT) / 2;
        this.font.draw(poseStack, TITLE, (float)(i + 5 - l / 2 + 48), (float)(j+6), 4210752);
    }

}
