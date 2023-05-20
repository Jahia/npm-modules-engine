export default function () {
    // Last argument is the options object.
    var result = [];

    // Skip the last argument.
    for (var i = 0; i < arguments.length - 1; ++i) {
        result.push(arguments[i]);
    }

    return result;
}
