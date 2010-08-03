/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.codegen.model;

import java.util.List;
import java.util.Set;

/**
 * @author tiwe
 *
 * @param <T>
 */
public interface Type {
    
    Type as(TypeCategory category);
    
    Type asArrayType();
    
    TypeCategory getCategory();
    
    String getFullName();
    
    String getGenericName(boolean asArgType);

    String getGenericName(boolean asArgType, Set<String> packages, Set<String> classes);
    
    String getPackageName();

    List<Type> getParameters();
    
    String getPrimitiveName();
    
    String getRawName(Set<String> packages, Set<String> classes);
    
    String getSimpleName();

    boolean isFinal();
    
    boolean isPrimitive();
    
}