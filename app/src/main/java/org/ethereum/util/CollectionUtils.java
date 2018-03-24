package org.ethereum.util;

import java.util.*;

/**
 * @author Mikhail Kalinin
 * @since 14.07.2015
 */
public class CollectionUtils {

    public static <K, V> List<V> collectList(Collection<K> items, Functional.Function<K, V> collector) {
        List<V> collected = new ArrayList<V>(items.size());
        for(K item : items) {
            collected.add(collector.apply(item));
        }
        return collected;
    }

    public static <K, V> Set<V> collectSet(Collection<K> items, Functional.Function<K, V> collector) {
        Set<V> collected = new HashSet<V>();
        for(K item : items) {
            collected.add(collector.apply(item));
        }
        return collected;
    }

    public static <T> List<T> truncate(List<T> items, int limit) {
        if(limit > items.size()) {
            return new ArrayList<T>(items);
        }
        List<T> truncated = new ArrayList<T>(limit);
        for(T item : items) {
            truncated.add(item);
            if(truncated.size() == limit) {
                break;
            }
        }
        return truncated;
    }

    public static <T> List<T> selectList(Collection<T> items, Functional.Predicate<T> predicate) {
        List<T> selected = new ArrayList<T>();
        for(T item : items) {
            if(predicate.test(item)) {
                selected.add(item);
            }
        }
        return selected;
    }

    public static <T> Set<T> selectSet(Collection<T> items, Functional.Predicate<T> predicate) {
        Set<T> selected = new HashSet<T>();
        for(T item : items) {
            if(predicate.test(item)) {
                selected.add(item);
            }
        }
        return selected;
    }
}
