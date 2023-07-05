import {gql as gqlServer} from '@jahia/server-helpers';
import {gql} from '@apollo/client';
import {print} from 'graphql';

export const getPageAncestors = (workspace, path) => {
    const result = gqlServer.executeQuerySync({
        query: print(gql`
            query ($workspace: Workspace!, $path: String!){
                jcr(workspace: $workspace) {
                    nodeByPath(path: $path) {
                        ancestors(fieldFilter: {filters:[{fieldName:"isNodeType", value:"true"}]}) {
                            path
                            isNodeType(type: {types: ["jnt:page"]})
                        }
                    }
                }
            }
        `),
        variables: {
            workspace,
            path
        }
    });
    // Currently no error handling is done, it will be implemented once handled by the framework
    return result.data ? result.data.jcr.nodeByPath.ancestors : [];
};

export const getMenuItemsChildren = (workspace, path) => {
    const result = gqlServer.executeQuerySync({
        query: print(gql`
            query childrenOfType($workspace: Workspace!, $path: String!){
                jcr(workspace: $workspace) {
                    nodeByPath(path: $path) {
                        children(typesFilter: {types:["jmix:navMenuItem"]}) {
                            nodes {
                                path
                            }
                        }
                    }
                }
            }
        `),
        variables: {
            workspace,
            path
        }
    });
    // Currently no error handling is done, it will be implemented once handled by the framework
    return result.data ? result.data.jcr.nodeByPath.children.nodes : [];
};

