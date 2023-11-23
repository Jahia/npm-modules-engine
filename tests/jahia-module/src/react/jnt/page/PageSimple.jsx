import React from 'react';
import {JArea, JRender, JAddResources} from '@jahia/server-jsx';

export default () => {
    return (<>
        <head>
            <JAddResources type={'css'} resources={'styles.css'}/>
        </head>
        <body>
        <div className="page">
            <div className="header">
                <div className="headerContent">
                    <JRender content={{
                        "name": "header",
                        "nodeType": "jnt:absoluteArea"
                    }}/>
                </div>
                <div className="headerLogin">
                    <JRender content={{
                        "name": "login",
                        "nodeType": "jnt:loginForm"
                    }}/>
                </div>
            </div>
            <div className="nav">
                <JRender content={{
                    "name": "navMenu",
                    "nodeType": "npmExample:navMenu",
                    "properties": {
                        "j:maxDepth": "10",
                        "j:baselineNode": "home",
                        "j:menuItemView": "menuElement"
                    }
                }}/>
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
                    <JRender content={{
                        "name": "footer",
                        "nodeType": "jnt:absoluteArea"
                    }}/>
                </div>
            </div>
        </div>
        </body>
    </>)
}