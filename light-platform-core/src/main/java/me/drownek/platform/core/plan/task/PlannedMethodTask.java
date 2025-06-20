package me.drownek.platform.core.plan.task;

import eu.okaeri.injector.Injector;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.plan.ExecutionTask;

import java.lang.reflect.Method;

@RequiredArgsConstructor
public class PlannedMethodTask implements ExecutionTask<LightPlatform> {

    private final Object parent;
    private final Method method;

    @SneakyThrows
    public PlannedMethodTask(Object parent, String methodName, Class<?> methodParameters) {
        this.parent = parent;
        this.method = parent.getClass().getMethod(methodName, methodParameters);
    }

    @Override
    @SneakyThrows
    public void execute(LightPlatform platform) {

        // simple method, just call
        if (this.method.getParameters().length == 0) {
            this.method.invoke(this.parent);
            return;
        }

        // method with parameters, use injector
        Injector injector = platform.getInjector();
        if (injector == null) {
            throw new RuntimeException("Cannot execute methods with parameters without injector");
        }

        injector.invoke(this.parent, this.method);
    }
}
