import {SafeString} from 'handlebars';
import {setResult} from '../jcr/util';

export default function (options) {
    var result = {
        editMode : options.data.root.renderContext.isEditMode(),
        locale : new SafeString(options.data.root.currentResource.getLocale().toString()),
        uiLocale : new SafeString(options.data.root.renderContext.getUILocale().toString()),
        moduleUrl : new SafeString(options.data.root.renderContext.getURLGenerator().getCurrentModule())
    }
    return setResult(result, this, options);
}
