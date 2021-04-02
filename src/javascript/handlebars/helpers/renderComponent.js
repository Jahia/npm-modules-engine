import {SafeString} from 'handlebars';
import {renderHelper} from '@jahia/server-helpers';

export default function (options) {
    return new SafeString(renderHelper.renderComponentSync(options.hash, options.data.root.renderContext));
}

