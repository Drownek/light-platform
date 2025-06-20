package me.drownek.platform.core.plan.task;

import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.plan.ExecutionTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class PlatformBannerStartupTask implements ExecutionTask<LightPlatform> {

    @Override
    public void execute(LightPlatform platform) {

        InputStream bannerResource = Thread.currentThread().getContextClassLoader().getResourceAsStream("banner.txt");
        if (bannerResource == null) {
            return;
        }

        platform.log(new BufferedReader(new InputStreamReader(bannerResource))
            .lines()
            .collect(Collectors.joining("\n")));
    }
}
