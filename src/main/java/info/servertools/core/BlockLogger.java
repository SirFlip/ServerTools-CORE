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

import info.servertools.core.lib.Reference;
import info.servertools.core.util.SaveThread;

import com.google.common.io.Files;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import info.servertools.core.util.ServerUtils;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BlockLogger {

    private static final String FILE_HEADER = "TimeStamp,UUID,PlayerName,DimID,BlockX,BlockY,BlockZ,BlockName,LocBlockName";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("kk-mm-ss");

    private final File breakDirectory;
    private final File placeDirectory;
    private final File interactDirectory;

    private final boolean logBlockPlaces;
    private final boolean logBlockBreaks;
    private final boolean logBlockInteract;


    public BlockLogger(File breakDirectory, File placeDirectory, File interactDirectory, boolean logBlockBreaks, boolean logBlockPlaces, boolean logBlockInteract) {
        this.logBlockBreaks = logBlockBreaks;
        this.logBlockPlaces = logBlockPlaces;
        this.logBlockInteract = logBlockInteract;
        this.breakDirectory = breakDirectory;
        this.placeDirectory = placeDirectory;
        this.interactDirectory = interactDirectory;
        if (logBlockBreaks) {
            if (breakDirectory.exists() && !breakDirectory.isDirectory()) {
                throw new IllegalArgumentException("File with same name as block break logging directory detected");
            }
            breakDirectory.mkdirs();
        }
        if (logBlockPlaces) {
            if (placeDirectory.exists() && !placeDirectory.isDirectory()) {
                throw new IllegalArgumentException("File with same name as block place logging directory detected");
            }
            placeDirectory.mkdirs();
        }
        if (logBlockInteract) {
            if (interactDirectory.exists() && !interactDirectory.isDirectory()) {
                throw new IllegalArgumentException("File with same name as block interact logging directory detected");
            }
            interactDirectory.mkdirs();
        }
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!logBlockBreaks) { return; }
        if(CoreConfig.LOG_EXCLUDE_OPS && ServerUtils.isOP(event.getPlayer().getGameProfile())) {return;}
        final File logFile = new File(breakDirectory, DATE_FORMAT.format(Calendar.getInstance().getTime()) + ".csv");
        new SaveThread(String.format(
                "%s,%s,%s,%s,%s,%s,%s,%s,%s",
                TIME_FORMAT.format(Calendar.getInstance().getTime()), //When was it placed
                event.getPlayer().getPersistentID(), // Who placed it (UUID)
                event.getPlayer().getDisplayName(),
                event.world.provider.dimensionId, // What dimension was it in
                event.x, // XCoord
                event.y, // YCoord
                event.z, // ZCoord
                event.block.getUnlocalizedName(),// What block was it
                event.block.getLocalizedName()

        ) + Reference.LINE_SEPARATOR) {
            @Override
            public void run() {
                synchronized (breakDirectory) {
                    try {
                        if (!logFile.exists()) {
                            writeHeader(logFile);
                        }
                        Files.append(data, logFile, Reference.CHARSET);
                    } catch (IOException e) {
                        super.log.warn("Failed to save block break file to disk", e);
                    }
                }
            }
        }.start();
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        if (!logBlockPlaces) { return; }
        if(CoreConfig.LOG_EXCLUDE_OPS && ServerUtils.isOP(event.player.getGameProfile())) {return;}
        final File logFile = new File(placeDirectory, DATE_FORMAT.format(Calendar.getInstance().getTime()) + ".csv");
        new SaveThread(String.format(
                "%s,%s,%s,%s,%s,%s,%s,%s,%s",
                TIME_FORMAT.format(Calendar.getInstance().getTime()), //When was it placed
                event.player.getPersistentID(), // Who placed it (UUID)
                event.player.getDisplayName(),
                event.world.provider.dimensionId, // What dimension was it in
                event.x, // XCoord
                event.y, // YCoord
                event.z, // ZCoord
                event.block.getUnlocalizedName(),// What block was it
                event.block.getLocalizedName()
        ) + Reference.LINE_SEPARATOR) {
            @Override
            public void run() {
                synchronized (placeDirectory) {
                    try {
                        if (!logFile.exists()) {
                            writeHeader(logFile);
                        }
                        Files.append(data, logFile, Reference.CHARSET);
                    } catch (IOException e) {
                        super.log.warn("Failed to save block place file to disk", e);
                    }
                }
            }
        }.start();
    }


   //File interactDirectory;
    //boolean logBlockInteract;
    @SubscribeEvent
    public void onBlockInteract(PlayerInteractEvent event) {
        if (!logBlockInteract) { return; }
        if (event.action!=PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) { return; }
        if(CoreConfig.LOG_EXCLUDE_OPS && ServerUtils.isOP(event.entityPlayer.getGameProfile())) {return;}
        final File logFile = new File(interactDirectory, DATE_FORMAT.format(Calendar.getInstance().getTime()) + ".csv");
        new SaveThread(String.format(
                "%s,%s,%s,%s,%s,%s,%s,%s,%s",
                TIME_FORMAT.format(Calendar.getInstance().getTime()), //When was it placed
                event.entityPlayer.getPersistentID(), // Who placed it (UUID)
                event.entityPlayer.getDisplayName(),
                event.world.provider.dimensionId, // What dimension was it in
                event.x, // XCoord
                event.y, // YCoord
                event.z, // ZCoord
                event.world.getBlock(event.x,event.y,event.z).getUnlocalizedName(),// What block was it
                event.world.getBlock(event.x,event.y,event.z).getLocalizedName()
        ) + Reference.LINE_SEPARATOR) {
            @Override
            public void run() {
                synchronized (interactDirectory) {
                    try {
                        if (!logFile.exists()) {
                            writeHeader(logFile);
                        }
                        Files.append(data, logFile, Reference.CHARSET);
                    } catch (IOException e) {
                        super.log.warn("Failed to save block interact file to disk", e);
                    }
                }
            }
        }.start();
    }


    private static void writeHeader(File file) throws IOException {
        Files.append(FILE_HEADER + Reference.LINE_SEPARATOR, file, Reference.CHARSET);
    }
}
