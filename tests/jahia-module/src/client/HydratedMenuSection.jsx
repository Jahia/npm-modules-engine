import React, {useState, useEffect} from 'react';
import {menuEntryCss} from '../helpers/menuEntryCss';

const HydratedMenuSection = ({level, path, rendered}) => {
    const [nodesToRender, setNodesToRender] = useState([]);
    const toRender = async (nodePath) => {
        const response = await fetch('/modules/graphql', {
            method: 'POST',
            headers: {
                'Content-type': 'application/json',
                accept: 'application/json'
            },
            body: JSON.stringify({
                query:`query {
                    jcr {
                        nodeByPath(path: "${nodePath}") {
                            children {
                                nodes {
                                    path,
                                    displayName,
                                    primaryNodeType {
                                        name
                                    },
                                    children {
                                        nodes {
                                            name
                                        }
                                    }
                                }
                            }
                        }
                    }
                }`})
        });

        const data = await response.json();
        const nodes = data.data.jcr.nodeByPath.children.nodes.filter(node => node.primaryNodeType.name == 'jnt:page' && !rendered.some(render => render.path == node.path));
        return nodes.map(node => {
            const hasChildren = node.children.nodes.length > 0;
            return {...node, children:hasChildren};
        });
    }

    useEffect(() => {
        const getNodes = async () => {
            setNodesToRender(await toRender(path));
            console.log(nodesToRender);
        }
        getNodes();
    }, [])

  return (

    <>
    {nodesToRender.map((menuEntry, i) => (
            <li className={menuEntryCss(nodesToRender, false, i === nodesToRender.length)}>
                <div>
                    <a href={`${menuEntry.path}.html`}>{menuEntry.displayName}</a>
                </div>
                {
                    menuEntry.children && (
                        <div className='navBar'>
                            <ul className={'inner-box level_' + (level + 1)}>
                                     <HydratedMenuSection first={i===0} last={false} level={level + 1} path={menuEntry.path} rendered={[]}/>
                            </ul>
                        </div>
                    )
                }
            </li>
    ))
    }
    </>
  )
}

export default HydratedMenuSection