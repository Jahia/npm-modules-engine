declare class RenderHelper {
    renderComponentSync(options:any, renderContext:any):String

    renderComponent(options:any, renderContext:any):object

    renderModuleSync(options:any, renderContext:any):String

    renderModule(options:any, renderContext:any):object
}

export const renderHelper: RenderHelper;
