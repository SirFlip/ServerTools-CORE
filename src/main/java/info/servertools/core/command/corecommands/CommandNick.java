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

import static net.minecraft.util.EnumChatFormatting.AQUA;
import static net.minecraft.util.EnumChatFormatting.GREEN;
import static net.minecraft.util.EnumChatFormatting.RED;

import info.servertools.core.ServerTools;
import info.servertools.core.command.ServerToolsCommand;
import info.servertools.core.util.ChatMessage;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;

//TODO more limiting of nicknames
public class CommandNick extends ServerToolsCommand {

    public CommandNick(String defaultName) {
        super(defaultName);
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + name + " {nickname}";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {

        EntityPlayerMP player = requirePlayer(sender);

        if (args.length == 0) {
            ServerTools.instance.nickHandler.setNick(player, player.getGameProfile().getName());
            sender.addChatMessage(ChatMessage.builder().color(GREEN).add("Removed nickname").build());
        } else if (args.length == 1) {
            if (ServerTools.instance.nickHandler.setNick(player, args[0])) {
                player.addChatMessage(ChatMessage.builder().add("Set nickname to ").color(AQUA).add(args[0]).build());
            } else {
                player.addChatMessage(ChatMessage.builder().color(RED).add("The nickname ").color(AQUA).add(args[0]).color(RED).add(" isn't valid").build());
            }
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }
    }
}
