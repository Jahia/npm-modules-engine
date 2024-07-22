import React from 'react';
import {AddResources, Area, defineJahiaComponent, Render, useServerContext} from '@jahia/js-server-core';
import {footer, header, login, navMenu} from '../../templates/pageComponents';

export const TestContentTemplate = () => {
    const {currentResource} = useServerContext();
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
                    <Render path={currentResource.getNode().getPath()}/>
                </div>
                <div className="aside">
                    <Area name={'aside'}/>
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

TestContentTemplate.jahiaComponent = defineJahiaComponent({
    nodeType: 'npmExample:testContentTemplate',
    // React views and templates may avoid using properties.default=true by using a 'default' name value.
    name: 'default',
    componentType: 'template',
});
