package schulscheduler.javafx;

import griffon.javafx.beans.binding.CollectionBindings;
import griffon.javafx.collections.ElementObservableList;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.util.Callback;

import java.util.Arrays;
import java.util.Objects;

/**
 * Functions that should really be in {@link Bindings} in the JavaFX SDK.
 */
public class MoreBindings {
    /**
     * Creates a new {@link javafx.beans.binding.ObjectBinding} that returns the first
     * {@link javafx.beans.value.ObservableValue} which is not {@code null}, or {@code null} if there is none.
     *
     * @param observables the operands to check for {@code null}.
     * @return A binding returning the first operand which is not {@code null}, or {@code null} if there is none.
     * @link https://bugs.openjdk.java.net/browse/JDK-8092329
     */
    @SafeVarargs
    public static final <T> ObjectBinding<T> coalesce(final ObservableValue<? extends T>... observables) {
        return Bindings.createObjectBinding(() ->
                        Arrays.stream(observables)
                                .map(ObservableValue::getValue)
                                .filter(Objects::nonNull)
                                .findFirst().orElse(null),
                observables);
    }

    /**
     * Creates a new {@link javafx.beans.binding.StringBinding} that returns the first
     * {@link javafx.beans.value.ObservableValue} which is not {@code null}, or {@code null} if there is none.
     *
     * @param observables the operands to check for {@code null}.
     * @return A binding returning the first operand which is neither {@code null} nor an empty string, or {@code null}
     * if there is none.
     */
    @SafeVarargs
    public static final StringBinding coalesceString(final ObservableValue<String>... observables) {
        return Bindings.createStringBinding(() ->
                        Arrays.stream(observables)
                                .map(ObservableValue::getValue)
                                .filter(s -> s != null && !s.isBlank())
                                .findFirst().orElse(null),
                observables);
    }

    /**
     * A string joiner for observable values that updates both when the outer list changes (additions, deletions) and
     * also when any of the inner items changes.
     *
     * @param delimiter The delimiter to use between the joined strings.
     * @param items The items to be joined.
     * @param toString A lambda that extracts an observable string from one of the items.
     * @param <T> The outer type.
     * @return A binding for the joined string.
     */
    public static final <T> StringBinding join(final String delimiter, final ObservableList<T> items,
                                               final Callback<T, ObservableValue<String>> toString) {
        return CollectionBindings.joinList(
                // Ensures that the binding is updated also when the inner strings update.
                new ElementObservableList<>(items, item -> new ObservableValue[]{toString.call(item)}),
                delimiter,
                item -> toString.call(item).getValue()
        );
    }
}
