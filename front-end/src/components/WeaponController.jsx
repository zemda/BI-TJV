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

    const handleError = (message) => {
        setErrorMessage(message);
        setTimeout(() => setErrorMessage(null), 5000);
    }

    const getWeapons = () => {
        axios.get('http://localhost:8080/weapons')
            .then(response => {
                setWeapons(response.data);
            })
            .catch(error => {
                console.error('Error fetching data: ', error);
                handleError(error.response.data);
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
        axios.get(`http://localhost:8080/skins/exists?skinId=${selectedSkin.value}&weaponName=${newWeapon.name}`)
            .then(response => {
                console.error('Response: ', response.data);
                if (!response.data) {
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
                            handleError(error.response.data);
                        });
                } else {
                    handleError('Skin already exists on this weapon');
                }
            })
            .catch(error => {
                console.error('Error creating weapon: ', error);
                handleError(error.response.data);
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
                handleError(error.response.data);
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
                handleError(error.response.data);
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
            handleError('Weapon name must be 1-50 characters long');
            return;
        }

        if (!newWeapon.type || newWeapon.type.length > 50) {
            handleError('Weapon type must be 1-50 characters long');
            return;
        }

        if (newWeapon.tag && newWeapon.tag.length > 50) {
            handleError('Weapon tag must be 50 characters or less');
            return;
        }

        if (!selectedSkin) {
            handleError('Skin must be selected');
            return;
        }

        createWeapon();
    };

    const handleUpdateWeaponTag = () => {
        if (!updateTagWeaponId) {
            handleError('Weapon ID can\'t be empty');
            return;
        }

        if (newTag && newTag.length > 50) {
            handleError('New tag must be 50 characters or less');
            return;
        }

        updateWeaponTag();
    };

    const handleDeleteWeapon = () => {
        if (!deleteWeaponId) {
            handleError('Weapon ID can\'t be empty');
            return;
        }

        deleteWeapon(deleteWeaponId);
    };

    const handleSortChange = (event) => {
        const sortMethod = event.target.value;
        let sortedWeapons;

        switch (sortMethod) {
            case 'weapon_id_asc':
                sortedWeapons = [...weapons].sort((a, b) => a.id - b.id);
                break;
            case 'weapon_id_desc':
                sortedWeapons = [...weapons].sort((a, b) => b.id - a.id);
                break;
            case 'skin_id_asc':
                sortedWeapons = [...weapons].sort((a, b) => a.skin.id - b.skin.id);
                break;
            case 'skin_id_desc':
                sortedWeapons = [...weapons].sort((a, b) => b.skin.id - a.skin.id);
                break;
            case 'price_asc':
                sortedWeapons = [...weapons].sort((a, b) => a.skin.price - b.skin.price);
                break;
            case 'price_desc':
                sortedWeapons = [...weapons].sort((a, b) => b.skin.price - a.skin.price);
                break;
            default:
                sortedWeapons = weapons;
        }

        setWeapons(sortedWeapons);
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
            <select className="input-field" name="sort" onChange={handleSortChange} style={{ marginLeft: '10px' }}>
                <option value="">Select sort method</option>
                <option value="weapon_id_asc">Weapon ID (Low to High)</option>
                <option value="weapon_id_desc">Weapon ID (High to Low)</option>
                <option value="skin_id_asc">Skin ID (Low to High)</option>
                <option value="skin_id_desc">Skin ID (High to Low)</option>
                <option value="price_asc">Price (Low to High)</option>
                <option value="price_desc">Price (High to Low)</option>
            </select>

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
                                <th>Price</th>
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
                                    <td>{weapon.skin.price}</td>
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