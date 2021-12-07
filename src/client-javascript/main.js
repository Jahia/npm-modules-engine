// eslint-disable-next-line no-undef
__webpack_init_sharing__('default');

setTimeout(() => {
    console.log('Initializing remotes ..');
    Promise.all([
        // eslint-disable-next-line no-undef, camelcase
        ...Object.values(window.appShell || {}).map(container => container.init(__webpack_share_scopes__.default))
    ]).then(() => {
        console.log('Bootstrapping application ..');

        import('./bootstrap').then(res => {
            console.log('Application started');
        });
    });
}, 1000); // todo do not set any delay, just for test