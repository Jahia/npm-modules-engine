import {getNode, setResult} from '../../util';
import {node, registry} from '@jahia/server-helpers';

const absoluteUrlRegExp = /^(?:[a-z+]+:)?\/\//i;

const finalizeUrl = (url, renderContext) => {
    if (!absoluteUrlRegExp.test(url)) {
        url = url.startsWith('/') ? renderContext.getRequest().getContextPath() + url : url;
        return renderContext.getResponse().encodeURL(url);
    }

    return url;
};

function appendParameters(url, parameters) {
    const separator = url.includes('?') ? '&' : '?';
    const URLParameters = Object.keys(parameters)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(parameters[key])}`)
        .join('&');

    return `${url}${separator}${URLParameters}`;
}

export default {
    init: () => {
        registry.add('urlBuilder', 'nt:file', {
            priority: 1,
            buildURL: ({jcrNode, mode, currentResource}) => {
                let workspace = mode ?
                    ((mode === 'edit' || mode === 'preview') ? 'default' : 'live') :
                    currentResource.getWorkspace();
                return '/files/' + workspace + node.escapePath(jcrNode.getCanonicalPath());
            }
        });
        registry.add('urlBuilder', '*', {
            priority: 0,
            buildURL: ({jcrNode, mode, language, extension, renderContext, currentResource}) => {
                let workspace;
                let servletPath;
                if (mode) {
                    switch (mode) {
                        case 'edit':
                            servletPath = '/cms/edit';
                            workspace = 'default';
                            break;
                        case 'preview':
                            servletPath = '/cms/render';
                            workspace = 'default';
                            break;
                        default:
                            servletPath = '/cms/render';
                            workspace = 'live';
                            break;
                    }
                } else {
                    servletPath = renderContext.getServletPath();
                    workspace = currentResource.getWorkspace();
                }

                return servletPath + '/' + workspace + '/' + (language ? language : currentResource.getLocale().toString()) +
                    node.escapePath(jcrNode.getPath()) + (extension ? extension : '.html');
            }
        });
    },
    helper: options => {
        const renderContext = options.data.root.renderContext;
        const currentResource = options.data.root.currentResource;

        let url;
        if (options.hash.path) {
            let jcrNode;
            try {
                jcrNode = getNode({path: options.hash.path}, currentResource.getNode().getSession())
            } catch (error) {
                // node not found
            }

            if (jcrNode) {
                const urlBuilders = registry.find({type: 'urlBuilder'}, 'priority');
                for (const urlBuilder of urlBuilders) {
                    if (urlBuilder.key === '*' || jcrNode.isNodeType(urlBuilder.key)) {
                        url = urlBuilder.buildURL({
                            jcrNode,
                            mode: options.hash.mode,
                            language: options.hash.language,
                            extension: options.hash.extension,
                            renderContext,
                            currentResource
                        });
                        break;
                    }
                }
            }
        } else if (options.hash.value) {
            url = options.hash.value;
        } else {
            console.warn('Missing parameter to build url, please provide either a content path using \'path\' parameter, ' +
                'or a prebuild valid url using \'value\' parameter');
        }

        if (url) {
            // Handle parameters
            if (options.hash.parameters && Object.prototype.toString.call(options.hash.parameters) === '[object Object]') {
                url = appendParameters(url, options.hash.parameters);
            }

            // Finalize URL (add context, encodeURL)
            return setResult(finalizeUrl(url, renderContext), this, options);
        }

        console.warn('Unable to build url for: ' + JSON.stringify(options.hash));
    }
};
