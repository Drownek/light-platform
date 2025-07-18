package me.drownek.platform.core.component.manifest;

import eu.okaeri.commons.classpath.ClasspathResourceType;
import eu.okaeri.commons.classpath.ClasspathScanner;
import eu.okaeri.injector.Injectable;
import eu.okaeri.injector.Injector;
import eu.okaeri.injector.annotation.Inject;
import lombok.Data;
import lombok.NonNull;
import me.drownek.platform.core.LightPlatform;
import me.drownek.platform.core.annotation.*;
import me.drownek.platform.core.component.ExternalResourceProvider;
import me.drownek.platform.core.component.creator.ComponentCreator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Data
public class BeanManifest {

    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("okaeri.platform.debug", "false"));
    private static final int MAX_RESOLVE_FAIL = Integer.parseInt(System.getProperty("okaeri.platform.maxResolveFail", "100"));
    private static final Logger LOGGER = Logger.getLogger(BeanManifest.class.getName());

    public static BeanManifest of(@NonNull Parameter parameter) {
        return of(parameter, false);
    }

    public static BeanManifest of(@NonNull Parameter parameter, boolean readParamName) {

        Inject inject = parameter.getAnnotation(Inject.class);
        String name = (inject == null) ? (readParamName ? parameter.getName() : "") : inject.value();

        BeanManifest manifest = new BeanManifest();
        manifest.setType(parameter.getType());
        manifest.setName(name);
        manifest.setDepends(Collections.emptyList());
        manifest.setExternals(Collections.emptyList());
        manifest.setSource(BeanSource.INJECT);

        return manifest;
    }

    private static BeanManifest of(@NonNull Field field) {

        Inject inject = field.getAnnotation(Inject.class);
        String name = (inject == null) ? "" : inject.value();

        BeanManifest manifest = new BeanManifest();
        manifest.setName(name);
        manifest.setType(field.getType());
        manifest.setDepends(Collections.emptyList());
        manifest.setExternals(Collections.emptyList());
        manifest.setSource(BeanSource.INJECT);

        return manifest;
    }

    public static BeanManifest of(@NonNull ClassLoader classLoader, @NonNull Class<?> clazz, @NonNull ComponentCreator creator, boolean fullLoad) {

        if (!creator.isComponent(clazz)) {
            throw new IllegalArgumentException("Cannot create manifest of non-component " + clazz);
        }

        BeanManifest manifest = new BeanManifest();
        manifest.setType(clazz);
        manifest.setName(nameClass(clazz));
        manifest.setSource(BeanSource.COMPONENT);
        manifest.setFullLoad(fullLoad);

        List<External> externals = Arrays.asList(clazz.getAnnotationsByType(External.class));
        manifest.setExternals(externals);

        List<BeanManifest> depends = new ArrayList<>();
        manifest.setDepends(depends);

        depends.addAll(Arrays.stream(clazz.getAnnotationsByType(Register.class))
            .filter(Objects::nonNull)
            .map(reg -> BeanManifest.of(classLoader, reg, creator))
            .collect(Collectors.toList()));

        depends.addAll(Arrays.stream(clazz.getAnnotationsByType(Scan.class))
            .filter(Objects::nonNull)
            .flatMap(scan -> BeanManifest.of(classLoader, clazz, scan, creator).stream())
            .collect(Collectors.toList()));

        depends.addAll(Arrays.stream(clazz.getAnnotationsByType(DependsOn.class))
            .filter(Objects::nonNull)
            .map(dependency -> BeanManifest.ofRequirement(dependency.type(), dependency.name()))
            .collect(Collectors.toList()));

        depends.addAll(Arrays.stream(clazz.getDeclaredMethods())
            .filter(creator::isComponentMethod)
            .map(method -> BeanManifest.of(manifest, method, creator))
            .collect(Collectors.toList()));

        List<Constructor<?>> injectConstructors = Arrays.stream(clazz.getConstructors())
            .filter(constructor -> constructor.getAnnotation(Inject.class) != null)
            .collect(Collectors.toList());

        if (injectConstructors.size() > 1) {
            throw new IllegalArgumentException("More than one constructor is marked with @Inject in " + clazz);
        } else if (!injectConstructors.isEmpty()) {
            depends.addAll(Arrays.stream(injectConstructors.get(0).getParameters())
                .map(param -> BeanManifest.of(param, true))
                .collect(Collectors.toList()));
        }

        depends.addAll(Arrays.stream(clazz.getDeclaredFields())
            .filter(field -> field.getAnnotation(Inject.class) != null)
            .map(BeanManifest::of)
            .collect(Collectors.toList()));

        return manifest;
    }

    private static BeanManifest ofRequirement(@NonNull Class<?> type, @NonNull String name) {

        BeanManifest manifest = new BeanManifest();
        manifest.setName(name);
        manifest.setType(type);
        manifest.setDepends(Collections.emptyList());
        manifest.setExternals(Collections.emptyList());
        manifest.setSource(BeanSource.INJECT);

        return manifest;
    }

    public static BeanManifest of(@NonNull BeanManifest parent, @NonNull Method method, @NonNull ComponentCreator creator) {

        Bean annotation = method.getAnnotation(Bean.class);
        if ((annotation == null) && !creator.isComponentMethod(method)) {
            throw new IllegalArgumentException("Cannot create BeanManifest from method without @Bean: " + method);
        }

        BeanManifest manifest = new BeanManifest();
        manifest.setType(method.getReturnType());
        manifest.setName((annotation == null) ? "" : annotation.value());

        manifest.setDepends(Arrays.stream(method.getParameters())
            .filter(parameter -> !creator.getRegistry().isDynamicParameter(parameter))
            .map(BeanManifest::of)
            .collect(Collectors.toList()));
        manifest.setExternals(Collections.emptyList());

        manifest.setSource(BeanSource.METHOD);
        manifest.setMethod(method);
        manifest.setParent(parent);

        return manifest;
    }

    public static BeanManifest of(@NonNull ClassLoader classLoader, @NonNull Register register, @NonNull ComponentCreator creator) {
        return of(classLoader, register.value(), creator, false);
    }

    public static List<BeanManifest> of(@NonNull ClassLoader classLoader, @NonNull Class<?> parent, @NonNull Scan scan, @NonNull ComponentCreator creator) {

        List<String> exclusions = Arrays.asList(scan.exclusions());
        String value = scan.value();

        if (value.isEmpty()) {
            value = parent.getPackage().getName();
        }

        List<BeanManifest> results = ClasspathScanner.of(classLoader)
            .findResources(value, scan.deep())
            .filter(resource -> resource.getType() == ClasspathResourceType.CLASS)
            .filter(resource -> exclusions.stream().noneMatch(exclusion -> resource.getQualifiedName().startsWith(exclusion)))
            .map(resource -> {
                Class<?> clazz;
                try {
                    clazz = classLoader.loadClass(resource.getQualifiedName());
                } catch (Throwable throwable) {
                    throw new RuntimeException("Class failed to load (use 'exclusions = \"my.package.libs\"' for shaded dependencies?): " + resource, throwable);
                }
                if (creator.isComponent(clazz) && !LightPlatform.class.isAssignableFrom(clazz)) {
                    if (DEBUG) creator.debug("Scanned: " + clazz);
                    return of(classLoader, clazz, creator, false);
                }
                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        if (results.isEmpty()) {
            throw new IllegalArgumentException("Scan returned 0 results: " + scan);
        }

        return results;
    }

    private String name;
    private Object object;
    private Class<?> type;
    private BeanSource source;
    private BeanManifest parent;
    private Method method;
    private boolean fullLoad = false;
    private List<BeanManifest> depends;
    private List<External> externals;
    private Map<DependencyPair, Integer> failCounter = new HashMap<>();

    @Override
    public String toString() {
        return "BeanManifest(" +
            "name='" + this.name + '\'' +
            ", object=" + this.object +
            ", type=" + this.type +
            ", source=" + this.source +
            ", parent=" + (this.parent == null ? null : "[present]") +
            ", method=" + this.method +
            ", fullLoad=" + this.fullLoad +
            ", depends=" + (this.depends == null ? null : "[present (" + this.depends.size() + ")]") +
            ", externals=" + (this.externals == null ? null : "[present (" + this.externals.size() + ")]") +
            ", failCounter=" + this.failCounter +
            ')';
    }

    public BeanManifest withDepend(int pos, @NonNull BeanManifest beanManifest) {
        this.depends.add(pos, beanManifest);
        return this;
    }

    public BeanManifest withDepend(@NonNull BeanManifest beanManifest) {
        this.depends.add(beanManifest);
        return this;
    }

    public BeanManifest name(@NonNull String name) {
        this.name = name;
        return this;
    }

    public BeanManifest update(@NonNull ComponentCreator creator, @NonNull Injector injector) {

        if (this.object == null) {
            Optional<?> injectable = injector.getExact(this.name, this.type);
            if (injectable.isPresent()) {
                this.object = injectable.get();
            } else if (this.ready(creator, injector) && creator.isComponent(this.type) && (this.source != BeanSource.INJECT)) {
                this.object = creator.make(this, injector);
                injector.registerInjectable(this.name, this.object);
            }
        }

        this.invokeInjectDependencies(injector);
        this.invokeMethodDependencies(creator, injector);
        this.updateAllDependencies(creator, injector);
        return this;
    }

    private void updateAllDependencies(@NonNull ComponentCreator creator, @NonNull Injector injector) {
        for (BeanManifest depend : this.depends) {
            depend.update(creator, injector);
        }
    }

    private void invokeMethodDependencies(@NonNull ComponentCreator creator, @NonNull Injector injector) {
        for (BeanManifest depend : this.depends) {

            if ((this.object == null) || !depend.ready(creator, injector) || (depend.getSource() != BeanSource.METHOD) || (depend.getObject() != null)) {
                continue;
            }

            // create object using creator
            Object createdObject = creator.make(depend, injector);

            // a little hack to fool stO0opid BeanManifest - void bean can be executable method eg. @Scheduled
            if ((createdObject == null) && (depend.getMethod().getReturnType() == void.class)) {
                depend.setObject(void.class);
                continue;
            }

            // standard bean
            if (createdObject != null) {
                depend.setObject(createdObject);
                injector.registerInjectable(depend.getName(), depend.getObject());
                continue;
            }

            throw new RuntimeException("Cannot register null as bean [method: " + depend.getMethod() + "]");
        }
    }

    private void invokeInjectDependencies(@NonNull Injector injector) {
        for (BeanManifest depend : this.depends) {
            if ((depend.getSource() != BeanSource.INJECT) || (depend.getObject() != null)) {
                continue;
            }
            Optional<?> injectable = injector.getExact(depend.getName(), depend.getType());
            if (!injectable.isPresent()) {
                continue;
            }
            depend.setObject(injectable.get());
        }
    }

    private void injectExternals(@NonNull Injector injector, @NonNull ExternalResourceProvider resourceProvider) {

        for (External external : this.getExternals()) {
            if (injector.getExact(external.name(), external.type()).isPresent()) {
                continue;
            }
            Object value = resourceProvider.provide(external.name(), external.type(), external.of());
            injector.registerInjectable(external.name(), value);
        }

        for (BeanManifest depend : this.getDepends()) {
            if (depend.getSource() == BeanSource.INJECT) {
                continue;
            }
            depend.injectExternals(injector, resourceProvider);
        }
    }

    public boolean ready(@NonNull ComponentCreator creator, @NonNull Injector injector) {
        return this.ready(creator, injector, true);
    }

    public boolean ready(@NonNull ComponentCreator creator, @NonNull Injector injector, boolean registerFail) {

        for (BeanManifest depend : this.depends) {

            if ((depend.getObject() == null) && (depend.getSource() == BeanSource.INJECT)) {

                Optional<?> injectable = injector.getExact(depend.getName(), depend.getType());

                if (!injectable.isPresent()) {

                    if (!registerFail) {
                        return false;
                    }

                    Class<?> dependClass = depend.getType();
                    DependencyPair dependencyPair = new DependencyPair(depend.getName(), dependClass);
                    int newValue = this.failCounter.getOrDefault(dependencyPair, 0) + 1;
                    this.failCounter.put(dependencyPair, newValue);

                    if (newValue > MAX_RESOLVE_FAIL) {
                        this.failCounter.entrySet().stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .filter(entry -> entry.getValue() > 10)
                            .forEach((entry) -> LOGGER.severe(entry.getKey() + " - " + entry.getValue() + " fails"));

                        String dependDependencies = "- (unknown)";
                        if (depend.getSource() == BeanSource.INJECT) {

                            BeanManifest manifest = BeanManifest.of(this.getClass().getClassLoader(), depend.getType(), creator, false);
                            manifest.invokeInjectDependencies(injector);

                            if (manifest.getDepends().isEmpty()) {
                                dependDependencies = "- (none)";
                            } else {
                                dependDependencies = manifest.getDepends().stream()
                                    .map(d -> {
                                        if (d.getSource() == BeanSource.INJECT && d.getObject() == null) {
                                            List<String> similarNames = injector.allOf(d.getType()).stream()
                                                .map(Injectable::getName)
                                                .collect(Collectors.toList());
                                            Collections.reverse(similarNames);
                                            return "- '" + d.getName() + "' -> " + d.getType() + " (" + "empty, similar by type: [" + String.join(", ", similarNames) + "])";
                                        }
                                        return "- '" + d.getName() + "' -> " + d.getType() + " (" + (d.getObject() == null ? "empty" : "filled") + ")";
                                    })
                                    .collect(Collectors.joining("\n"));
                            }
                        }

                        String injectorContents = injector.stream()
                            .map(i -> "- '" + i.getName() + "' -> " + i.getType())
                            .collect(Collectors.joining("\n"));

                        throw new RuntimeException("Failed to resolve component/bean " + dependClass + " (" + depend.getName() + "=" + depend.getSource() + ") in " + this.getType() + "!" +
                            "\n" + dependClass.getSimpleName() + " dependencies (as component): \n" + dependDependencies +
                            "\nInjector contents: \n" + injectorContents);
                    }

                    return false;
                }

                Object injectObject = injectable.get();
                depend.setObject(injectObject);
            }
        }

        return true;
    }

    private boolean fullLoadReady(@NonNull Injector injector) {
        return this.depends.stream().noneMatch(depend -> depend.getObject() == null);
    }

    public BeanManifest execute(@NonNull ComponentCreator creator, @NonNull Injector injector, @NonNull ExternalResourceProvider resourceProvider) {

        this.injectExternals(injector, resourceProvider);

        while (!this.ready(creator, injector) || (this.fullLoad && !this.fullLoadReady(injector))) {
            this.update(creator, injector);
        }

        return this;
    }

    public static String nameClass(@NonNull Class<?> clazz) {
        String text = clazz.getSimpleName();
        char[] chars = text.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
}
