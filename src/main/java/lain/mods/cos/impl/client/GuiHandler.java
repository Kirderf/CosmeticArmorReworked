package lain.mods.cos.impl.client;

import java.util.Set;
import com.google.common.collect.ImmutableSet;
import lain.mods.cos.impl.ModConfigs;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.gui.GuiCosArmorButton;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import lain.mods.cos.impl.client.gui.GuiCosArmorToggleButton;
import lain.mods.cos.impl.client.gui.IShiftingWidget;
import lain.mods.cos.impl.network.packet.PacketOpenCosArmorInventory;
import lain.mods.cos.impl.network.packet.PacketOpenNormalInventory;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

public enum GuiHandler
{

    INSTANCE;

    public static final Set<Integer> ButtonIds = ImmutableSet.of(76, 77);

    private int lastLeft = -1;

    private void handleGuiDrawPre(GuiScreenEvent.DrawScreenEvent.Pre event)
    {
        if (event.getGui() instanceof InventoryScreen || event.getGui() instanceof GuiCosArmorInventory)
        {
            ContainerScreen<?> gui = (ContainerScreen<?>) event.getGui();
            if (lastLeft != gui.guiLeft)
            {
                int diffLeft = gui.guiLeft - lastLeft;
                lastLeft = gui.guiLeft;
                gui.buttons.stream().filter(IShiftingWidget.class::isInstance).forEach(b -> b.x += diffLeft);
            }
        }
    }

    private void handleGuiInitPost(GuiScreenEvent.InitGuiEvent.Post event)
    {
        if (event.getGui() instanceof InventoryScreen || event.getGui() instanceof GuiCosArmorInventory)
        {
            ContainerScreen<?> gui = (ContainerScreen<?>) event.getGui();
            lastLeft = gui.guiLeft;
            if (!ModConfigs.CosArmorGuiButton_Hidden.get())
                event.addWidget(new GuiCosArmorButton(gui.guiLeft + ModConfigs.CosArmorGuiButton_Left.get()/* 65 */, gui.guiTop + ModConfigs.CosArmorGuiButton_Top.get()/* 67 */, 10, 10, event.getGui() instanceof GuiCosArmorInventory ? "cos.gui.buttonnormal" : "cos.gui.buttoncos", button -> {
                    if (gui instanceof GuiCosArmorInventory)
                    {
                        InventoryScreen newGui = new InventoryScreen(gui.getMinecraft().player);
                        newGui.oldMouseX = ((GuiCosArmorInventory) gui).oldMouseX;
                        newGui.oldMouseY = ((GuiCosArmorInventory) gui).oldMouseY;
                        gui.getMinecraft().displayGuiScreen(newGui);
                        ModObjects.network.sendToServer(new PacketOpenNormalInventory());
                    }
                    else
                    {
                        ModObjects.network.sendToServer(new PacketOpenCosArmorInventory());
                    }
                }));
            if (!ModConfigs.CosArmorToggleButton_Hidden.get())
                event.addWidget(new GuiCosArmorToggleButton(gui.guiLeft + ModConfigs.CosArmorToggleButton_Left.get()/* 59 */, gui.guiTop + ModConfigs.CosArmorToggleButton_Top.get()/* 72 */, 5, 5, "", PlayerRenderHandler.Disabled ? 1 : 0, button -> {
                    PlayerRenderHandler.Disabled = !PlayerRenderHandler.Disabled;
                    ((GuiCosArmorToggleButton) button).state = PlayerRenderHandler.Disabled ? 1 : 0;
                }));
        }
        // TODO add baubles integration
    }

    public void registerEvents()
    {
        MinecraftForge.EVENT_BUS.addListener(this::handleGuiDrawPre);
        MinecraftForge.EVENT_BUS.addListener(this::handleGuiInitPost);
        setupGuiFactory();
    }

    private void setupGuiFactory()
    {
        ScreenManager.registerFactory(ModObjects.typeContainerCosArmor, GuiCosArmorInventory::new);
    }

}
