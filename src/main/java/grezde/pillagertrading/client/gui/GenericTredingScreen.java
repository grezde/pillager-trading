package grezde.pillagertrading.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import grezde.pillagertrading.recipe.ITredingScreenEntry;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class GenericTredingScreen extends Screen {

    protected ResourceLocation TRADING_LOCATION;
    protected static int SCROLLER_HEIGHT = 27;
    protected static int SCROLLER_WIDTH = 6;
    protected static int SCROLLBAR_START_X = 94;
    protected static int SCROLLBAR_START_Y = 18;
    protected static int SCROLLBAR_HEIGHT = 139;
    protected static int SCROLLER_UV_X = 106;
    protected static int SCROLLER_UV_Y = 33;

    protected static int BACKGROUND_WIDTH = 106;
    protected static int BACKGROUND_HEIGHT = 210;

    protected static int TRADING_AREA_START_X = 16;

    protected static int ARROW_UV_X = 121;
    protected static int ARROW_UV_Y = 5;

    protected int shopItem = -1;
    protected final TradeOfferButton[] tradeOfferButtons = new TradeOfferButton[7];
    protected List<? extends ITredingScreenEntry> entries;
    protected int scrollOff;
    protected boolean isDragging;

    protected GenericTredingScreen(ResourceLocation background, List<? extends ITredingScreenEntry> entries) {
        super(GameNarrator.NO_TITLE);
        TRADING_LOCATION = background;
        this.entries = entries;
    }

    protected void postButtonClick() {
        // nothing to do with server afaik
    }

    protected void init() {
        super.init();
        int i = (this.width - BACKGROUND_WIDTH) / 2;
        int j = (this.height - BACKGROUND_HEIGHT) / 2;
        int k = j + 16 + 2;

        for(int l = 0; l < 7; ++l) {
            this.tradeOfferButtons[l] = this.addRenderableWidget(new TradeOfferButton(i + 5, k, l, (button) -> {
                if (button instanceof TradeOfferButton tradeOfferButton) {
                    setShopItem(tradeOfferButton.getIndex() + this.scrollOff);
                    this.postButtonClick();
                }

            }));
            k += 20;
        }
    }

    // RENDER

    protected void renderBg(PoseStack poseStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TRADING_LOCATION);
        int i = (this.width - BACKGROUND_WIDTH) / 2;
        int j = (this.height - BACKGROUND_HEIGHT) / 2;
        this.blit(poseStack, i, j, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    }

    private void renderScroller(PoseStack poseStack) {
        int baseI = (this.width - BACKGROUND_WIDTH) / 2;
        int baseJ = (this.height - BACKGROUND_HEIGHT) / 2;
        int i = entries.size() + 1 - 7;
        if (i > 1) {
            int j = SCROLLBAR_HEIGHT - (SCROLLER_HEIGHT + (i - 1) * SCROLLBAR_HEIGHT / i);
            int k = 1 + j / i + SCROLLBAR_HEIGHT / i;
            int l = SCROLLBAR_HEIGHT - SCROLLER_HEIGHT + 1;
            int i1 = Math.min(l, this.scrollOff * k);
            if (this.scrollOff == i - 1) {
                i1 = l;
            }

            blit(poseStack, baseI + SCROLLBAR_START_X, baseJ + SCROLLBAR_START_Y + i1, SCROLLER_UV_X, SCROLLER_UV_Y, SCROLLER_WIDTH, SCROLLER_HEIGHT);
        } else {
            blit(poseStack, baseI + SCROLLBAR_START_X, baseJ + SCROLLBAR_START_Y, SCROLLER_UV_X+SCROLLER_WIDTH, SCROLLER_UV_Y, SCROLLER_WIDTH, SCROLLER_HEIGHT);
        }

    }

    public void render(PoseStack poseStack, int int1, int int2, float float1) {
        this.renderBackground(poseStack);
        this.renderBg(poseStack);
        super.render(poseStack, int1, int2, float1);
        if (!entries.isEmpty()) {
            int i = (this.width - BACKGROUND_WIDTH) / 2;
            int j = (this.height - BACKGROUND_HEIGHT) / 2;
            int k = j + 16 + 1;
            int l = i + 5 + 5;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TRADING_LOCATION);
            this.renderScroller(poseStack);
            int i1 = 0;

            for(ITredingScreenEntry entry : entries) {
                if (this.canScroll(entries.size()) && (i1 < this.scrollOff || i1 >= 7 + this.scrollOff)) {
                    ++i1;
                } else {
                    ItemStack itemstack = entry.getTradingInitialFirstItem();
                    ItemStack itemstack1 = entry.getTradingFirstItem();
                    ItemStack itemstack2 = entry.getTradingSecondItem();
                    ItemStack itemstack3 = entry.getTradingResult();
                    this.itemRenderer.blitOffset = 100.0F;
                    int j1 = k + 2;
                    this.renderAndDecorateCostA(poseStack, itemstack1, itemstack, l, j1);
                    if (!itemstack2.isEmpty()) {
                        this.itemRenderer.renderAndDecorateFakeItem(itemstack2, i + 5 + 35, j1);
                        this.itemRenderer.renderGuiItemDecorations(this.font, itemstack2, i + 5 + 35, j1);
                    }

                    this.renderButtonArrows(poseStack, entry, i, j1);
                    this.itemRenderer.renderAndDecorateFakeItem(itemstack3, i + 5 + 68, j1);
                    this.itemRenderer.renderGuiItemDecorations(this.font, itemstack3, i + 5 + 68, j1);
                    this.itemRenderer.blitOffset = 0.0F;
                    k += 20;
                    ++i1;
                }
            }

            /*int k1 = this.shopItem;
            MerchantOffer merchantoffer1 = merchantOffers.get(k1);

            if (merchantoffer1.isOutOfStock() && this.isHovering(186, 35, 22, 21, (double)int1, (double)int2) && this.menu.canRestock()) {
                this.renderTooltip(poseStack, DEPRECATED_TOOLTIP, int1, int2);
            }*/

            for(TradeOfferButton merchantscreen$tradeofferbutton : this.tradeOfferButtons) {
                if (merchantscreen$tradeofferbutton.isHoveredOrFocused()) {
                    merchantscreen$tradeofferbutton.renderToolTip(poseStack, int1, int2);
                }

                merchantscreen$tradeofferbutton.visible = merchantscreen$tradeofferbutton.index < entries.size();
            }

            RenderSystem.enableDepthTest();
        }

        // this.renderTooltip(poseStack, int1, int2); this may be for container
    }


    private void renderButtonArrows(PoseStack poseStack, ITredingScreenEntry entry, int int1, int int2) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TRADING_LOCATION);
        if (!entry.isTradeCrossed()) {
            blit(poseStack, int1 + 5 + 35 + 20, int2 + 3, this.getBlitOffset(), (float)ARROW_UV_X, (float)ARROW_UV_Y, 10, 9, 256, 256);
        } else {
            blit(poseStack, int1 + 5 + 35 + 20, int2 + 3, this.getBlitOffset(), (float)(ARROW_UV_X + 10), (float)ARROW_UV_Y, 10, 9, 256, 256);
        }

    }

    private void renderAndDecorateCostA(PoseStack poseStack, ItemStack inputIS, ItemStack inputIS2, int int1, int int2) {
        this.itemRenderer.renderAndDecorateFakeItem(inputIS, int1, int2);
        if (inputIS2.getCount() == inputIS.getCount()) {
            this.itemRenderer.renderGuiItemDecorations(this.font, inputIS, int1, int2);
        } else {
            this.itemRenderer.renderGuiItemDecorations(this.font, inputIS2, int1, int2, inputIS2.getCount() == 1 ? "1" : null);
            // Forge: fixes Forge-8806, code for count rendering taken from ItemRenderer#renderGuiItemDecorations
            PoseStack posestack = new PoseStack();
            posestack.translate(0.0D, 0.0D, (itemRenderer.blitOffset + 200.0F));
            MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            font.drawInBatch(String.valueOf(inputIS.getCount()), (int1 + 14) + 19 - 2 - font.width(String.valueOf(inputIS.getCount())), int2 + 6 + 3, 0xFFFFFF, true, posestack.last().pose(), bufferSource, false, 0, 15728880);
            bufferSource.endBatch();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TRADING_LOCATION);
            this.setBlitOffset(this.getBlitOffset() + 300);
            blit(poseStack, int1 + 7, int2 + 12, this.getBlitOffset(), 0.0F, 176.0F, 9, 2, 512, 256);
            this.setBlitOffset(this.getBlitOffset() - 300);
        }

    }

    // INPUT

    protected void setScrollOff(int newScrollOff) { scrollOff = newScrollOff; }

    private boolean canScroll(int p_99141_) {
        return p_99141_ > 7;
    }

    public boolean mouseScrolled(double p_99127_, double p_99128_, double p_99129_) {
        int i = entries.size();
        if (this.canScroll(i)) {
            int j = i - 7;
            setScrollOff(Mth.clamp((int)((double)this.scrollOff - p_99129_), 0, j));
        }

        return true;
    }

    public boolean mouseDragged(double p_99135_, double p_99136_, int p_99137_, double p_99138_, double p_99139_) {
        int i = entries.size();
        if (this.isDragging) {
            int j = (this.height - BACKGROUND_HEIGHT) / 2 + SCROLLBAR_START_Y;
            int k = j + SCROLLBAR_HEIGHT;
            int l = i - 7;
            float f = ((float)p_99136_ - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
            f = f * (float)l + 0.5F;
            setScrollOff(Mth.clamp((int)f, 0, l));
            return true;
        } else {
            return super.mouseDragged(p_99135_, p_99136_, p_99137_, p_99138_, p_99139_);
        }
    }

    public boolean mouseClicked(double clickX, double clickY, int clickType) {
        this.isDragging = false;
        int i = (this.width - BACKGROUND_WIDTH) / 2;
        int j = (this.height - BACKGROUND_HEIGHT) / 2;
        if (this.canScroll(entries.size()) && clickX > (double)(i + SCROLLBAR_START_X) && clickX < (double)(i + SCROLLBAR_START_X + SCROLLER_WIDTH) && clickY > (double)(j + SCROLLBAR_START_Y) && clickY <= (double)(j + SCROLLBAR_START_Y + SCROLLBAR_HEIGHT + 1)) {
            this.isDragging = true;
        }

        return super.mouseClicked(clickX, clickY, clickType);
    }


    protected void setShopItem(int newShopItem) {
        this.shopItem = newShopItem;
    }

    @OnlyIn(Dist.CLIENT)
    class TradeOfferButton extends Button {
        final int index;

        public TradeOfferButton(int p_99205_, int p_99206_, int p_99207_, Button.OnPress p_99208_) {
            super(p_99205_, p_99206_, 89, 20, CommonComponents.EMPTY, p_99208_);
            this.index = p_99207_;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }

        public void renderToolTip(PoseStack poseStack, int int1, int int2) {
            if (this.isHovered && entries.size() > this.index + GenericTredingScreen.this.scrollOff) {
                if (int1 < this.x + 20) {
                    ItemStack itemstack = entries.get(this.index + GenericTredingScreen.this.scrollOff).getTradingFirstItem();
                    GenericTredingScreen.this.renderTooltip(poseStack, itemstack, int1, int2);
                } else if (int1 < this.x + 50 && int1 > this.x + 30) {
                    ItemStack itemstack2 = entries.get(this.index + GenericTredingScreen.this.scrollOff).getTradingSecondItem();
                    if (!itemstack2.isEmpty()) {
                        GenericTredingScreen.this.renderTooltip(poseStack, itemstack2, int1, int2);
                    }
                } else if (int1 > this.x + 65) {
                    ItemStack itemstack1 = entries.get(this.index + GenericTredingScreen.this.scrollOff).getTradingResult();
                    GenericTredingScreen.this.renderTooltip(poseStack, itemstack1, int1, int2);
                }
            }

        }
    }

}
