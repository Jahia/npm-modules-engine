import React from 'react';
import {useServerContext, nodeProps} from '@jahia/server-jsx';

export const TestNodeProps = () => {
    const {currentNode} = useServerContext();
    const props = nodeProps(currentNode, ['propNotSet', 'propNotExists', 'smallText', 'textarea', 'choicelist', 'long', 'double', 'boolean', 'weakreference', 'bigtext', 'date', 'decimal', 'uri', 'name', 'path', 'password']);
    const multipleProps = nodeProps(currentNode, ['multipleSmallText', 'multipleTextarea','multipleChoicelist', 'multipleLong', 'multipleDouble', 'multipleBoolean', 'multipleWeakreference', 'multipleBigtext', 'multipleDate', 'multipleDecimal', 'multipleUri', 'multipleName', 'multiplePath', 'multiplePassword']);

    const printMultiValuedProp = (selector, values, richText = false, ref = false) => {
        return values && values.map(function(value, i){
            if (richText) {
                return <div data-testid={`${selector}_${i + 1}`} dangerouslySetInnerHTML={{
                    __html: value
                }}/>
            } else {
                return <div data-testid={`${selector}_${i + 1}`}>{ref ? value.getPath() : value.toString()}</div>;
            }
        })
    }

    return (
        <>
            <h3>nodeProps usages (single)</h3>
            <div data-testid="nodeProps_smallText">{props.smallText}</div>
            <div data-testid="nodeProps_textarea">{props.textarea}</div>
            <div data-testid="nodeProps_choicelist">{props.choicelist}</div>
            <div data-testid="nodeProps_long">{props.long}</div>
            <div data-testid="nodeProps_double">{props.double}</div>
            <div data-testid="nodeProps_boolean">{props.boolean.toString()}</div>
            <div data-testid="nodeProps_weakreference">{props.weakreference.getPath()}</div>
            <div data-testid="nodeProps_bigtext" dangerouslySetInnerHTML={{
                __html: props.bigtext
            }}/>
            <div data-testid="nodeProps_date">{props.date}</div>
            <div data-testid="nodeProps_decimal">{props.decimal}</div>
            <div data-testid="nodeProps_uri">{props.uri}</div>
            <div data-testid="nodeProps_name">{props.name}</div>
            <div data-testid="nodeProps_path">{props.path}</div>

            <h3>nodeProps usages (multiple)</h3>
            <div data-testid="nodeProps_multipleSmallText">
                {printMultiValuedProp('nodeProps_multipleSmallText', multipleProps.multipleSmallText)}
            </div>
            <div data-testid="nodeProps_multipleTextarea">
                {printMultiValuedProp('nodeProps_multipleTextarea', multipleProps.multipleTextarea)}
            </div>
            <div data-testid="nodeProps_multipleChoicelist">
                {printMultiValuedProp('nodeProps_multipleChoicelist', multipleProps.multipleChoicelist)}
            </div>
            <div data-testid="nodeProps_multipleLong">
                {printMultiValuedProp('nodeProps_multipleLong', multipleProps.multipleLong)}
            </div>
            <div data-testid="nodeProps_multipleDouble">
                {printMultiValuedProp('nodeProps_multipleDouble', multipleProps.multipleDouble)}
            </div>
            <div data-testid="nodeProps_multipleBoolean">
                {printMultiValuedProp('nodeProps_multipleBoolean', multipleProps.multipleBoolean)}
            </div>
            <div data-testid="nodeProps_multipleWeakreference">
                {printMultiValuedProp('nodeProps_multipleWeakreference', multipleProps.multipleWeakreference, false, true)}
            </div>
            <div data-testid="nodeProps_multipleBigtext">
                {printMultiValuedProp('nodeProps_multipleBigtext', multipleProps.multipleBigtext, true)}
            </div>
            <div data-testid="nodeProps_multipleDate">
                {printMultiValuedProp('nodeProps_multipleDate', multipleProps.multipleDate)}
            </div>
            <div data-testid="nodeProps_multipleDecimal">
                {printMultiValuedProp('nodeProps_multipleDecimal', multipleProps.multipleDecimal)}
            </div>
            <div data-testid="nodeProps_multipleUri">
                {printMultiValuedProp('nodeProps_multipleUri', multipleProps.multipleUri)}
            </div>
            <div data-testid="nodeProps_multipleName">
                {printMultiValuedProp('nodeProps_multipleName', multipleProps.multipleName)}
            </div>
            <div data-testid="nodeProps_multiplePath">
                {printMultiValuedProp('nodeProps_multiplePath', multipleProps.multiplePath)}
            </div>

            <h3>nodeProps usages (typing and safety tests)</h3>
            <div data-testid="nodeProps_propNotSet">{props.propNotSet}</div>
            <div data-testid="nodeProps_propNotExists">{props.propNotExists}</div>
            <div data-testid="nodeProps_checkBooleanType">{(typeof props.boolean === 'boolean').toString()}</div>
            <div data-testid="nodeProps_checkLongType">{(typeof props.long === 'number').toString()}</div>
            <div data-testid="nodeProps_checkDoubleType">{(typeof props.double === 'number').toString()}</div>
        </>
    )
}

TestNodeProps.jahiaComponent = {
    id: 'testNodeProps',
    target: 'npmExample:testNodeProps',
    templateName: 'default',
    displayName: 'test nodeProps'
}