/*
 * This file is a part of ServerTools <http://servertools.info>
 *
 * Copyright (c) 2014 ServerTools
 * Copyright (c) 2014 contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.servertools.core.command.corecommands;

import static info.servertools.core.command.CommandLevel.OP;
import static net.minecraft.util.EnumChatFormatting.AQUA;

import info.servertools.core.ServerTools;
import info.servertools.core.command.ServerToolsCommand;
import info.servertools.core.util.ChatMessage;
import info.servertools.core.util.ServerUtils;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

import java.util.List;

import javax.annotation.Nullable;

public class CommandWhois extends ServerToolsCommand {

    public CommandWhois(String defaultName) {
        super(defaultName);
        setRequiredLevel(OP);
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + name + " [player]";
    }

    @Nullable
    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        }
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        final @Nullable EntityPlayerMP player = ServerUtils.getPlayerForUsername(args[0]);

        if (player != null) { // Found the player on the server
            sender.addChatMessage(ChatMessage.builder().add("Username: ").color(AQUA).add(player.getGameProfile().getName()).build());
            sender.addChatMessage(ChatMessage.builder().italic(true).add("  Is currently online").build());
            @Nullable final String nickName = ServerTools.instance.nickHandler.getNick(player.getPersistentID());
            if (nickName != null) {
                sender.addChatMessage(ChatMessage.builder().add("  Nickname: ").color(AQUA).add(nickName).build());
            }
            sender.addChatMessage(ChatMessage.builder().add("  UUID: ").color(AQUA).add(player.getGameProfile().getId().toString()).build());
            sender.addChatMessage(ChatMessage.builder().add("  IP: ").color(AQUA).add(player.getPlayerIP()).build());

            String opStatus = ServerUtils.isOP(player.getGameProfile()) ? "Yes" : "No";
            if (ServerUtils.isSinglePlayerOwner(player.getGameProfile().getName())) {
                opStatus = "Yes (Singleplayer owner)";
            }
            sender.addChatMessage(ChatMessage.builder().add("  Operator: ").color(AQUA).add(opStatus).build());
        } else {
            @Nullable final GameProfile profile = MinecraftServer.getServer().getPlayerProfileCache().getGameProfileForUsername(args[0]);
            if (profile != null) { // Found the profile from Mojang
                sender.addChatMessage(ChatMessage.builder().add("Username: ").color(AQUA).add(profile.getName()).build());
                sender.addChatMessage(ChatMessage.builder().italic(true).add("  Is not on this server").build());
                sender.addChatMessage(ChatMessage.builder().add("  UUID: ").color(AQUA).add(profile.getId().toString()).build());
                @Nullable final String nickName = ServerTools.instance.nickHandler.getNick(profile.getId());
                if (nickName != null) {
                    sender.addChatMessage(ChatMessage.builder().add("  Nickname: ").color(AQUA).add(nickName).build());
                }

                final String opStatus = ServerUtils.isOP(profile) ? "Yes" : "No";
                sender.addChatMessage(ChatMessage.builder().add("  Operator: ").color(AQUA).add(opStatus).build());
            } else { // Couldn't find the player/profile
                throw new PlayerNotFoundException("That player couldn't be identified");
            }
        }
    }
}
