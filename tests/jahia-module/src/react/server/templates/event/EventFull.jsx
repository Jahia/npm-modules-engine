import React from 'react';
import {JAddResources, JArea, JRender, useServerContext} from '@jahia/js-server-engine';
import {footer, header, login, navMenu} from "../pageComponents";

export const EventFull = () => {
    const {currentResource} = useServerContext();
    return (<>
        <head>
            <JAddResources type={'css'} resources={'styles.css'}/>
        </head>
        <body>
        <div className="page">
            <div className="header">
                <div className="headerContent">
                    <JRender content={header}/>
                </div>
                <div className="headerLogin">
                    <JRender content={login}/>
                </div>
            </div>
            <div className="nav">
                <JRender content={navMenu}/>
            </div>
            <div className="main">
                <div className="article">
                    <JRender path={currentResource.getNode().getPath()}/>
                </div>
                <div className="aside">
                    <JArea name={'aside'}/>
                </div>
            </div>
            <div className="footer">
                <div className="footerContent">
                    <JRender content={footer}/>
                </div>
            </div>
        </div>
        </body>
    </>)
}

EventFull.jahiaComponent = {
    nodeType: 'jnt:event',
    name: 'fullReact',
    displayName: 'Full event (react)',
    componentType: 'template'
}
