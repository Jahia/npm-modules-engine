import React from 'react';
import {JArea, JRender, JAddResources} from '@jahia/server-jsx';
import {footer, login, navMenu, header} from "./pageComponents";

export const PageSimple = () => {
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
                    <JArea name={'pagecontent'}/>
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

PageSimple = {
    id: 'page_simple_react',
    target: 'jnt:page',
    templateName: 'simpleReact',
    displayName: 'Simple page (react)',
    properties: {
        template: 'true'
    }
}