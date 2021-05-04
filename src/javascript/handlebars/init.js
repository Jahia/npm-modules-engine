import {registry, osgi} from '@jahia/server-helpers';
import * as helpers from './helpers';
import Handlebars from 'handlebars';
import array from 'handlebars-helpers/lib/array'
import collection from 'handlebars-helpers/lib/collection'
import comparison from 'handlebars-helpers/lib/comparison'
import html from 'handlebars-helpers/lib/html'
import match from 'handlebars-helpers/lib/match'
import math from 'handlebars-helpers/lib/math'
import misc from 'handlebars-helpers/lib/misc'
import number from 'handlebars-helpers/lib/number'
import object from 'handlebars-helpers/lib/object'
import path from 'handlebars-helpers/lib/path'
import regex from 'handlebars-helpers/lib/regex'
import string from 'handlebars-helpers/lib/string'
import url from 'handlebars-helpers/lib/url'

export default () => {
    Object.keys(helpers).forEach(k => {
        Handlebars.registerHelper(k, helpers[k]);
    });

    [array, collection, comparison, html, match, math, misc, number, object, path, regex, string, url].forEach(lib => {
        Object.keys(lib).forEach(k => {
            Handlebars.registerHelper(k, lib[k]);
        });
    });

    // Hack to expose handlebars to other modules
    registry.add('module', 'handlebars', {
        exports: Handlebars
    });

    registry.add('view', 'handlebars', {
        render: (currentResource, renderContext, view) => {
            const templateStr = osgi.loadResource(view.bundle, view.templateFile);
            const template = Handlebars.compile(templateStr);
            return template({currentResource, renderContext});
        }
    });
};
