package pl.drownek.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@UtilityClass
public class CommandUtil {

    public static final String SUCCESS_MESSAGE = "&aSukces!";
    public static final Runnable EMPTY_RUNNABLE = () -> {
    };
    public static final String INVALID_ELEMENT = "&cNieprawidłowy element!";
    public static final String ELEMENT_MISSING = "&cNie ma takiego elementu na liście!";
    public static final String ELEMENT_ALREADY_EXIST = "&cElement już jest na liście!";

    /*
     * Remove actions
     */
    public <T> String removeIfPresent(@NonNull Collection<T> list, T object, @Nullable Runnable successAction) {
        if (object == null) {
            return INVALID_ELEMENT;
        }
        if (!list.contains(object)) {
            return ELEMENT_MISSING;
        }
        list.remove(object);
        if (successAction != null) {
            successAction.run();
        }
        return SUCCESS_MESSAGE;
    }

    public <T> String removeIfPresent(@NonNull Collection<T> list, T object) {
        return removeIfPresent(list, object, null);
    }

    public <T> String removeIfMatch(@NonNull Collection<T> list, @NonNull Predicate<T> predicate, @Nullable Runnable successAction) {
        Optional<T> any = list.stream().filter(predicate).findAny();
        if (any.isEmpty()) {
            return ELEMENT_MISSING;
        }
        list.remove(any.get());
        if (successAction != null) {
            successAction.run();
        }
        return SUCCESS_MESSAGE;
    }

    public <T> String removeIfMatch(@NonNull Collection<T> list, @NonNull Predicate<T> predicate) {
        return removeIfMatch(list, predicate, null);
    }

    public <T> String removeAtIndexAndConsume(@NonNull List<T> list, int index, Consumer<T> consumer) {
        if (index < 0 || index >= list.size()) {
            return ELEMENT_MISSING;
        }
        T element = list.remove(index);
        consumer.accept(element);
        return SUCCESS_MESSAGE;
    }

    public <T> String removeAtIndex(@NonNull List<T> list, int index, Runnable successAction) {
        if (index < 0 || index >= list.size()) {
            return ELEMENT_MISSING;
        }
        list.remove(index);
        successAction.run();
        return SUCCESS_MESSAGE;
    }

    /*
     *
     */

    public <T> String addIfNoneMatch(@NonNull Collection<T> list, T object, @NonNull Predicate<T> predicate, @Nullable Runnable runnable) {
        if (predicate.test(object)) {
            return ELEMENT_ALREADY_EXIST;
        }
        list.add(object);
        if (runnable != null) {
            runnable.run();
        }
        return SUCCESS_MESSAGE;
    }

    public <T> String addIfNoneMatch(@NonNull Collection<T> list, T object, @NonNull Predicate<T> predicate) {
        return addIfNoneMatch(list, object, predicate, null);
    }

    public <T> String addIfAbsent(@NonNull Collection<T> list, T object, Runnable runnable) {
        return addIfNoneMatch(list, object, list::contains, runnable);
    }

    public <T> String addIfAbsent(@NonNull Collection<T> list, T object) {
        return addIfAbsent(list, object, null);
    }

    /*
     * List
     */

    public <T> List<String> listElements(@NonNull Collection<T> list) {
        return listElements(list, "Lista:", t -> t.toString());
    }

    public <T> List<String> listElements(@NonNull Collection<T> list, Function<T, String> mapper) {
        return listElements(list, "Lista:", mapper);
    }

    public <T> List<String> listElements(@NonNull Collection<T> list, String header, Function<T, String> mapper) {
        return Stream.of(header)
            .flatMap(string -> Stream.concat(Stream.of(string), list.stream().map(mapper)))
            .collect(Collectors.toList());
    }

    public <T> List<String> listOrderElements(@NonNull Collection<T> list, Function<T, String> mapper) {
        return listOrderElements(list, "Lista:", mapper);
    }

    public <T> List<String> listOrderElements(@NonNull Collection<T> list) {
        return listOrderElements(list, "Lista:", t -> t.toString());
    }

    public <T> List<String> listOrderElements(@NonNull Collection<T> collection, String header, Function<T, String> mapper) {
        List<T> list = new ArrayList<>(collection);
        return Stream.concat(
            Stream.of(header),
            IntStream.range(0, list.size())
                .mapToObj(index -> index + ". " + mapper.apply(list.get(index)))
        ).collect(Collectors.toList());
    }

    public <T> Component listLocationElements(Collection<T> list, Function<T, Location> locationMapper) {
        return listLocationElements(list, "Lista:", Object::toString, locationMapper);
    }

    public <T> Component listLocationElements(Collection<T> list, Function<T, String> mapper, Function<T, Location> locationMapper) {
        return listLocationElements(list, "Lista:", mapper, locationMapper);
    }

    public <T> Component listLocationElements(@NonNull Collection<T> list, String header, Function<T, String> mapper, Function<T, Location> locationMapper) {
        Component component = TextUtil.component(header);
        for (T t : list) {
            String apply = mapper.apply(t);
            Location location = locationMapper.apply(t);
            Component message = MiniMessage.miniMessage().deserialize(apply + " " + location(location));
            component = component.appendNewline().append(message);
        }
        return component;
    }

    public <K, V> List<String> listMapElements(@NonNull Map<K, V> map, Function<Map.Entry<K, V>, String> mapper) {
        return listMapElements(map, "Lista:", mapper);
    }

    public <K, V> List<String> listMapElements(@NonNull Map<K, V> map, String header, Function<Map.Entry<K, V>, String> mapper) {
        return Stream.of(header)
            .flatMap(string -> Stream.concat(Stream.of(string), map.entrySet().stream().map(mapper)))
            .collect(Collectors.toList());
    }

    public <T> Component listOrderElementsComponent(@NonNull Collection<T> collection, Function<T, Component> mapper) {
        return Component.text("Lista:")
            .appendNewline()
            .append(
                Component.join(
                    JoinConfiguration.newlines(),
                    collection.stream().map(mapper).toList()
                )
            );
    }

    // --------------------------
    public <T> String consumeItemFromList(@NonNull List<T> list, int index, Consumer<T> consumer) {
        if (index < 0 || index >= list.size()) {
            return "Nie ma takiego elementu na liście!";
        }
        consumer.accept(list.get(index));
        return SUCCESS_MESSAGE;
    }
    // -----------------------------
    public String operation(List<Condition> failConditionsAndMessages, Runnable successAction, String successMessage) {
        for (Condition condition : failConditionsAndMessages) {
            if (condition.isCondition()) {
                return TextUtil.color(condition.getMessage());
            }
        }
        successAction.run();
        return TextUtil.color(successMessage);
    }

    public String operation(List<Condition> failConditionsAndMessages, String successMessage) {
        return operation(failConditionsAndMessages, EMPTY_RUNNABLE, successMessage);
    }

    public String operation(List<Condition> failConditionsAndMessages, Runnable successAction) {
        return operation(failConditionsAndMessages, successAction, SUCCESS_MESSAGE);
    }

    public String operation(Runnable successAction) {
        return operation(List.of(), successAction, SUCCESS_MESSAGE);
    }
    // -----------------------------
    public String location(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        return "<click:run_command:'/tp %s %s %s'><hover:show_text:Kliknij, aby tp>[tp]</hover></click>".formatted(x, y, z);
    }
}