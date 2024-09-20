import React from 'react';
import {defineJahiaComponent, useServerContext, buildNavMenu, HydrateInBrowser, doAsGuest} from '@jahia/js-server-core';
import {menuEntryCss} from "../../../../helpers/menuEntryCss";
import {buildNavMenu as staticNavMenu, getBaseNode} from '../../../../helpers/navBuilderWithSession';
import HydratedMenuSection from "../../../../client/HydratedMenuSection";

const customSections = [];

const customEntries = [];

const NavMenuEntry = ({menuEntry, fist, last, level}) => {
    return (
        <li className={menuEntryCss(menuEntry, fist, last)}>
            <div dangerouslySetInnerHTML={{
                __html: menuEntry.render
            }}/>
            {menuEntry.children && (
                <div className='navBar'>
                    <ul className={'inner-box level_' + (level + 1)}>
                        {menuEntry.children.map(function(childEntry, i){
                            return <NavMenuEntry menuEntry={childEntry} first={i === 0} last={i === (menuEntry.children.length - 1)} level={level} key={i} />;
                        })}
                        <HydrateInBrowser child={HydratedMenuSection} props={{level:0, path:menuEntry.path, rendered: (menuEntry.children || [] )}} />
                    </ul>
                </div>
            )}
        </li>
    )
}

export const HydratedNavMenu = () => {
    const {renderContext,currentResource} = useServerContext();
    const base = currentResource.getNode().getPropertyAsString('j:baselineNode');
    const maxDepth = currentResource.getNode().getProperty('j:maxDepth').getLong();
    const startLevel = currentResource.getNode().hasProperty('j:startLevel') ? currentResource.getNode().getProperty('j:startLevel').getLong() : 0;
    const staticMenu = doAsGuest(session => staticNavMenu(maxDepth, base, undefined, startLevel, renderContext, currentResource, session));
    const basePath = getBaseNode(base, renderContext, renderContext.isLiveMode() ? 'LIVE' : 'EDIT').getPath();
    return (
        <div className={'navBar'}>
            <ul className="navmenu level_0">
                {staticMenu.map(function(menuEntry, i){
                    return <NavMenuEntry menuEntry={menuEntry} first={i === 0} last={i === (staticMenu.length - 1)} level={0} />;
                })}
                <HydrateInBrowser child={HydratedMenuSection} props={{level:0, path:basePath, rendered: staticMenu}} />
            </ul>
        </div>
    )
}

HydratedNavMenu.jahiaComponent = defineJahiaComponent({
    nodeType: 'npmExample:hydratedNavMenu',
    name: 'default',
    displayName: 'hydratedNavMenu',
    componentType: 'view',
    properties: {
        'cache.mainResource': 'true'
    }
});
