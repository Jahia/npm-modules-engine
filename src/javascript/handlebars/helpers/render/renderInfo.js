import {setResult} from '../../util';

export default function (options) {
    var result = {
        editMode: options.data.root.renderContext.isEditMode(),
        locale: options.data.root.currentResource.getLocale().toString(),
        uiLocale: options.data.root.renderContext.getUILocale().toString(),
        moduleUrl: options.data.root.renderContext.getURLGenerator().getCurrentModule()
    };
    return setResult(result, this, options);
}
