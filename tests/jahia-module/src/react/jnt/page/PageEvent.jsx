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
                    <JArea name={'events'} allowedTypes={['jnt:event']}/>
                </div>
                <div className="aside">
                    <JRender content={{
                        "name": "calendar",
                        "nodeType": "jnt:calendar",
                        "boundComponentRelativePath": "/events",
                        "properties": {
                            "startDateProperty": "startDate",
                            "endDateProperty": "endDate"
                        }
                    }}/>
                    <JRender content={{
                        "name": "facets",
                        "nodeType": "jnt:facets",
                        "boundComponentRelativePath": "/events",
                        "properties": {
                            "j:type": "jnt:event"
                        },
                        "children": [
                            {
                                "name": "fieldFacet",
                                "nodeType": "jnt:fieldFacet",
                                "properties": {
                                    "facet": "type",
                                    "field": "jnt:event;eventsType",
                                    "limit": "100",
                                    "mincount": "1",
                                    "missing": "false",
                                    "offset": "0"
                                },
                                "i18nProperties": {
                                    "en": {
                                        "label": "Type"
                                    }
                                }
                            },
                            {
                                "name": "field-hierarchical-facet",
                                "nodeType": "jnt:fieldHierarchicalFacet",
                                "properties": {
                                    "facet": "Categories",
                                    "field": "jmix:categorized;j:defaultCategory",
                                    "limit": "100",
                                    "mincount": "1",
                                    "missing": "false",
                                    "offset": "0",
                                    "prefix": "/sites/systemsite/categories"
                                },
                                "i18nProperties": {
                                    "en": {
                                        "label": "Categories"
                                    },
                                    "fr": {
                                        "label": "Catégories"
                                    }
                                }
                            },
                            {
                                "name": "queryFacet",
                                "nodeType": "jnt:queryFacet",
                                "properties": {
                                    "facet": "nextEvents",
                                    "mincount": "1",
                                    "query": "0\\:FACET\\:startDate:[NOW/DAY TO NOW/MONTH+1MONTH] OR 0\\:FACET\\:endDate:[NOW/DAY TO NOW/MONTH+1MONTH]"
                                },
                                "i18nProperties": {
                                    "en": {
                                        "label": "Next events per month",
                                        "valueLabel": "##DynamicDateLabel(NOW/MONTH,date,MMMM)##"
                                    },
                                    "fr": {
                                        "label": "Prochains évènements par mois",
                                        "valueLabel": "##DynamicDateLabel(NOW/MONTH,date,MMMM)##"
                                    }
                                }
                            },
                            {
                                "name": "queryFacet1",
                                "nodeType": "jnt:queryFacet",
                                "properties": {
                                    "facet": "nextEvents",
                                    "mincount": "1",
                                    "query": "0\\:FACET\\:startDate:[NOW/MONTH+1MONTH TO NOW/MONTH+2MONTH] OR 0\\:FACET\\:endDate:[NOW/MONTH+1MONTH TO NOW/MONTH+2MONTH]"
                                },
                                "i18nProperties": {
                                    "en": {
                                        "label": "Next events per month",
                                        "valueLabel": "##DynamicDateLabel(NOW/MONTH+1MONTH,date,MMMM)##"
                                    },
                                    "fr": {
                                        "label": "Prochains évènements par mois",
                                        "valueLabel": "##DynamicDateLabel(NOW/MONTH+1MONTH,date,MMMM)##"
                                    }
                                }
                            },
                            {
                                "name": "queryFacet2",
                                "nodeType": "jnt:queryFacet",
                                "properties": {
                                    "facet": "nextEvents",
                                    "mincount": "1",
                                    "query": "0\\:FACET\\:startDate:[NOW/MONTH+2MONTH TO NOW/MONTH+3MONTH] OR 0\\:FACET\\:endDate:[NOW/MONTH+2MONTH TO NOW/MONTH+3MONTH]"
                                },
                                "i18nProperties": {
                                    "en": {
                                        "label": "Next events per month",
                                        "valueLabel": "##DynamicDateLabel(NOW/MONTH+2MONTH,date,MMMM)##"
                                    },
                                    "fr": {
                                        "label": "Prochains évènements par mois",
                                        "valueLabel": "##DynamicDateLabel(NOW/MONTH+2MONTH,date,MMMM)##"
                                    }
                                }
                            },
                            {
                                "name": "queryFacet3",
                                "nodeType": "jnt:queryFacet",
                                "properties": {
                                    "facet": "nextEvents",
                                    "mincount": "1",
                                    "query": "0\\:FACET\\:startDate:[NOW/MONTH+3MONTH TO NOW/MONTH+4MONTH] OR 0\\:FACET\\:endDate:[NOW/MONTH+3MONTH TO NOW/MONTH+4MONTH]"
                                },
                                "i18nProperties": {
                                    "en": {
                                        "label": "Next events per month",
                                        "valueLabel": "##DynamicDateLabel(NOW/MONTH+3MONTH,date,MMMM)##"
                                    },
                                    "fr": {
                                        "label": "Prochains évènements par mois",
                                        "valueLabel": "##DynamicDateLabel(NOW/MONTH+3MONTH,date,MMMM)##"
                                    }
                                }
                            }
                        ]
                    }}/>
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