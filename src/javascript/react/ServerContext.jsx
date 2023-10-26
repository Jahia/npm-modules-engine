import React, {useContext} from 'react';

export const ServerContext = React.createContext({});
export const useServerContext = () => useContext(ServerContext);
export const ServerContextProvider = ({renderContext, currentResource, children}) => {
    return (
        <ServerContext.Provider value={{renderContext, currentResource}}>
            {children}
        </ServerContext.Provider>
    );
};
