import {gqlHelper} from '@jahia/server-helpers'

export default function(options) {
    const query = options.fn(this)
    const {varName, ...rest} = options.hash;
    let {data} = gqlHelper.executeQuerySync({
        query,
        ...rest
    });

    const effectiveVarName = varName || 'gql';
    this[effectiveVarName] = data;
}