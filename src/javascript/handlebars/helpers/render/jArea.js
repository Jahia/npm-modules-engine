import {SafeString} from 'handlebars';
import {setResult} from '../../util';
import renderArea from '../../../utils/renderArea';

export default function (options) {
    return setResult(new SafeString(renderArea(options.hash, options.data.root.renderContext)), this, options);
}