import React from 'react';
import ReactDOM from 'react-dom';

const renderReactComponent = element => {
    if (!element.dataset.hydrated) {
        const props = JSON.parse(element.dataset.reactrender);
        window.appShell[props.bundle].get(props.view).then(f => {
            ReactDOM.render(React.createElement(f().default, props), element, () => {
                element.dataset.hydrated = 'true';
                console.log('React component rendered', element);
            });
        });
    }
};

const hydrateReactComponent = element => {
    if (!element.dataset.hydrated) {
        const props = JSON.parse(element.dataset.reacthydrate);
        window.appShell[props.bundle].get(props.view).then(f => {
            ReactDOM.hydrate(React.createElement(f().default, props), element, () => {
                element.dataset.hydrated = 'true';
                console.log('React component hydrated', element);
            });
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

// Theses value will be exposed in window.jahia
export {
    renderReactComponent,
    hydrateReactComponent,
    renderReactComponents,
    hydrateReactComponents
};
