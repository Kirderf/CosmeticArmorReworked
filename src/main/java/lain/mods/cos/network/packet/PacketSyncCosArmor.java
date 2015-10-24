package lain.mods.cos.network.packet;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import lain.mods.cos.inventory.InventoryCosArmor;
import lain.mods.cos.network.NetworkPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.client.FMLClientHandler;

public class PacketSyncCosArmor extends NetworkPacket
{

    int entityId;
    int slot;
    boolean isSkinCosArmor;
    ItemStack itemCosArmor;

    public PacketSyncCosArmor(EntityPlayer player, int slot)
    {
        this.entityId = player.getEntityId();
        this.slot = slot;
        this.isSkinCosArmor = InventoryCosArmor.isSkinCosArmor(player, slot);
        this.itemCosArmor = InventoryCosArmor.getCosArmorSlot(player, slot);
    }

    @Override
    public void handlePacketClient()
    {
        Minecraft mc = FMLClientHandler.instance().getClient();

        if (mc.theWorld == null)
            return;

        Entity entity = mc.theWorld.getEntityByID(entityId);
        if (entity != null && entity instanceof EntityPlayer)
            InventoryCosArmor.setCosArmorClient((EntityPlayer) entity, slot, isSkinCosArmor, itemCosArmor);
    }

    @Override
    public void handlePacketServer(EntityPlayerMP player)
    {
    }

    @Override
    public void readFromBuffer(ByteBuf buf)
    {
        PacketBuffer pb = new PacketBuffer(buf);

        entityId = pb.readInt();
        slot = pb.readByte();
        isSkinCosArmor = pb.readBoolean();
        try
        {
            itemCosArmor = pb.readItemStackFromBuffer();
        }
        catch (IOException ignored)
        {
        }
    }

    @Override
    public void writeToBuffer(ByteBuf buf)
    {
        PacketBuffer pb = new PacketBuffer(buf);

        pb.writeInt(entityId);
        pb.writeByte(slot);
        pb.writeBoolean(isSkinCosArmor);
        try
        {
            pb.writeItemStackToBuffer(itemCosArmor);
        }
        catch (IOException ignored)
        {
        }
    }

}