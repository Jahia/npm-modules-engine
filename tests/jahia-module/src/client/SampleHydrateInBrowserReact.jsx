import React, { useState } from 'react';

const SampleHydrateInBrowserReact = ({initialValue}) => {
    const [count, setCount] = useState(initialValue);

    const handleClick = () => {
        setCount(count + 1);
    };

    return (
        <div>
            <h2>This React component is hydrated client side:</h2>
            <p>Count: {count}</p>
            <button onClick={handleClick}>Increment</button>
        </div>
    );
}

export default SampleHydrateInBrowserReact;