import {SafeString} from 'handlebars';
import {getMenuItemsChildren, getPageAncestors} from './navMenu.utils';
import {render} from '@jahia/server-helpers';
/* eslint-disable */
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

const printMenu = (node, navMenuLevel, config) => {
    let result = '';
    if (node) {
        const session = node.getSession();
        const children = getMenuItemsChildren(config.workspace, node.getPath(), ['jmix:navMenuItem']);
        const nbOfChilds = children.length;
        let closeUl = false;
        let firstEntry = true;

        for (let index = 0; index < children.length; index++) {
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
                    const listItemCssClass = (hasChildren ? 'hasChildren' : 'noChildren') + (inpath ? ' inPath' : '') +
                        (selected ? ' selected' : '') + (index === 0 ? ' firstInLevel' : '') +
                        (index === nbOfChilds - 1 ? ' lastInLevel' : '');
                    const renderedMenuItem = render.renderModule({
                        path: menuItem.getPath(),
                        view: 'menuElement'
                    }, config.renderContext);

                    if (renderedMenuItem) {
                        if (firstEntry) {
                            result += ((navMenuLevel - config.startLevelValue) === 1 ? '<div class="navbar">' : '<div class="box-inner">');
                            result += `<ul class="navmenu level_${navMenuLevel - config.startLevelValue}">`;
                            closeUl = true;
                        }

                        result += `<li class="${listItemCssClass}">`;
                        result += renderedMenuItem;
                    }

                    if (hasChildren) {
                        result += printMenu(menuItem, navMenuLevel + 1, config);
                    }

                    if (renderedMenuItem) {
                        result += '</li>';
                        firstEntry = false;
                    }
                } else if (hasChildren) {
                    result += printMenu(menuItem, navMenuLevel + 1, config);
                }
            }

            if (closeUl && index === (nbOfChilds - 1)) {
                result += '</ul>';
                result += '</div>';
                closeUl = false;
            }
        }
    }

    return result;
};

export default options => {
    const renderContext = options.data.root.renderContext;
    const currentResource = options.data.root.currentResource;
    const menuName = currentResource.getNode().getName();
    const mainResourceNode = renderContext.getMainResource().getNode();
    const workspace = renderContext.isLiveMode() ? 'LIVE' : 'EDIT';

    const maxDepth = parseInt(options.hash.maxDepth, 10);
    const baseNode = getBaseNode(options.hash.baseNode, renderContext, workspace);
    const startLevelValue = options.hash.startLevel ? parseInt(options.hash.startLevel, 10) : 0;

    // Add dependencies to parent of main resource so that we are aware of new pages at sibling level
    currentResource.getDependencies().add(mainResourceNode.getParent().getCanonicalPath());
    return new SafeString(printMenu(baseNode, 1, {
        renderContext,
        mainResourceNode,
        currentResource,
        workspace,
        menuName,
        startLevelValue,
        maxDepth
    }));
};
