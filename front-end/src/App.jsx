import React, { useState } from 'react';
import SkinController from './components/SkinController';
import WeaponController from './components/WeaponController';
import CsgoCaseController from './components/CsgoCaseController';
import './App.css';


function App() {
    const [showSkinController, setShowSkinController] = useState(false);
    const [showWeaponController, setShowWeaponController] = useState(false);
    const [showCsgoCaseController, setShowCsgoCaseController] = useState(false);

    return (
        
      <div>
        <div id="tsparticles"></div>
        <h1>CS:GO Skin Management</h1>
        
        <button onClick={() => setShowSkinController(!showSkinController)}>Toggle Skin Controller</button>
        {showSkinController && <SkinController />}
        
        <hr className="divider" />        
        
        <button onClick={() => setShowWeaponController(!showWeaponController)}>Toggle Weapon Controller</button>
        {showWeaponController && <WeaponController />}
          
        <hr className="divider" />        
        
        <button onClick={() => setShowCsgoCaseController(!showCsgoCaseController)}>Toggle Case Controller</button>
        {showCsgoCaseController && <CsgoCaseController />}
      
      </div>
    );
}

export default App;