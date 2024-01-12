import * as jsServerEngine from '@jahia/js-server-engine';
import jsServerEngineBuilder from '@jahia/js-server-engine-builder';
import Handlebars from 'handlebars';
import React from 'react';
import styledJsx from 'styled-jsx/style';

export default () => {
    // Repackage @jahia/js-server-engine for runtime
    for (const [key, value] of Object.entries(jsServerEngine)) {
        jsServerEngineBuilder.addToLibrary(key, value);
    }

    // Declared shared libraries
    jsServerEngineBuilder.addSharedLibrary('handlebars', Handlebars);
    jsServerEngineBuilder.addSharedLibrary('react', React);
    jsServerEngineBuilder.addSharedLibrary('styled-jsx', styledJsx);
};
