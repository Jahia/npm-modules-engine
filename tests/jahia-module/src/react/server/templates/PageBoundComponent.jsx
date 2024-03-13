import React from 'react';
import {JAddResources, JArea, JRender} from '@jahia/js-server-engine';
import {footer, header, login, navMenu} from "./pageComponents";

export const PageBoundComponent = () => {
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
                    <JRender content={{
                        name: 'boundComponentTest',
                        nodeType: 'npmExample:testBoundComponent',
                        boundComponentRelativePath: '/events'
                    }}/>
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

PageBoundComponent.jahiaComponent = {
    nodeType: 'jnt:page',
    name: 'boundComponent',
    displayName: 'Bound component page',
    componentType: 'template'
}
