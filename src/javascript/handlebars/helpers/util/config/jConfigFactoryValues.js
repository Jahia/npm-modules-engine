import {setResult} from '../../../util';
import {config} from '@jahia/server-helpers';

export default function (factoryPid, factoryIdentifier, options) {
    return setResult(config.getConfigFactoryValues(factoryPid, factoryIdentifier), this, options);
}
