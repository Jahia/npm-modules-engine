import {setResult} from '../../../hbsUtils';
import {config} from '@jahia/server-helpers';

export default function (options) {
    return setResult(config.getConfigPids(), this, options);
}
