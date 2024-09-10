package com.exist.ecc.util;


@FunctionalInterface
public interface Saveable {
    <T> void save(T saveable, String fileName);
}
