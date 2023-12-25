package com.jbp.common.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by nijianfeng on 18/2/6.
 */
public class FunctionUtil {

    public static <V, K> Map<K, V> keyValueMap(Collection<V> collection, Function<V, K> keyFunc) {

        if (CollectionUtils.isEmpty(collection) || keyFunc == null) {
            return Maps.newHashMap();
        }
        return collection.stream().collect(Collectors.toMap(keyFunc, Function.identity(), (k1, k2) -> k1));
    }

    public static <V, K, U> Map<K, U> keyValueMap(Collection<V> collection, Function<V, K> keyFunc, Function<V, U> valueFunc) {

        if (CollectionUtils.isEmpty(collection) || keyFunc == null) {
            return Maps.newHashMap();
        }
        return collection.stream().collect(Collectors.toMap(keyFunc, valueFunc, (k1, k2) -> k1));
    }

    public static <T, K> Set<K> valueSet(Collection<T> collection, Predicate<T> predicate, Function<T, K> valueFunc) {

        if (CollectionUtils.isEmpty(collection) || predicate == null || valueFunc == null) {
            return Sets.newHashSet();
        }
        return collection.stream().filter(predicate).map(valueFunc).collect(Collectors.toSet());
    }

    public static <T, K> Set<K> valueSet(Collection<T> collection, Function<T, K> valueFunc) {

        if (CollectionUtils.isEmpty(collection) || valueFunc == null) {
            return Sets.newHashSet();
        }
        return collection.stream().map(valueFunc).collect(Collectors.toSet());
    }

    public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {

        if (CollectionUtils.isEmpty(collection) || predicate == null) {
            return Lists.newArrayList();
        }
        return collection.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate, Comparator<? super T> comparator) {

        if (CollectionUtils.isEmpty(collection) || predicate == null) {
            return Lists.newArrayList();
        }
        return collection.stream().filter(predicate).sorted(comparator).collect(Collectors.toList());
    }

    public static <T, K> List<K> valueList(Collection<T> collection, Predicate<T> predicate, Function<T, K> valueFunc) {

        if (CollectionUtils.isEmpty(collection) || predicate == null || valueFunc == null) {
            return Lists.newArrayList();
        }
        return collection.stream().filter(predicate).map(valueFunc).collect(Collectors.toList());
    }

    public static <T, K> List<K> valueList(Collection<T> collection, Function<T, K> valueFunc) {

        if (CollectionUtils.isEmpty(collection) || valueFunc == null) {
            return Lists.newArrayList();
        }
        return collection.stream().map(valueFunc).collect(Collectors.toList());
    }

    public static <T, K> Map<K, List<T>> valueMap(Collection<T> collection, Function<T, K> valueFunc) {

        if (CollectionUtils.isEmpty(collection) || valueFunc == null) {
            return Maps.newHashMap();
        }
        return collection.stream().collect(
                Collectors.groupingBy(valueFunc, Collectors.mapping(Function.identity(), Collectors.toList())));
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private FunctionUtil() {
    }

}

