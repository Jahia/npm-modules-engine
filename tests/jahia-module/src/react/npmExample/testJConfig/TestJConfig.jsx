import React from 'react';
import {config} from '@jahia/server-helpers';

export const TestJConfig = () => {
    const configValues = config.getConfigValues('org.jahia.modules.test');
    const defaultFactoryConfigs = config.getConfigFactoryValues('org.jahia.modules.test.factory', 'default');
    const complexObject = config.getConfigValues('org.jahia.modules.test.complex');

    return (
        <>
            <h2>OSGi Configuration view testing component</h2>

            <h3>OSGi Config Basic value tests</h3>

            <p data-testid="configValues.configKey1">configValues.configKey1={configValues.configKey1}</p>
            <p data-testid="configValues.configKey2">configValues.configKey2={configValues.configKey2}</p>

            <p data-testid="configKey1">configKey1={config.getConfigValue('org.jahia.modules.test', 'configKey1')}</p>

            <h3>OSGi Config Factory tests</h3>

            <p data-testid="defaultFactoryConfigs.configKey1">
                defaultFactoryConfigs.configKey1={defaultFactoryConfigs.configKey1}</p>
            <p data-testid="defaultFactoryConfigs.configKey2">
                defaultFactoryConfigs.configKey2={defaultFactoryConfigs.configKey2}</p>

            <h3>OSGi Test Module Factory Config Identifiers</h3>
            <p data-testid="testModuleFactoryIdentifiers">
                testModuleFactoryIdentifiers={config.getConfigFactoryIdentifiers('org.jahia.modules.test.factory').join(',')}
            </p>

            <h3>All Config PIDs</h3>
            <p data-testid="allConfigPIDs">allConfigPIDs={config.getConfigPids()}</p>

            <h3>OSGi Config Complex Values</h3>
            {Object.keys(complexObject).map(function(key, i){
                return (<div data-testid={`complexObject_${key}`} key={key}>
                    {key}: {complexObject[key]}
                </div>);
            })}
        </>
    )
}

TestJConfig.jahiaComponent = {
    id: 'testConfig_react',
    target: 'npmExample:testJConfig',
    templateName: 'react',
    displayName: 'test jConfig (react)'
}