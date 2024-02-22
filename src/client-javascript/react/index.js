import React from 'react';
import {hydrateRoot, createRoot} from 'react-dom/client';

const hydrateReactComponent = element => {
    if (!element.dataset.hydrated) {
        const conf = JSON.parse(decodeURIComponent(element.dataset.reacthydrate));

        if (!window.appShell || !window.appShell[conf.bundle] || !window.appShell[conf.bundle].get(conf.name)) {
            console.error('npm-modules-engine AppShell: React hydration not possible because component not found for bundle ' +
                conf.bundle + ' and id ' + conf.name +
                '. Make sure you have a registered appShell for this module and that the component is registered in it.');
            return;
        }

        window.appShell[conf.bundle].get(conf.name).then(f => {
            hydrateRoot(element, React.createElement(f().default, conf.props));
            element.dataset.hydrated = 'true';
            console.log('npm-modules-engine AppShell: React component hydrated', element);
        });
    }
};

const renderReactComponent = element => {
    if (!element.dataset.hydrated) {
        const conf = JSON.parse(decodeURIComponent(element.dataset.reactrender));

        if (!window.appShell || !window.appShell[conf.bundle] || !window.appShell[conf.bundle].get(conf.name)) {
            console.error('npm-modules-engine AppShell: React render not possible because component not found for bundle ' +
                conf.bundle + ' and id ' + conf.name +
                '. Make sure you have a registered appShell for this module and that the component is registered in it.');
            return;
        }

        window.appShell[conf.bundle].get(conf.name).then(f => {
            const root = createRoot(element);
            root.render(React.createElement(f().default, conf.props));
            element.dataset.hydrated = 'true';
            console.log('npm-modules-engine AppShell: React component rendered', element);
        });
    }
};

const renderReactComponents = rootElement => {
    rootElement.querySelectorAll('[data-reactrender]').forEach(element => {
        renderReactComponent(element);
    });
};

const hydrateReactComponents = rootElement => {
    rootElement.querySelectorAll('[data-reacthydrate]').forEach(element => {
        hydrateReactComponent(element);
    });
};

export {
    hydrateReactComponent,
    renderReactComponent,
    hydrateReactComponents,
    renderReactComponents
};
