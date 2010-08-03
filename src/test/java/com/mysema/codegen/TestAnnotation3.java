/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.codegen;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TestAnnotation3 {
    
    ElementType type(); 

}
