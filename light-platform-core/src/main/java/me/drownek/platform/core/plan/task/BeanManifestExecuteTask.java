package me.drownek.platform.core.plan.task;

import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.component.ComponentHelper;
import me.drownek.platform.core.component.ExternalResourceProvider;
import me.drownek.platform.core.component.manifest.BeanManifest;
import me.drownek.platform.core.plan.ExecutionTask;

public class BeanManifestExecuteTask implements ExecutionTask<LightPlatform> {

    private static final ExternalResourceProvider NULL_RESOURCE_PROVIDER = (name, type, of) -> null;

    @Override
    public void execute(LightPlatform platform) {

        // find manifest
        BeanManifest beanManifest = platform.getInjector().get("manifest", BeanManifest.class)
            .orElseThrow(() -> new RuntimeException("Cannot execute manifest without manifest being present!"));

        // resolve resource provider
        ExternalResourceProvider resourceProvider = platform.getInjector().get("externalResourceProvider", ExternalResourceProvider.class)
            .orElse(NULL_RESOURCE_PROVIDER);

        // execute component tree and register everything
        beanManifest.execute(platform.getCreator(), platform.getInjector(), resourceProvider);

        // sub-components do not require manual injecting because
        // these are filled at the initialization by the DI itself
        // plugin instance however is not, so here it goes
        ComponentHelper.injectComponentFields(platform, platform.getInjector());

        // call PostConstruct
        ComponentHelper.invokePostConstruct(platform, platform.getInjector());
    }
}
