package graphql.annotations;

import graphql.schema.DataFetcher;

public class GraphQLAnnotationConfiguration {

    private Class<?> defaultGenericType;
    private Class<? extends DataFetcher> defaultDataFetcher;

    public GraphQLAnnotationConfiguration(Class<?> defaultGenericType, Class<? extends DataFetcher> defaultDataFetcher) {
        this.defaultGenericType = defaultGenericType;
        this.defaultDataFetcher = defaultDataFetcher;
    }

    public Class<?> getDefaultGenericType() {
        return defaultGenericType;
    }

    public Class<? extends DataFetcher> getDefaultDataFetcher() {
        return defaultDataFetcher;
    }
}
