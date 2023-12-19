import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Select from 'react-select';
import './Controller.css';

const customStyles = {
    control: (provided) => ({
        ...provided,
        backgroundColor: '#4CAF50',
        borderColor: '#ddd',
        padding: '5px',
        color: 'white',
        width: 'calc(90%)',
        minWidth: '200px',
    }),
    placeholder: (provided) => ({
        ...provided,
        color: 'white',
    }),
    menu: (provided) => ({
        ...provided,
        backgroundColor: '#666',
        borderColor: '#ddd',
    }),
    option: (provided, state) => ({
        ...provided,
        backgroundColor: state.isFocused ? '#444' : state.isSelected ? '#222' : '#666',
        color: 'white',
    }),
    singleValue: (provided) => ({
        ...provided,
        color: 'white',
        fontWeight: 'bold',
    }),
};

const WeaponController = () => {
    const [weapons, setWeapons] = useState('');
    const [skins, setSkins] = useState([]);
    const [selectedSkin, setSelectedSkin] = useState(null);
    const [newWeapon, setNewWeapon] = useState({});
    const [updateTagWeaponId, setUpdateTagWeaponId] = useState('');
    const [newTag, setNewTag] = useState('');
    const [deleteWeaponId, setDeleteWeaponId] = useState('');

    const [showWeapons, setShowWeapons] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [weaponsPerPage,] = useState(10);

    const indexOfLastWeapon = currentPage * weaponsPerPage;
    const indexOfFirstWeapon = indexOfLastWeapon - weaponsPerPage;
    const currentWeapons = weapons.slice(indexOfFirstWeapon, indexOfLastWeapon);

    const [errorMessage, setErrorMessage] = useState(null);

    const paginate = (pageNumber) => setCurrentPage(pageNumber);

    useEffect(() => {
        getWeapons();
        getSkinsWithNoWeapon();
    }, []);

    const getWeapons = () => {
        axios.get('http://localhost:8080/weapons')
            .then(response => {
                setWeapons(response.data);
            });
    };

    const getSkinsWithNoWeapon = () => {
        axios.get('http://localhost:8080/skins/noWeapon')
            .then(response => {
                setSkins(response.data.map(skin => ({ value: skin.id, label: `ID: ${skin.id}, Name: ${skin.name}, Price: ${skin.price}, Float: ${skin.float}` })));
            });
    };

    const createWeapon = () => {
        const weaponWithSkin = { ...newWeapon, skin: selectedSkin.value };
        axios.post('http://localhost:8080/weapons', weaponWithSkin)
            .then(response => {
                console.log(response.data);
                getWeapons();
                getSkinsWithNoWeapon();
                setSelectedSkin(null)
                setNewWeapon({});
            })
            .catch(error => {
                console.error('Error creating weapon: ', error);
                setErrorMessage(error.response.data);
                setTimeout(() => setErrorMessage(null), 5000);
            });
    };

    const updateWeaponTag = () => {
        axios.put(`http://localhost:8080/weapons/${updateTagWeaponId}/tag`, null, {
            params: {
                newTag: newTag
            }
        })
            .then(response => {
                console.log(response.data);
                getWeapons();
                setUpdateTagWeaponId('');
                setNewTag('');
            })
            .catch(error => {
                console.error('Error updating weapon\'s tag: ', error);
                setErrorMessage(error.response.data);
                setTimeout(() => setErrorMessage(null), 5000);
            });
    };

    const deleteWeapon = (id) => {
        axios.delete(`http://localhost:8080/weapons/${id}`)
            .then(response => {
                console.log(response.data);
                getWeapons();
                getSkinsWithNoWeapon();
                setDeleteWeaponId('');
            })
            .catch(error => {
                console.error('Error deleting weapon: ', error);
                setErrorMessage(error.response.data);
                setTimeout(() => setErrorMessage(null), 5000);
            });
    };

    const handleNewWeaponChange = (e) => {
        setNewWeapon({
            ...newWeapon,
            [e.target.name]: e.target.value
        });
    };

    const handleCreateWeapon = () => {
        if (!newWeapon || !newWeapon.name || newWeapon.name.length > 50) {
            setErrorMessage('Weapon name must be 1-50 characters long');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        if (!newWeapon.type || newWeapon.type.length > 50) {
            setErrorMessage('Weapon type must be 1-50 characters long');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        if (newWeapon.tag && newWeapon.tag.length > 50) {
            setErrorMessage('Weapon tag must be 50 characters or less');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        if (!selectedSkin) {
            setErrorMessage('Skin must be selected');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        createWeapon();
    };

    const handleUpdateWeaponTag = () => {
        if (!updateTagWeaponId) {
            setErrorMessage('Weapon ID can\'t be empty');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        if (newTag && newTag.length > 50) {
            setErrorMessage('New tag must be 50 characters or less');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        updateWeaponTag();
    };

    const handleDeleteWeapon = () => {
        if (!deleteWeaponId) {
            setErrorMessage('Weapon ID can\'t be empty');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        deleteWeapon(deleteWeaponId);
    };

    return (
        <div className="container">
            {errorMessage && (
                <div className="notification">
                    {errorMessage}
                </div>
            )}

            <h1>Weapons</h1>
            <button className="button" onClick={() => setShowWeapons(!showWeapons)}>Toggle Show Weapons</button>

            <div style={{ display: showWeapons ? 'block' : 'none' }}>
                {showWeapons && currentWeapons.length > 0 ? (
                    <table className="table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Tag</th>
                                <th>Type</th>
                                <th>Skin ID</th>
                                <th>Skin Name</th>
                            </tr>
                        </thead>
                        <tbody>
                            {weapons.slice((currentPage - 1) * weaponsPerPage, currentPage * weaponsPerPage).map(weapon => (
                                <tr key={weapon.id}>
                                    <td>{weapon.id}</td>
                                    <td>{weapon.name}</td>
                                    <td>{weapon.tag}</td>
                                    <td>{weapon.type}</td>
                                    <td>{weapon.skin.id}</td>
                                    <td>{weapon.skin.name}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                ) : (
                    <p>No weapons...</p>
                )}

                {/* Pages */}
                <div>
                    {[...Array(Math.ceil(weapons.length / weaponsPerPage)).keys()].map(number => (
                        <button key={number} onClick={() => paginate(number + 1)}>
                            {number + 1}
                        </button>
                    ))}
                </div>
            </div>

            <h2>Create a new weapon</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field" type="text" name="name" placeholder="Weapon name" onChange={handleNewWeaponChange} value={newWeapon.name || ''} />
                    <input className="input-field" type="text" name="type" placeholder="Weapon type" onChange={handleNewWeaponChange} value={newWeapon.type || ''} />
                    <input className="input-field" type="text" name="tag" placeholder="Weapon tag (optional)" onChange={handleNewWeaponChange} value={newWeapon.tag || ''} />
                </div>
                <div className="form-group">
                    <Select className="select-field" styles={customStyles} options={skins} value={selectedSkin} onChange={setSelectedSkin} />
                    <button onClick={handleCreateWeapon}>Create Weapon</button>
                </div>
            </div>

            <h2>Update a weapon's tag</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field" type="number" placeholder="Weapon ID" value={updateTagWeaponId} onChange={(e) => setUpdateTagWeaponId(e.target.value)} />
                    <input className="input-field" type="text" placeholder="New tag" value={newTag} onChange={(e) => setNewTag(e.target.value)} />
                    <button onClick={handleUpdateWeaponTag}>Update Tag</button>
                </div>
            </div>

            <h2>Delete a weapon</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field" type="number" placeholder="Weapon ID" value={deleteWeaponId} onChange={(e) => setDeleteWeaponId(e.target.value)} />
                    <button onClick={handleDeleteWeapon}>Delete Weapon</button>
                </div>
            </div>
        </div>
    );
};

export default WeaponController;