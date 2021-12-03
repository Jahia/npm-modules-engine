import React from 'react'
import ReactDOM from 'react-dom'

const startAppShell = ({view, bundleKey, id, props}) => {
    window.appShell[bundleKey].get(view).then(f => {
        ReactDOM.hydrate(React.createElement(f().default, props), document.getElementById(id), () => {
            console.log('React component hydrated', id)
        })
    })
}

document.querySelectorAll('[data-reactcomponent]').forEach(function(data) {
    startAppShell(JSON.parse(data.dataset.reactcomponent));
});

// Theses value will be exposed in window.jahia
export {
    startAppShell,
    ReactDOM
};
