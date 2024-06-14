package com.akjostudios.acsp.common.stream;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

@SuppressWarnings("unused")
public class PagingCollector<T> implements Collector<T, Map<Integer, List<T>>, Map<Integer, List<T>>> {
    private final int pageSize;

    public PagingCollector(int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be greater than 0!");
        }
        this.pageSize = pageSize;
    }

    @Override
    public Supplier<Map<Integer, List<T>>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<Integer, List<T>>, T> accumulator() {
        return (map, item) -> {
            int pageCount = !map.isEmpty() ? map.size() - 1 : 0;
            List<T> currentPage = map.computeIfAbsent(pageCount, k -> new ArrayList<>());
            currentPage.add(item);
            if (currentPage.size() == pageSize) {
                map.put(pageCount + 1, new ArrayList<>());
            }
        };
    }

    @Override
    public BinaryOperator<Map<Integer, List<T>>> combiner() {
        return (map1, map2) -> {
            int offset = map1.size();
            map2.forEach((key, value) -> map1.merge(offset + key, value, (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            }));
            return map1;
        };
    }

    @Override
    public Function<Map<Integer, List<T>>, Map<Integer, List<T>>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of();
    }
}