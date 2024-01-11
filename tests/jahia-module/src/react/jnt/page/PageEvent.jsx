import React from 'react';
import {JAddResources, JArea, JRender} from '@jahia/js-server-engine';
import {calendar, facets, footer, header, login, navMenu} from "./pageComponents";

export const PageEvent = () => {
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
                    <JArea name={'events'} allowedTypes={['jnt:event']}/>
                </div>
                <div className="aside">
                    <JRender content={calendar}/>
                    <JRender content={facets}/>
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

PageEvent.jahiaComponent = {
    id: 'page_event_react',
    target: 'jnt:page',
    templateName: 'eventsReact',
    displayName: 'Events page (react)',
    properties: {
        template: 'true',
        'cache.requestParameters': 'N-*'
    }
}
