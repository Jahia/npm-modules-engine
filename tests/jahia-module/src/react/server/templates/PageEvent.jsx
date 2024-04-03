import React from 'react';
import {AddResources, Area, Render} from '@jahia/js-server-core';
import {calendar, facets, footer, header, login, navMenu} from "./pageComponents";

export const PageEvent = () => {
    return (<>
        <head>
            <AddResources type={'css'} resources={'styles.css'}/>
        </head>
        <body>
        <div className="page">
            <div className="header">
                <div className="headerContent">
                    <Render content={header}/>
                </div>
                <div className="headerLogin">
                    <Render content={login}/>
                </div>
            </div>
            <div className="nav">
                <Render content={navMenu}/>
            </div>
            <div className="main">
                <div className="article">
                    <Area name={'events'} allowedTypes={['jnt:event']}/>
                </div>
                <div className="aside">
                    <Render content={calendar}/>
                    <Render content={facets}/>
                </div>
            </div>
            <div className="footer">
                <div className="footerContent">
                    <Render content={footer}/>
                </div>
            </div>
        </div>
        </body>
    </>)
}

PageEvent.jahiaComponent = {
    nodeType: 'jnt:page',
    name: 'eventsReact',
    displayName: 'Events page (react)',
    componentType: 'template',
    properties: {
        'cache.requestParameters': 'N-*'
    }
}
