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

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.*;

import static org.testng.AssertJUnit.assertEquals;


public class GraphQLAnnotationConfigurationTest {

    static Map<String, GraphQLObjectType> registry;

    GraphQLInterfaceType iface;
    GraphQLObjectType rootType;
    GraphQLObjectType objectType2;
    GraphQLSchema schema;
    GraphQLObjectType objectType;
    GraphQL graphQL;

    @Test
    public void testInterfaceInlineFragmentWildcardTypeVarNoClass() throws Exception {
        // Given
        registry = new HashMap<>();
        iface = (GraphQLInterfaceType) GraphQLAnnotations.iface(MyInterface.class);
        GraphQLAnnotations.getInstance().setConfiguration(new GraphQLAnnotationConfiguration(MyInterface.class, null));
        rootType = GraphQLAnnotations.object(RootObject.class);
        objectType2 = GraphQLAnnotations.object(MyObject2.class);
        registry.put("MyObject2", objectType2);

        objectType = GraphQLAnnotations.object(MyObject.class);
        registry.put("MyObject", objectType);

        schema = GraphQLSchema.newSchema()
                .query(rootType)
                .build(new HashSet(Arrays.asList(iface, rootType, objectType, objectType2)));

        graphQL = new GraphQL(schema);

        // When
        ExecutionResult graphQLResult = graphQL.execute("{itemsWildcardTypeVar { ... on MyObject {a, my {b}} ... on MyObject2 {a, b}  }}", new RootObject());

        // Then
        Map resultMap = ((Map) graphQLResult.getData());
        assertEquals(graphQLResult.getErrors().size(), 0);
        assertEquals(resultMap.size(), 1);
        assertEquals(((Map) ((List) resultMap.get("itemsWildcardTypeVar")).get(0)).get("a"), "a1");
    }


    @Test
    public void testInterfaceInlineFragmentDataFetcher() throws Exception {
        // Given
        registry = new HashMap<>();
        iface = (GraphQLInterfaceType) GraphQLAnnotations.iface(MyInterface.class);
        GraphQLAnnotations.getInstance().setConfiguration(new GraphQLAnnotationConfiguration(MyInterface.class, MyDatafetcher.class));
        rootType = GraphQLAnnotations.object(RootObject.class);
        objectType2 = GraphQLAnnotations.object(MyObject2.class);
        registry.put("MyObject2", objectType2);

        objectType = GraphQLAnnotations.object(MyObject.class);
        registry.put("MyObject", objectType);

        schema = GraphQLSchema.newSchema()
                .query(rootType)
                .build(new HashSet(Arrays.asList(iface, rootType, objectType, objectType2)));

        graphQL = new GraphQL(schema);

        // When
        ExecutionResult graphQLResult = graphQL.execute("{items { ... on MyObject {a, my {b}} ... on MyObject2 {a, b}  }}", new RootObject());

        // Then
        Map resultMap = ((Map) graphQLResult.getData());
        assertEquals(graphQLResult.getErrors().size(), 0);
        assertEquals(resultMap.size(), 1);
        assertEquals(((Map) ((List) resultMap.get("items")).get(0)).get("a"), "a1+x");
    }

    @Test
    public void testInterfaceInlineFragmentTypeVar() throws Exception {
        // Given
        registry = new HashMap<>();
        iface = (GraphQLInterfaceType) GraphQLAnnotations.iface(MyInterface.class);
        GraphQLAnnotations.getInstance().setConfiguration(new GraphQLAnnotationConfiguration(MyInterface.class, null));
        rootType = GraphQLAnnotations.object(RootObject.class);
        objectType2 = GraphQLAnnotations.object(MyObject2.class);
        registry.put("MyObject2", objectType2);

        objectType = GraphQLAnnotations.object(MyObject.class);
        registry.put("MyObject", objectType);

        schema = GraphQLSchema.newSchema()
                .query(rootType)
                .build(new HashSet(Arrays.asList(iface, rootType, objectType, objectType2)));

        graphQL = new GraphQL(schema);

        // When
        ExecutionResult graphQLResult = graphQL.execute("{itemsTypeVar { ... on MyObject {a, my {b}} ... on MyObject2 {a, b}  }}", new RootObject());

        // Then
        Set resultMap = ((Map) graphQLResult.getData()).entrySet();
        assertEquals(graphQLResult.getErrors().size(), 0);
        assertEquals(resultMap.size(), 1);
    }


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInterfaceInlineFragmentWildcardTypeVarNoClass_NoDefaultFromConfigurationError() throws Exception {
        // Given
        registry = new HashMap<>();
        iface = (GraphQLInterfaceType) GraphQLAnnotations.iface(MyInterface.class);
        GraphQLAnnotations.getInstance().setConfiguration(new GraphQLAnnotationConfiguration(null, null));
        rootType = GraphQLAnnotations.object(RootObject.class);
        objectType2 = GraphQLAnnotations.object(MyObject2.class);
        registry.put("MyObject2", objectType2);

        objectType = GraphQLAnnotations.object(MyObject.class);
        registry.put("MyObject", objectType);

        schema = GraphQLSchema.newSchema()
                .query(rootType)
                .build(new HashSet(Arrays.asList(iface, rootType, objectType, objectType2)));

        graphQL = new GraphQL(schema);
    }

    public static class RootObject<T> {
        @GraphQLField
        public List<MyInterface> getItems() {
            return Arrays.asList(new MyObject(), new MyObject2());
        }

        @GraphQLField
        public List<T> getItemsTypeVar() {
            return Arrays.asList((T)new MyObject(), (T)new MyObject2());
        }

        @GraphQLField
        public List<? extends T> getItemsWildcardTypeVar() {
            return Arrays.asList((T)new MyObject(), (T)new MyObject2());
        }
    }

    public static class MyObject implements MyInterface {
        public String getA() {
            return "a1";
        }

        public String getB() {
            return "b1";
        }

        @GraphQLField
        public MyObject2 getMy() {
            return new MyObject2();
        }
    }

    public static class MyObject2 extends SuperClass {
    }

    public static class SuperClass implements MyInterface {

        public String getA() {
            return "a2";
        }

        public String getB() {
            return "b2";
        }
    }

    @GraphQLTypeResolver
    public static interface MyInterface {
        @GraphQLField
        public String getA();

        @GraphQLField
        public String getB();
    }

    public static class MyDatafetcher extends MethodDataFetcher {

        public MyDatafetcher(Method method) {
            super(method);
        }

        @Override
        public Object get(DataFetchingEnvironment environment) {
            Object o = super.get(environment);
            if (o instanceof String) {
                return o + "+x";
            } else {
                return o;
            }

        }
    }

}