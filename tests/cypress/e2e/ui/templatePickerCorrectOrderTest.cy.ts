import {addSimplePage} from '../../utils/Utils';
describe('Template Picker Correct Order Test', () => {
    before('Create page', () => {
        addSimplePage('/sites/npmTestSite/home', 'TemplatePickerCorrectOrderTest', 'TemplatePickerCorrectOrderTest', 'en', 'simple', [], ['jmix:canBeUseAsTemplateModel'], [{name: 'j:pageTemplateTitle', value: 'testCustom', language: 'en'}]);
    });

    it('should display the correct order of templates', () => {
        cy.login();
        cy.visit('/jahia/jcontent/npmTestSite/en/pages/home/aaa');
        cy.get('[data-sel-role="home"]').rightclick();
        cy.get('[data-sel-role="jnt:page"]').click();
        cy.get('[id="select-jmix:hasTemplateNode_j:templateName"]').click();
        cy.get('[id="select-jmix:hasTemplateNode_j:templateName"] menu li').first().should('have.text', '===== TEMPLATES =====');
        cy.logout();
    });
});
