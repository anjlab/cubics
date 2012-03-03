package com.anjlab.cubics;

public interface Calculator<T> {

    public Object calculate(FactValueProvider<T> valueProvider, T instance);
    
}
