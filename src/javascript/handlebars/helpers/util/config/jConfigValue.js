import {setResult} from '../../../hbsUtils';
import {config} from '@jahia/server-helpers';

export default function (configPid, key, options) {
    return setResult(config.getConfigValue(configPid, key), this, options);
}
