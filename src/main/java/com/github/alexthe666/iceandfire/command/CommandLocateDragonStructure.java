package com.github.alexthe666.iceandfire.command;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.event.StructureSearch;
import com.github.alexthe666.iceandfire.world.LocatableStructure;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** Finds the nearest dragon roost or den and reports its coordinates without teleporting. */
public class CommandLocateDragonStructure extends CommandBase {
    private static final List<String> STRUCTURES = Arrays.asList(
            "fire_dragon_roost",
            "ice_dragon_roost",
            "lightning_dragon_roost",
            "fire_dragon_den",
            "ice_dragon_den",
            "lightning_dragon_den");

    @Override
    public String getName() {
        return "iflocate";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("dragonlocate", "iceandfirelocate");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/iflocate <fire_dragon_roost|ice_dragon_roost|lightning_dragon_roost|fire_dragon_den|ice_dragon_den|lightning_dragon_den>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerMP)) {
            sender.sendMessage(new TextComponentString("This command must be run by a player."));
            return;
        }
        if (!IceAndFire.CONFIG.enableStructureLocator) {
            sender.sendMessage(new TextComponentString("Enable Structure Locator in ice_and_fire.cfg and restart the server first."));
            return;
        }
        if (args.length != 1) {
            throw new CommandException(getUsage(sender));
        }

        String id = args[0].toLowerCase(Locale.ROOT);
        if (id.endsWith("_teleporter")) {
            id = id.substring(0, id.length() - "_teleporter".length());
        }
        if (id.equals("fire_roost")) id = "fire_dragon_roost";
        if (id.equals("ice_roost")) id = "ice_dragon_roost";
        if (id.equals("lightning_roost")) id = "lightning_dragon_roost";
        if (id.equals("fire_den")) id = "fire_dragon_den";
        if (id.equals("ice_den")) id = "ice_dragon_den";
        if (id.equals("lightning_den")) id = "lightning_dragon_den";

        LocatableStructure structure = findStructure(id);
        if (structure == null) {
            throw new CommandException(getUsage(sender));
        }
        StructureSearch.startLocate((EntityPlayerMP) sender, structure);
    }

    @Nullable
    private static LocatableStructure findStructure(String id) {
        for (LocatableStructure structure : LocatableStructure.values()) {
            if (structure.getId().equals(id)) {
                return structure;
            }
        }
        return null;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          @Nullable BlockPos targetPos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, STRUCTURES) : Collections.emptyList();
    }
}
