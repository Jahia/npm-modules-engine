declare class Registry {
    add(type:string, key:string, ...muiTheme: object[]):object

    get(type:string, key:string):object
    
    remove(type:string, key:string):object
   
    find(filter:object):object[]
}

export const registry: Registry;
