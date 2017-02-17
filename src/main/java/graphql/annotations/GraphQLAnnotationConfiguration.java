package graphql.annotations;

import graphql.schema.DataFetcher;

public class GraphQLAnnotationConfiguration {

    private Class<?> defaultGenericType;
    private DataFetcher defaultDataFetcher;

    public GraphQLAnnotationConfiguration(Class<?> defaultGenericType, DataFetcher defaultDataFetcher) {
        this.defaultGenericType = defaultGenericType;
        this.defaultDataFetcher = defaultDataFetcher;
    }

    public Class<?> getDefaultGenericType() {
        return defaultGenericType;
    }

    public DataFetcher getDefaultDataFetcher() {
        return defaultDataFetcher;
    }
}
