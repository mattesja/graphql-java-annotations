/**
 * Copyright 2016 Yurii Rashkovskii
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */
package graphql.annotations;

import graphql.schema.*;
import graphql.schema.GraphQLType;

import java.util.Map;

/**
 * Type resolver, which uses the registry to resolve types depending on the name.
 */
public class DefaultTypeResolver implements TypeResolver {

    public final static String DEFAULT_TYPE = "default";

    private Map<String, GraphQLType> typeRegistry;

    public void init( Map<String, graphql.schema.GraphQLType> typeRegistry){
        this.typeRegistry = typeRegistry;
    }

    @Override
    public GraphQLObjectType getType(Object object) {
        graphql.schema.GraphQLType graphQLType = typeRegistry.get(GraphQLAnnotations.getTypeName(object.getClass()));
        if (graphQLType instanceof GraphQLObjectType) {
            return (GraphQLObjectType) graphQLType;
        } else {
            GraphQLType defaultType = typeRegistry.get(DEFAULT_TYPE);
            if (defaultType instanceof GraphQLObjectType) {
                return (GraphQLObjectType) defaultType;
            }
        }
        return null;
    }
}