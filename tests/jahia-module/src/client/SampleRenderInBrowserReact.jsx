import React, {useEffect, useState} from 'react';

const SampleRenderInBrowserReact = ({path}) => {
    const [currentDate, setCurrentDate] = useState(new Date());

    const updateDate = () => {
        setCurrentDate(new Date());
    };

    useEffect(() => {
        const timerID = setInterval(updateDate, 2000);

        return () => clearInterval(timerID);
    }, []);

    return (
        <div>
            <h2>This React component is fully rendered client side:</h2>
            <p>Able to display current node path: {path}</p>
            <p>And refreshing date every 2 sec: {currentDate.toLocaleString()}</p>
        </div>
    );
}

export default SampleRenderInBrowserReact;