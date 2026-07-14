package org.betterx.wover.ui.api;

import org.betterx.wover.config.api.client.ClientConfigs;
import org.betterx.wover.config.impl.CachedConfig;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverUi;

import net.fabricmc.loader.api.FabricLoader;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

public class VersionChecker implements Runnable {
    private static final boolean TEST_UPDATE_SCREEN = false && ModCore.isDevEnvironment();

    @FunctionalInterface
    public interface UpdateInfoProvider {
        void send(String modID, String currentVersion, String newVersion);
    }

    private static final List<String> KNOWN_MODS = new LinkedList<>();
    private static final List<ModVersion> NEW_VERSIONS = new LinkedList<>();

    public static class ModVersion {
        String n;
        String v;

        @Override
        public String toString() {
            return n + ":" + v;
        }
    }

    public static class Versions {
        String mc;
        String loader;
        List<ModVersion> mods;

        @Override
        public String toString() {
            return "Versions{" +
                    "mc='" + mc + '\'' +
                    ", loader='" + loader + '\'' +
                    ", mods=" + mods +
                    '}';
        }
    }

    public static final int WAIT_FOR_DAYS = 5;
    private static final String BASE_URL = "https://wunderreich.ambertation.de/api/v1/versions/";
    private static Thread versionChecker;

    public static void startCheck(boolean isClient) {
        if (versionChecker == null && isClient) {
            final VersionChecker checker = new VersionChecker();

            if (ClientConfigs.CLIENT.checkForNewVersions.get() && ClientConfigs.CLIENT.didPresentWelcomeScreen.get()) {
                if (TEST_UPDATE_SCREEN) {
                    Gson gson = new Gson();
                    String fakeVersions = "{\n" +
                            "    \"mc\":\"1.21-rc.1\",\n" +
                            "    \"loader\":\"fabric\",\n" +
                            "    \"mods\":[\n" +
                            "      {\"n\":\"bclib\", \"v\":\"21.0.0\"},\n" +
                            "      {\"n\":\"wover\", \"v\":\"21.0.0\"},\n" +
                            "      {\"n\":\"betterend\", \"v\":\"21.0.0\"},\n" +
                            "      {\"n\":\"betternether\", \"v\":\"21.0.0\"},\n" +
                            "      {\"n\":\"wunderreich\", \"v\":\"21.0.0\"}\n" +
                            "    ]\n" +
                            "  }";
                    Versions json = gson.fromJson(fakeVersions, Versions.class);
                    CachedConfig.INSTANCE.setLastVersionJson(fakeVersions);
                    CachedConfig.INSTANCE.save();
                    checker.processVersions(json);
                } else if (checker.needRecheck()) {
                    versionChecker = new Thread(checker);
                    versionChecker.start();
                } else {
                    String str = CachedConfig.INSTANCE.lastVersionJson();
                    if (str != null && str.trim().length() > 0) {
                        Gson gson = new Gson();
                        Versions json = gson.fromJson(str, Versions.class);
                        checker.processVersions(json);
                    }
                }
            }
        }
    }

    public static void registerMod(ModCore modCore) {
        KNOWN_MODS.add(modCore.namespace);
    }

    private static void registerMod(String modId) {
        KNOWN_MODS.add(modId);
    }

    boolean needRecheck() {
        Instant lastCheck = CachedConfig.INSTANCE.lastCheckDate().plus(WAIT_FOR_DAYS, ChronoUnit.DAYS);
        Instant now = Instant.now();


        return now.isAfter(lastCheck);
    }

    @Override
    public void run() {
        Gson gson = new Gson();

        ModCore modCore = ModCore.create("minecraft");
        String minecraftVersion = modCore.getModVersion().toString().replace(".", "_");
        LibWoverUi.C.LOG.info("Check Versions for minecraft=" + minecraftVersion);

        try {
            String fileName = "mc_fabric_" + URLEncoder.encode(
                    minecraftVersion,
                    StandardCharsets.ISO_8859_1.toString()
            ) + ".json";

            URL url = new URL(BASE_URL + fileName);
            try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
                Versions json = gson.fromJson(reader, Versions.class);
                String str = gson.getAdapter(Versions.class).toJson(json);
                CachedConfig.INSTANCE.setLastVersionJson(str);
                CachedConfig.INSTANCE.setLastCheckDate();
                CachedConfig.INSTANCE.save();

                processVersions(json);
            }
        } catch (UnsupportedEncodingException e) {
            LibWoverUi.C.LOG.error("Failed to encode URL during VersionCheck", e);
            return;
        } catch (MalformedURLException e) {
            LibWoverUi.C.LOG.error("Invalid URL during VersionCheck", e);
            return;
        } catch (IOException e) {
            LibWoverUi.C.LOG.error("I/O Error during VersionCheck", e);
            return;
        }

    }

    private void processVersions(Versions json) {
        if (json != null) {
            LibWoverUi.C.LOG.info("Received Version Info for minecraft=" + json.mc + ", loader=" + json.loader);
            if (json.mods != null) {
                for (ModVersion mod : json.mods) {
                    if (!KNOWN_MODS.contains(mod.n)) {
                        if (FabricLoader.getInstance().getModContainer(mod.n).isPresent())
                            registerMod(mod.n);
                    }
                    if (mod.n != null && mod.v != null && KNOWN_MODS.contains(mod.n)) {
                        final ModCore modCore = ModCore.create(mod.n);
                        var installedVersion = modCore.getModVersion();


                        boolean isNew = TEST_UPDATE_SCREEN || installedVersion.isLessThan(mod.v)
                                && !installedVersion.equals("0.0.0");
                        LibWoverUi.C.LOG.info(" - " + mod.n + ":" + mod.v + (isNew ? " (update available)" : ""));
                        if (isNew)
                            NEW_VERSIONS.add(mod);
                    }
                }
            }
        } else {
            LibWoverUi.C.LOG.warn("No valid Version Info");
        }
    }

    public static boolean isEmpty() {
        return NEW_VERSIONS.isEmpty();
    }

    public static void forEachUpdate(UpdateInfoProvider consumer) {
        for (ModVersion v : NEW_VERSIONS) {
            final ModCore modCore = ModCore.create(v.n);
            String currrent = modCore.getModVersion().toString();
            consumer.send(v.n, currrent, v.v);
        }
    }
}
