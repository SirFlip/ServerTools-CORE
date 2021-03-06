/*
 * Copyright 2014 ServerTools
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
package info.servertools.core;

import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

import java.io.File;

public class CoreConfig {

    public static boolean DEBUG_MODE;
    public static boolean SEND_MOTD_ON_LOGIN;
    public static boolean COLOR_OP_CHAT_MESSAGE;
    public static boolean GENERATE_FLAT_BEDROCK;
    public static boolean LOG_BLOCK_BREAKS;
    public static boolean LOG_BLOCK_PLACES;
    public static boolean LOG_BLOCK_INTERACT;
    public static int DEFAULT_REMOVE_ALL_RANGE;
    public static String OP_CHAT_PREFIX;
    public static String VOICE_CHAT_PREFIX;
    public static boolean LOG_EXCLUDE_OPS;

    public static boolean ENABLE_HELP_OVERRIDE;

    public static void init(File configFile) {

        Configuration configuration = new Configuration(configFile);

        try {
            configuration.load();

            String category = "general";

            configuration.renameProperty(category, "Color OP Messages", "Enable OP Prefix");

            DEBUG_MODE = configuration.get(category, "Debug Mode", false, "Spams the logs with debug info").getBoolean(false);
            SEND_MOTD_ON_LOGIN = configuration.get(category, "Send MOTD on Login", true, "Send the MOTD to players upon logging into the server").getBoolean(true);
            COLOR_OP_CHAT_MESSAGE = configuration.get(category, "Enable OP Prefix", true, "Gives OPs a prefix in chat").getBoolean(true);
            GENERATE_FLAT_BEDROCK = configuration.get(category, "Enable Flat Bedrock", true, "Causes bedrock to generate only one layer thick").getBoolean(true);
            DEFAULT_REMOVE_ALL_RANGE = configuration.get(category, "Default RemoveAll Range", 20, "The default range for the /removeall command").getInt();
            OP_CHAT_PREFIX = configuration.get(category, "OP Chat Prefix", "OP", "The prefix in chat for server operators").getString();
            VOICE_CHAT_PREFIX = configuration.get(category, "Voice Chat Prefix", "+", "The prefix in chat for voiced users").getString();
            ENABLE_HELP_OVERRIDE = configuration.get(category, "Enable Help Override", true, "Fixes /help when mods screw it up").getBoolean(true);

            category = "world";
            LOG_BLOCK_BREAKS = configuration.get(category, "Log Block Breaks", false, "This will log all blocks broken by players and fake players").getBoolean(false);
            LOG_BLOCK_PLACES = configuration.get(category, "Log Block Places", false, "This will log all blocks placed by players and fake players").getBoolean(false);
            LOG_BLOCK_INTERACT = configuration.get(category, "Log Block Interact", false, "This will log all blocks Interacted by players and fake players").getBoolean(false);
            LOG_EXCLUDE_OPS = configuration.get(category,"Log OPs exclude",true,"Will OPs be excluded").getBoolean(true);

        } catch (Exception e) {
            ServerTools.LOG.log(Level.FATAL, "Failed to load core configuration", e);
        } finally {
            if (configuration.hasChanged()) {
                configuration.save();
            }
        }
    }
}
