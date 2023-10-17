import React, {useEffect, useState} from 'react';

export default ({currentNode, mainNode, user, view, bundle}) => {
    const [count, setCount] = useState(0);

    useEffect(() => {
        const i = setInterval(() =>
            setCount((prev) => prev + 1), 1000);
        return () => {
            clearInterval(i);
        }
    }, [])

    return (
        <div>My React component [{currentNode.getPath()}] st={count}</div>
    );
}