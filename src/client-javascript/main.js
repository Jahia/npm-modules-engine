// eslint-disable-next-line no-undef
__webpack_init_sharing__('default');

console.log('npm-modules-engine AppShell: Initializing remotes ..');
Promise.all([
    // eslint-disable-next-line no-undef, camelcase
    ...Object.values(window.appShell || {}).map(container => container.init(__webpack_share_scopes__.default))
]).then(() => {
    console.log('npm-modules-engine AppShell: Bootstrapping application ..');

    import('./bootstrap').then(() => {
        console.log('npm-modules-engine AppShell: Application started');
    });
});
