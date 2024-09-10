package com.exist.ecc.util;


@FunctionalInterface
public interface Loadable {
    <T> boolean load(T loadable, String fileName);
}
