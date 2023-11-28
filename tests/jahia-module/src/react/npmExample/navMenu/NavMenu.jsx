import React from 'react';
import {useServerContext, jBuildNavMenu} from '@jahia/server-jsx';
import {menuEntryCss} from "../../../helpers/menuEntryCss";

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
                    </ul>
                </div>
            )}
        </li>
    )
}

export default () => {
    const {currentResource} = useServerContext();

    const base = currentResource.getNode().getPropertyAsString('j:baselineNode');
    const maxDepth = currentResource.getNode().getProperty('j:maxDepth').getLong();
    const startLevel = currentResource.getNode().hasProperty('j:startLevel') ? currentResource.getNode().getProperty('j:startLevel').getLong() : 0;
    const menu = jBuildNavMenu(maxDepth, base, undefined, startLevel);

    return (
        <div className={'navBar'}>
            <ul className="navmenu level_0">
                {menu.map(function(menuEntry, i){
                    return <NavMenuEntry menuEntry={menuEntry} first={i === 0} last={i === (menu.length - 1)} level={0} key={i} />;
                })}
            </ul>
        </div>
    )
}