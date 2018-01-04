package com.rayleigh.core.async;

import java.util.concurrent.Future;

public interface AsyncService<T> {
    Future<T> doAsync();
}
