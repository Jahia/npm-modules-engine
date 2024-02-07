import React from 'react';
import {JAddResources, JArea, JRender} from '@jahia/js-server-engine';
import {footer, header, login, navMenu} from "./pageComponents";

export const PageSimple = () => {
    return (<>
        <head>
            <JAddResources type={'css'} resources={'styles.css'}/>
        </head>
        <body>
        <JAddResources type={'javascript'} resources={'body-script.js'} targetTag={'body'}/>
        <JAddResources type={'javascript'} resources={'head-script.js'} />
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

PageSimple.jahiaComponent = {
    id: 'page_simple_react',
    nodeType: 'jnt:page',
    name: 'simpleReact',
    displayName: 'Simple page (react)',
    componentType: 'template'
}
