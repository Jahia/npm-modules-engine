import {buildUrl, useQuery} from '@jahia/js-server-core';
import {print} from 'graphql';
import gql from 'graphql-tag';

/**
 * @typedef {Object} NavigationItem
 * @property {string} displayName
 * @property {string} url
 * @property {NavigationItem[]} children
 */
export const buildNode = (origNode, session, renderContext, currentResource) => {
    let node = session.getNode(origNode.getPath());

    /** @type {NavigationItem} */
    let navigationItem = {
        displayName: node.getI18N(currentResource.getLocale()).getProperty('jcr:title').getString(),
        url: buildUrl({path: node.getPath()}, renderContext, currentResource),
        children: node.getNodes().getSize() ?
            Array.from(node.getNodes())
                .filter(child => child.isNodeType('jnt:page'))
                .map(child => buildNode(child, session, renderContext, currentResource)) :
            null
    };
    return navigationItem;
};

const getPageAncestors = (workspace, path, types) => {
    const result = useQuery({
        query: print(gql`
            query ($workspace: Workspace!, $path: String!, $types: [String]!){
                jcr(workspace: $workspace) {
                    nodeByPath(path: $path) {
                        ancestors(fieldFilter: {filters:[{fieldName:"isNodeType", value:"true"}]}) {
                            path
                            isNodeType(type: {types: $types})
                        }
                    }
                }
            }
        `),
        variables: {
            workspace,
            path,
            types
        }
    });
    // Currently no error handling is done, it will be implemented once handled by the framework
    return result.data ? result.data.jcr.nodeByPath.ancestors : [];
};

/**
 * Get the base node for the navigation menu based on the various parameters
 * @param {string} baseline the baseline to use to get the base node. If not specified or if 'home', the site's home page will be used, if 'currentPage', the current page will be used
 * @param {import('org.jahia.services.render').RenderContext} renderContext the current rendering context
 * @param {string} workspace the workspace to use: 'default' for the edit workspace, 'live' for the live workspace
 * @returns {import('org.jahia.services.content').JCRNodeWrapper} the baseline node to use for the navigation menu
 */
export const getBaseNode = (baseline, renderContext, workspace) => {
    const mainResourceNode = renderContext.getMainResource().getNode();
    const pageAncestors = getPageAncestors(workspace, mainResourceNode.getPath(), ['jnt:page']);
    if (!baseline || baseline === 'home') {
        return renderContext.getSite().getHome();
    }

    if (baseline === 'currentPage') {
        if (renderContext.getMainResource().getNode().isNodeType('jnt:page')) {
            return mainResourceNode;
        }

        if (pageAncestors.length > 0) {
            return mainResourceNode.getSession().getNode(pageAncestors.slice(-1)[0].path);
        }
    }

    return mainResourceNode;
};
