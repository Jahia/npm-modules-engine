import {setResult} from '../../../util';
import {config} from '@jahia/server-helpers';

export default function (configPid, options) {
    return setResult(config.getConfigValues(configPid), this, options);
}
