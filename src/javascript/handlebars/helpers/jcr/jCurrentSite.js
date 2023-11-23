import {setResult} from '../../hbsUtils';
import {render} from '@jahia/server-helpers';

export default function (options) {
    return setResult(render.transformToJsNode(options.data.root.renderContext.getSite(), false, false, false), this, options);
}
