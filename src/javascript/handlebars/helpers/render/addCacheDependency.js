import {SafeString} from 'handlebars';
import {render} from '@jahia/server-helpers';

export default function (options) {
    return new SafeString(render.addCacheDependencyTag(options.hash, options.data.root.renderContext));
}

