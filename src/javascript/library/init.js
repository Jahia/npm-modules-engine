import * as jsServerEngine from '@jahia/js-server-engine';
import jsServerEngineBuilder from '@jahia/js-server-engine-builder';
import Handlebars from 'handlebars';
import React from 'react';
import * as ReactI18Next from 'react-i18next';
import I18next from 'i18next';
import styledJsx from 'styled-jsx/style';

export default () => {
    // Repackage @jahia/js-server-engine for runtime
    for (const [key, value] of Object.entries(jsServerEngine)) {
        jsServerEngineBuilder.addToLibrary(key, value);
    }

    // Declared shared libraries
    jsServerEngineBuilder.addSharedLibrary('handlebars', Handlebars);
    jsServerEngineBuilder.addSharedLibrary('react', React);
    jsServerEngineBuilder.addSharedLibrary('react-i18next', ReactI18Next);
    jsServerEngineBuilder.addSharedLibrary('i18next', I18next);
    jsServerEngineBuilder.addSharedLibrary('styled-jsx', styledJsx);
};
