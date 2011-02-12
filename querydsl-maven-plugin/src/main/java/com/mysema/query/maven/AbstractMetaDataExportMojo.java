/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query.maven;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.mysema.query.codegen.BeanSerializer;
import com.mysema.query.codegen.Serializer;
import com.mysema.query.sql.DefaultNamingStrategy;
import com.mysema.query.sql.MetaDataExporter;
import com.mysema.query.sql.MetaDataSerializer;
import com.mysema.query.sql.NamingStrategy;

/**
 * MetaDataExportMojo is a goal for MetaDataExporter usage
 *
 * @author tiwe
 */
public class AbstractMetaDataExportMojo extends AbstractMojo{

    /**
     * @parameter expression="${project}" readonly=true required=true
     */
    private MavenProject project;

    /**
     * JDBC driver class name
     * @parameter required=true
     */
    private String jdbcDriver;

    /**
     * JDBC connection url
     * @parameter required=true
     */
    private String jdbcUrl;

    /**
     * JDBC connection username
     * @parameter
     */
    private String jdbcUser;

    /**
     * JDBC connection password
     * @parameter
     */
    private String jdbcPassword;

    /**
     * name prefix for query-types (default: "Q")
     * @parameter default-value="Q"
     */
    private String namePrefix;
    
    /**
     * name prefix for query-types (default: "")
     * @parameter default-value=""
     */
    private String nameSuffix;

    /**
     * package name for sources
     * @parameter required=true
     */
    private String packageName;

    /**
     * schemaPattern a schema name pattern; must match the schema name
     *        as it is stored in the database; "" retrieves those without a schema;
     *        <code>null</code> means that the schema name should not be used to narrow
     *        the search (default: null)
     *
     * @parameter
     */
    private String schemaPattern;

    /**
     * tableNamePattern a table name pattern; must match the
    *        table name as it is stored in the database (default: null)
     *
     * @parameter
     */
    private String tableNamePattern;

    /**
     * target source folder to create the sources into (e.g. target/generated-sources/java)
     *
     * @parameter required=true
     */
    private String targetFolder;

    /**
     * namingstrategy class to override (default: DefaultNamingStrategy.class)
     *
     * @parameter
     */
    private String namingStrategyClass;

    /**
     * serialize beans as well
     *
     * @parameter default-value=false
     */
    private boolean exportBeans;

    /**
     * wrap key properties into inner classes (default: false)
     *
     * @parameter default-value=false
     */
    private boolean innerClassesForKeys;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (isForTest()){
            project.addTestCompileSourceRoot(targetFolder);
        }else{
            project.addCompileSourceRoot(targetFolder);
        }
        NamingStrategy namingStrategy;
        if (namingStrategyClass != null){
            try {
                namingStrategy = (NamingStrategy) Class.forName(namingStrategyClass).newInstance();
            } catch (InstantiationException e) {
                throw new MojoExecutionException(e.getMessage(),e);
            } catch (IllegalAccessException e) {
                throw new MojoExecutionException(e.getMessage(),e);
            } catch (ClassNotFoundException e) {
                throw new MojoExecutionException(e.getMessage(),e);
            }
        }else{
            namingStrategy = new DefaultNamingStrategy();
        }
        Serializer serializer = new MetaDataSerializer(namePrefix, namingStrategy, innerClassesForKeys);

        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setNamePrefix(namePrefix);
        exporter.setNameSuffix(nameSuffix);
        exporter.setPackageName(packageName);
        exporter.setTargetFolder(new File(targetFolder));
        exporter.setNamingStrategy(namingStrategy);
        exporter.setSerializer(serializer);
        if (exportBeans){
            exporter.setBeanSerializer(new BeanSerializer());
        }
        exporter.setSchemaPattern(schemaPattern);
        exporter.setTableNamePattern(tableNamePattern);

        try {
            Class.forName(jdbcDriver);
            Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
            try{
                exporter.export(conn.getMetaData());
            }finally{
                if (conn != null){
                    conn.close();
                }
            }
        } catch (SQLException e) {
            getLog().error(e);
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            getLog().error(e);
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    protected boolean isForTest(){
        return false;
    }

}
