import {getMenuItemsChildren, getPageAncestors} from './navMenu.utils';
import {render} from '@jahia/server-helpers';
const getBaseNode = (baseline, renderContext, workspace) => {
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

const buildMenu = (node, navMenuLevel, config) => {
    let result = [];
    if (node) {
        const session = node.getSession();
        const children = getMenuItemsChildren(config.workspace, node.getPath(), ['jmix:navMenuItem']);

        for (let index = 0; index < children.length; index++) {
            const menuEntry = {};
            const child = children[index];
            const itemPath = child.path;
            const menuItem = session.getNode(itemPath);
            const inpath = config.mainResourceNode.getPath() === itemPath || config.mainResourceNode.getPath().startsWith(itemPath + '/');
            let selected = false;
            let referenceIsBroken = false;
            let correctType = true;

            // Handle selection
            if (menuItem.isNodeType('jmix:nodeReference')) {
                config.currentResource.getDependencies().add(menuItem.getPropertyAsString('j:node'));
                const refNode = menuItem.getProperty('j:node').getNode();
                if (refNode) {
                    selected = config.mainResourceNode.getPath() === refNode.getPath();
                } else {
                    selected = false;
                    referenceIsBroken = true;
                }
            } else {
                selected = config.mainResourceNode.getPath() === menuItem.getPath();
            }

            // This should be filled by a simple custom choicelist initializer in this module that provides the list of declared menus
            // Not done yet as in the current state it is not possible to register choicelist initializers.
            // Check if menu item is explicitly not display in menu
            if (menuItem.hasProperty['j:displayInMenuName']) {
                correctType = false;
                menuItem.getProperty('j:displayInMenuName').getValues().each(displayMenuValue => {
                    correctType |= (displayMenuValue.getString() === config.menuName);
                });
            }

            if (!referenceIsBroken && correctType && (config.startLevelValue < navMenuLevel || inpath)) {
                const hasChildren = navMenuLevel < config.maxDepth && getMenuItemsChildren(config.workspace, menuItem.getPath()).length > 0;
                if (config.startLevelValue < navMenuLevel) {
                    config.currentResource.getDependencies().add(menuItem.getCanonicalPath());
                    menuEntry.render = render.render({
                        path: menuItem.getPath(),
                        view: config.menuEntryComponent || 'menuElement'
                    }, config.renderContext, config.currentResource);
                }

                if (hasChildren) {
                    menuEntry.children = buildMenu(menuItem, navMenuLevel + 1, config);
                }

                menuEntry.node = menuItem;
                menuEntry.inPath = inpath;
                menuEntry.selected = selected;
                menuEntry.level = navMenuLevel;
            }

            result.push(menuEntry);
        }
    }

    return result;
};

/**
 * Helper to build a navigation array (used to build nav menus)
 */
export default options => {
    const renderContext = options.data.root.renderContext;
    const currentResource = options.data.root.currentResource;
    const menuName = currentResource.getNode().getName();
    const mainResourceNode = renderContext.getMainResource().getNode();
    const workspace = renderContext.isLiveMode() ? 'LIVE' : 'EDIT';

    const maxDepth = parseInt(options.hash.maxDepth, 10);
    const baseNode = getBaseNode(options.hash.baseNode, renderContext, workspace);
    const menuEntryComponent = options.hash.menuEntryComponent;
    const startLevelValue = options.hash.startLevel ? parseInt(options.hash.startLevel, 10) : 0;

    return buildMenu(baseNode, 1, {
        renderContext,
        mainResourceNode,
        currentResource,
        workspace,
        menuName,
        startLevelValue,
        maxDepth,
        menuEntryComponent
    });
};
