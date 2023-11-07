import {render} from '@jahia/server-helpers';
import {SafeString} from 'handlebars';
import {setResult} from "../../util";

const allowedAreaProperties = ['areaView', 'allowedTypes', 'numberOfItems', 'subNodesView'];

export default function(options) {
    const attrs = {};
     attrs.content = Object.keys(options.hash).reduce((acc, attr) => {
        if(allowedAreaProperties.includes(attr)) {
            if(!acc.properties) acc.properties = {};
            acc.properties['j:'+attr] = options.hash[attr];
        } else if(attr === 'name') {
            acc.name = options.hash.name
        }else {
            console.warn(`jArea helper: ${attr} is not a valid attribute, attribute ignored`);
        }

        return acc;
    }, {});

    if (Object.keys(attrs).length === 0) {
        console.warn(`jArea helper: no valid attribute found within ${JSON.stringify(options.hash)}`);
        return '';
    }

    if(!attrs.content.name) {
        console.warn('jArea helper: name is a mandatory property');
        return '';
    }

    attrs.content.nodeType = 'jnt:area';

    return setResult(new SafeString(render.renderComponent(attrs, options.data.root.renderContext)), this, options);
}