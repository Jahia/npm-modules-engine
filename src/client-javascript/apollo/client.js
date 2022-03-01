import {ApolloClient, InMemoryCache} from "@apollo/client";

console.log('creating client');

let apolloState = {}

/**
 * Simple object check.
 * @param item
 * @returns {boolean}
 */
export function isObject(item) {
    return (item && typeof item === 'object' && !Array.isArray(item));
}

/**
 * Deep merge two objects.
 * @param target
 * @param ...sources
 */
export function mergeDeep(target, source) {
    if (isObject(target) && isObject(source)) {
        for (const key in source) {
            if (isObject(source[key])) {
                if (!target[key]) {
                    Object.assign(target, { [key]: {} });
                }
                mergeDeep(target[key], source[key]);
            } else {
                Object.assign(target, { [key]: source[key] });
            }
        }
    }

    return target;
}

document.querySelectorAll('[data-apollostate]').forEach(data => {
    console.log('Found apollo data:', JSON.parse(data.dataset.apollostate));
    apolloState = mergeDeep(apolloState, JSON.parse(data.dataset.apollostate));
});


const client = new ApolloClient({
    uri: 'http://localhost:8080/modules/graphql',
    cache: new InMemoryCache().restore(apolloState),
});

export default client;
