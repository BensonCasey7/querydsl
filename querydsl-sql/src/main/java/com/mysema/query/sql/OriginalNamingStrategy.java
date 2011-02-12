/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query.sql;

import java.lang.annotation.Annotation;

import org.apache.commons.lang.StringUtils;

import com.mysema.query.codegen.EntityType;
import com.mysema.util.JavaSyntaxUtils;

/**
 * OriginalNamingStrategy preserves the table and column names in the conversion
 *
 * @author tiwe
 *
 */
public class OriginalNamingStrategy implements NamingStrategy {
    
    private String foreignKeysClassName = "ForeignKeys";
    
    private String foreignKeysVariable = "fk";
    
    private String primaryKeysClassName = "PrimaryKeys";
    
    private String primaryKeysVariable = "pk";
    
    private String reservedSuffix = "_col";
    
    @Override
    public String getClassName(String namePrefix, String nameSuffix, String tableName) {
        return namePrefix + tableName + nameSuffix;
    }

    @Override
    public String getDefaultAlias(String namePrefix, String nameSuffix, EntityType entityType) {
        for (Annotation ann : entityType.getAnnotations()) {
            if (ann.annotationType().equals(Table.class)) {
                return ((Table) ann).value();
            }
        }
        return getDefaultVariableName(namePrefix, nameSuffix, entityType);
    }

    @Override
    public String getDefaultVariableName(String namePrefix, String nameSuffix, EntityType entityType) {
        return StringUtils.uncapitalize(entityType.getSimpleName());
    }

    @Override
    public String getForeignKeysClassName() {
        return foreignKeysClassName;
    }

    @Override
    public String getForeignKeysVariable(EntityType entityType) {
        return foreignKeysVariable;
    }

    @Override
    public String getPrimaryKeysClassName() {
        return primaryKeysClassName;
    }

    @Override
    public String getPrimaryKeysVariable(EntityType entityType) {
        return primaryKeysVariable;
    }

    @Override
    public String getPropertyName(String columnName, String namePrefix, String nameSuffix, EntityType entityType) {        
        return getPropertyName(columnName);
    }

    @Override
    public String getPropertyNameForForeignKey(String foreignKeyName, EntityType entityType) {
        return getPropertyName(foreignKeyName);
    }

    @Override
    public String getPropertyNameForInverseForeignKey(String foreignKeyName, EntityType entityType) {
        return "_" + foreignKeyName;
    }

    @Override
    public String getPropertyNameForPrimaryKey(String primaryKeyName, EntityType model) {
        return getPropertyName(primaryKeyName);
    }

    public String getReservedSuffix() {
        return reservedSuffix;
    }

    @Override
    public String normalizeColumnName(String columnName) {
        return columnName;
    }

    @Override
    public String normalizeTableName(String tableName) {
        return tableName;
    }
    
    private String getPropertyName(String name){
        return JavaSyntaxUtils.isReserved(name) ? name + reservedSuffix : name;
    }

    public void setForeignKeysClassName(String foreignKeysClassName) {
        this.foreignKeysClassName = foreignKeysClassName;
    }

    public void setForeignKeysVariable(String foreignKeysVariable) {
        this.foreignKeysVariable = foreignKeysVariable;
    }

    public void setPrimaryKeysClassName(String primaryKeysClassName) {
        this.primaryKeysClassName = primaryKeysClassName;
    }

    public void setPrimaryKeysVariable(String primaryKeysVariable) {
        this.primaryKeysVariable = primaryKeysVariable;
    }

    public void setReservedSuffix(String reservedSuffix) {
        this.reservedSuffix = reservedSuffix;
    }

    
}
