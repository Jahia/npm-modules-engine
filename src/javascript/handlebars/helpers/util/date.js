import utils from 'handlebars-utils';
import dayjs from 'dayjs';
import localizedFormat from 'dayjs/plugin/localizedFormat';
import localeData from 'dayjs/plugin/localeData';
import locale_en from 'dayjs/locale/en';
import locale_fr from 'dayjs/locale/fr';
import locale_de from 'dayjs/locale/de';

dayjs.extend(localizedFormat)
dayjs.extend(localeData)

export default function (date, format, options) {
    if (utils.isOptions(format)) {
        options = format;
        format = undefined;
    }

    if (utils.isOptions(date)) {
        options = date;
        format = undefined;
        date = undefined;
    }

    return dayjs(date)
        .locale(options.data.root.renderContext.getMainResourceLocale().getLanguage())
        .format(format);
}
