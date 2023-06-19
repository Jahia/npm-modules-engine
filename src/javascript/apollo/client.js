import {ApolloClient, ApolloLink, InMemoryCache} from '@apollo/client';
import {gql} from '@jahia/server-helpers';
import * as Observable from 'zen-observable';
import {print} from 'graphql';

export const getSsrLink = renderContext => new ApolloLink(
    operation => {
        let {operationName, variables, query} = operation;
        let res = gql.executeQuerySync({
            query: print(query),
            operationName,
            variables: JSON.stringify(variables),
            renderContext: renderContext
        });
        return Observable.of(res);
    }
);

export const getClient = renderContext => new ApolloClient({
    ssrMode: true,
    link: getSsrLink(renderContext),
    cache: new InMemoryCache({
        typePolicies: {
            GqlNpmHelper: {
                merge: true
            }
        }
    })
});
