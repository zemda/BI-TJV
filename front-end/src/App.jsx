import SkinController from './components/SkinController';
import WeaponController from './components/WeaponController';

import './App.css';

function App() {
    return (
      <div>
        <h1>CS:GO Skin Management</h1>
        <SkinController />
        <p><b>-------------------------------------------------------------------------------------------------------</b></p>
        <WeaponController />
      </div>
    );
}

export default App;