import {setResult} from '../../../hbsUtils';
import {config} from '@jahia/server-helpers';

export default function (configPid, options) {
    return setResult(config.getConfigValues(configPid), this, options);
}
