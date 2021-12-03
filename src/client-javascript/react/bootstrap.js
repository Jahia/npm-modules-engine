import React from 'react'
import ReactDOM from 'react-dom'

const reactRender = ({view, bundleKey, id, props}) => {
    window.appShell[bundleKey].get(view).then(f => {
        ReactDOM.render(React.createElement(f().default, props), document.getElementById(id), () => {
            console.log('React component rendered', id)
        })
    })
}

const reactHydrate = ({view, bundleKey, id, props}) => {
    window.appShell[bundleKey].get(view).then(f => {
        ReactDOM.hydrate(React.createElement(f().default, props), document.getElementById(id), () => {
            console.log('React component hydrated', id)
        })
    })
}

document.querySelectorAll('[data-reactrender]').forEach(data => {
    reactRender(JSON.parse(data.dataset.reactrender));
});

document.querySelectorAll('[data-reacthydrate]').forEach(data => {
    reactHydrate(JSON.parse(data.dataset.reacthydrate));
});

// Theses value will be exposed in window.jahia
export {
    reactRender,
    reactHydrate,
    ReactDOM
};
