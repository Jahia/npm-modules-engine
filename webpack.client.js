const path = require('path');
const ModuleFederationPlugin = require('webpack/lib/container/ModuleFederationPlugin');

module.exports = () => {
    let config = {
        entry: {
            reactAppShell: path.resolve(__dirname, 'src/client-javascript/main')
        },
        output: {
            path: path.resolve(__dirname, 'src/main/resources/javascript/apps')
        },
        resolve: {
            mainFields: ['module', 'main'],
            extensions: ['.mjs', '.js', '.jsx']
        },
        module: {
            rules: [
                {
                    test: /\.jsx$/,
                    include: [path.join(__dirname, 'src'), path.join(__dirname, 'views')],
                    use: {
                        loader: 'babel-loader',
                        options: {
                            presets: [
                                ['@babel/preset-env', {modules: false, targets: {safari: '7', ie: '10'}}],
                                '@babel/preset-react'
                            ]
                        }
                    }
                }
            ]
        },
        plugins: [
            new ModuleFederationPlugin({
                name: 'reactAppShell',
                exposes: {
                    '.': './src/client-javascript/exposed'
                },
                shared: {
                    react: {
                        requiredVersion: '^17.0.2'
                    },
                    'react-dom': {},
                    '@apollo/client': {},
                    '@apollo/react-hooks': {},
                    '@jahia/data-helper': {}
                }
            })
        ],
        devtool: 'inline-source-map',
        mode: 'development'
    };

    return config;
};
