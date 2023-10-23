import {osgi, registry, render} from '@jahia/server-helpers';
import * as helpers from './helpers';
import Handlebars from 'handlebars';
import array from 'handlebars-helpers/lib/array';
import collection from 'handlebars-helpers/lib/collection';
import comparison from 'handlebars-helpers/lib/comparison';
import html from 'handlebars-helpers/lib/html';
import match from 'handlebars-helpers/lib/match';
import math from 'handlebars-helpers/lib/math';
import misc from 'handlebars-helpers/lib/misc';
import number from 'handlebars-helpers/lib/number';
import object from 'handlebars-helpers/lib/object';
import path from 'handlebars-helpers/lib/path';
import regex from 'handlebars-helpers/lib/regex';
import string from 'handlebars-helpers/lib/string';
import url from 'handlebars-helpers/lib/url';
import i18next from 'i18next';
import registerI18nHelper from 'handlebars-i18next';

export default () => {
    Object.keys(helpers).forEach(k => {
        if (helpers[k].init && typeof helpers[k].init === 'function') {
            helpers[k].init();
            Handlebars.registerHelper(k, helpers[k].helper);
        } else {
            Handlebars.registerHelper(k, helpers[k]);
        }
    });

    [array, collection, comparison, html, match, math, misc, number, object, path, regex, string, url].forEach(lib => {
        Object.keys(lib).forEach(k => {
            Handlebars.registerHelper(k, lib[k]);
        });
    });

    registerI18nHelper(Handlebars, i18next);

    // Hack to expose handlebars to other modules
    registry.add('module', 'handlebars', {
        exports: Handlebars
    });

    registry.add('view', 'handlebars', {
        render: (currentResource, renderContext, view) => {
            const templateStr = osgi.loadResource(view.bundle, view.templateFile, false);
            const template = Handlebars.compile(templateStr);
            const locale = renderContext.getRequest().getAttribute('org.jahia.utils.i18n.forceLocale') || currentResource.getLocale();
            const mode = renderContext.getMode();
            i18next.loadNamespaces(view.bundle.getSymbolicName());
            i18next.loadLanguages(locale.getLanguage());

            const i18nextValues = {
                ns: view.bundle.getSymbolicName(),
                lng: locale.getLanguage()
            };

            const currentContent = render.transformToJsNode(currentResource.getNode(), false, false, false);

            // Build a simplified ctx for handlebars template usage.
            const ctx = {
                isAjaxRequest: renderContext.isAjaxRequest(),
                isLoggedIn: renderContext.isLoggedIn(),
                readOnlyStatus: renderContext.getReadOnlyStatus().toString(),
                mode: {
                    value: mode,
                    isEdit: mode === "edit",
                    isPreview: mode === "preview",
                    isLive: mode === "live",
                    isFrame: mode === "frame",
                },
                workspace: renderContext.getWorkspace(),
                contentLanguage: locale.getLanguage(),
                uiLanguage: renderContext.getUILocale().getLanguage(),
                requestLanguage: renderContext.getRequest().getLocale().getLanguage(),
                currentModule: renderContext.getURLGenerator().getCurrentModule()
            };
            const renderParameters = render.getRenderParameters(currentResource);
            return template({
                ctx,
                renderParameters,
                currentResource,
                renderContext,
                i18next: i18nextValues,
                view,
                currentContent
            });
        }
    });
};
