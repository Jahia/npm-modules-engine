import {addSimplePage} from '../../utils/Utils';
import {JContent} from '@jahia/jcontent-cypress/dist/page-object/jcontent';
import {ContentEditor} from '@jahia/jcontent-cypress/dist/page-object/contentEditor';
import {PickerField} from '@jahia/jcontent-cypress/dist/page-object/fields/pickerField';

describe('Template Picker Correct Order Test', () => {
    before('Create page', () => {
        addSimplePage('/sites/npmTestSite/home', 'TemplatePickerCorrectOrderTest', 'TemplatePickerCorrectOrderTest', 'en', 'simple', [], ['jmix:canBeUseAsTemplateModel'], [{name: 'j:pageTemplateTitle', value: 'testCustom', language: 'en'}]);
    });

    it('should display the correct order of templates', () => {
        const templatesValues = [
            '===== TEMPLATES =====',
            'Bound component page',
            'Events page',
            'Events page (react)',
            'Home page',
            'Nav Menu',
            'Nav Menu (react)',
            'Simple page',
            'Simple page (react)',
            '===== PAGE MODELS =====',
            ' testCustom'
        ];
        cy.login();
        const jContent = JContent.visit('npmTestSite', 'en', 'pages/home').switchToPageBuilder()
        jContent.getCreatePage();
        cy.get('[id="select-jmix:hasTemplateNode_j:templateName"]').click();
        let i = 0;
        cy.get('[id="select-jmix:hasTemplateNode_j:templateName"] menu li').each(el => {
            cy.wrap(el).should('have.text', templatesValues[i]);
            i++;
        });
        cy.logout();
    });
});
