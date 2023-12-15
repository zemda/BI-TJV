import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Controller.css';

const SkinController = () => {
    const [skins, setSkins] = useState([]);
    const [newSkin, setNewSkin] = useState({});
    const [skinId, setSkinId] = useState(null);
    const [caseId, setCaseId] = useState(null);
    const [newPrice, setNewPrice] = useState(null);
    const [deleteSkinId, setDeleteSkinId] = useState(null);
    const [isFilterModalOpen, setIsFilterModalOpen] = useState(false);
    const [filterSkins, setFilterSkins] = useState([]);
    const [filterParams, setFilterParams] = useState({});

    const [showSkins, setShowSkins] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [skinsPerPage,] = useState(10);

    const indexOfLastSkin = currentPage * skinsPerPage;
    const indexOfFirstSkin = indexOfLastSkin - skinsPerPage;
    const currentSkins = skins.slice(indexOfFirstSkin, indexOfLastSkin);

    const [errorMessage, setErrorMessage] = useState(null);

    const paginateShowSkins = (pageNumber) => setCurrentPage(pageNumber);

    const [currentFilterPage, setCurrentFilterPage] = useState(1);
    const [filteredSkinsPerPage,] = useState(5);
    const indexOfLastFilterSkin = currentFilterPage * filteredSkinsPerPage;
    const indexOfFirstFilterSkin = indexOfLastFilterSkin - filteredSkinsPerPage;
    const currentFilterSkins = filterSkins.slice(indexOfFirstFilterSkin, indexOfLastFilterSkin);
    const paginateFilter = (pageNumber) => setCurrentFilterPage(pageNumber);




    useEffect(() => {
        getSkins();
    }, []);

    const getSkins = () => {
        axios.get('http://localhost:8080/skins')
            .then(response => {
                setSkins(response.data);
            })
            .catch(error => {
                console.error('Error fetching data: ', error);
                setErrorMessage(error.response.data);
                setTimeout(() => setErrorMessage(null), 5000);
            });
    };

    const createSkin = (skin, caseId) => {
        axios.post('http://localhost:8080/skins', skin, {
            params: {
                caseId: caseId
            }
        })
            .then(response => {
                console.log(response.data);
                console.log(caseId);
                getSkins();
                setNewSkin({});
            })
            .catch(error => {
                console.error('Error creating skin: ', error);
                setErrorMessage(error.response.data);
                setTimeout(() => setErrorMessage(null), 5000);
            });
    };

    const updateSkinPrice = (id, newPrice) => {
        axios.put(`http://localhost:8080/skins/${id}/price`, null, {
            params: {
                newPrice: newPrice
            }
        })
            .then(response => {
                console.log(response.data);
                getSkins();
            })
            .catch(error => {
                console.error('Error updating skin price: ', error);
                setErrorMessage(error.response.data);
                setTimeout(() => setErrorMessage(null), 5000);
            });
    };

    const updateSkinDropsFrom = (skinId, caseId) => {
        axios.put(`http://localhost:8080/skins/${skinId}/cases`, [caseId])
            .then(response => {
                console.log(response.data);
                getSkins();
            })
            .catch(error => {
                console.error('Error updating skin drops from: ', error);
                setErrorMessage(error.response.data);
                setTimeout(() => setErrorMessage(null), 5000);
            });
    };

    const deleteSkin = (id) => {
        axios.delete(`http://localhost:8080/skins/${id}`)
            .then(response => {
                console.log(response.data);
                getSkins();
            })
            .catch(error => {
                console.error('Error deleting skin: ', error);
                setErrorMessage(error.response.data);
                setTimeout(() => setErrorMessage(null), 5000);
            });
    };

    const getFilterSkins = (filter) => {
        const filterCopy = { ...filter };

        for (let key in filterCopy) {
            if (filterCopy[key] === '') {
                filterCopy[key] = null;
            }
        }

        axios.get('http://localhost:8080/skins/filter', { params: filterCopy })
            .then(response => {
                setFilterSkins(response.data);
            });
    };

    const handleNewSkinChange = (event) => {
        setNewSkin({
            ...newSkin,
            [event.target.name]: event.target.value
        });
    };

    const handleCreateSkin = () => {
        if (!newSkin || !newSkin.name || newSkin.name.length > 50) {
            setErrorMessage('Name must be 50 characters or less');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        if (!newSkin.rarity) {
            setErrorMessage('Rarity must be selected');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        if (!newSkin.price || isNaN(newSkin.price) || newSkin.price < 0) {
            setErrorMessage('Price must be a positive number');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        if (!newSkin.paintSeed || isNaN(newSkin.paintSeed) || newSkin.paintSeed < 0 || newSkin.paintSeed > 1000) {
            setErrorMessage('Paint Seed must be a number between 0 and 1000');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        if (!newSkin.float || isNaN(newSkin.float) || newSkin.float < 0 || newSkin.float > 1) {
            setErrorMessage('Float must be a number between 0 and 1');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        createSkin(newSkin, caseId);
    };

    const handleUpdateSkinPrice = () => {
        if (!skinId) {
            setErrorMessage('Skin ID can\'t be empty');
            setTimeout(() => setErrorMessage(null), 5000);
            return; 
        }

        if (!newPrice || isNaN(newPrice) || newPrice < 0) {
            setErrorMessage('Price must be a positive number');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        updateSkinPrice(skinId, newPrice);
    };

    const handleUpdateSkinDropsFrom = () => {
        if (!skinId || !caseId) {
            setErrorMessage('Both Skin ID and Case ID must be provided');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        updateSkinDropsFrom(skinId, caseId);
    };

    const handleDeleteSkin = () => {
        deleteSkin(deleteSkinId);
    };

    const handleFilterParamsChange = (e) => {
        setFilterParams({
            ...filterParams,
            [e.target.name]: e.target.value
        });
    };

    const handleClearResults = () => {
        setFilterSkins([]);
    };

    const handleFilterSkins = () => {
        if (filterParams.name && filterParams.name.length > 50) {
            setErrorMessage('Name must be 50 characters or less');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        if (filterParams.price && (isNaN(filterParams.price) || filterParams.price < 0)) {
            setErrorMessage('Price must be a positive number');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        if (filterParams.paintSeed && (isNaN(filterParams.paintSeed))) {
            setErrorMessage('Paint Seed must be a number');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        if (filterParams.float && (isNaN(filterParams.float))) {
            setErrorMessage('Float must be a number');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        getFilterSkins(filterParams);
    };

    return (
        <div className="container">
            {errorMessage && (
                <div className="notification">
                    {errorMessage}
                </div>
            )}

            <h1>Skins</h1>

            <button className="button" onClick={() => setShowSkins(!showSkins)}>Toggle Show Skins</button>
            <div style={{ display: showSkins ? 'block' : 'none' }}>
                {showSkins && currentSkins.length > 0 ? (
                    <table className="table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Rarity</th>
                                <th>Exterior</th>
                                <th>Price</th>
                                <th>Paint Seed</th>
                                <th>Float</th>
                            </tr>
                        </thead>
                        <tbody>
                            {skins.slice((currentPage - 1) * skinsPerPage, currentPage * skinsPerPage).map(skin => (
                                <tr key={skin.id}>
                                    <td>{skin.id}</td>
                                    <td>{skin.name}</td>
                                    <td>{skin.rarity}</td>
                                    <td>{skin.exterior}</td>
                                    <td>{skin.price}</td>
                                    <td>{skin.paintSeed}</td>
                                    <td>{skin.float}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                ) : (
                    <p>Loading skins...</p>
                )}

                {/* Pages */}
                <div>
                    {[...Array(Math.ceil(skins.length / skinsPerPage)).keys()].map(number => (
                        <button key={number} onClick={() => paginateShowSkins(number + 1)}>
                            {number + 1}
                        </button>
                    ))}
                </div>
            </div>

            <h2>Create a new skin</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field" name="name" placeholder="Name" onChange={handleNewSkinChange} />
                    <select className="input-field" name="rarity" onChange={handleNewSkinChange}>
                        <option value="">Select rarity</option>
                        <option value="Common">Common</option>
                        <option value="Uncommon">Uncommon</option>
                        <option value="Rare">Rare</option>
                        <option value="Mythical">Mythical</option>
                        <option value="Legendary">Legendary</option>
                        <option value="Ancient">Ancient</option>
                        <option value="Immortal">Immortal</option>
                    </select>
                    <input className="input-field" name="price" placeholder="Price" onChange={handleNewSkinChange} />
                    <input className="input-field" name="paintSeed" type="number" step="1" placeholder="Paint Seed" onChange={handleNewSkinChange} />
                    <input className="input-field" name="float" placeholder="Float" onChange={handleNewSkinChange} />
                    <input className="input-field" name="caseid" type="number" min="0" placeholder="Case ID (optional)" onChange={(e) => setCaseId(e.target.value)} />
                    <button className="button" onClick={handleCreateSkin}>Create Skin</button>
                </div>
            </div>

            <h2>Delete a skin</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field" name="deleteSkinId" type="number" step="1" placeholder="Skin ID" onChange={(e) => setDeleteSkinId(Math.ceil(e.target.value))} />
                    <button className="button" onClick={handleDeleteSkin}>Delete Skin</button>
                </div>
            </div>

            <h2>Filter skins</h2>

            <div className="form">
                <div className="form-group">
                    <button onClick={() => setIsFilterModalOpen(!isFilterModalOpen)}>Toggle Filters</button>
                    <button onClick={handleClearResults}>Clear Results</button>
                </div>
                {isFilterModalOpen && (
                    <div>
                        <input className="input-field" name="skinId" type="number" placeholder="Skin ID" onChange={handleFilterParamsChange} />
                        <input className="input-field" name="name" placeholder="Name" onChange={handleFilterParamsChange} />
                        <select className="input-field" name="rarity" onChange={handleFilterParamsChange}>
                            <option value="">Select rarity</option>
                            <option value="Common">Common</option>
                            <option value="Uncommon">Uncommon</option>
                            <option value="Rare">Rare</option>
                            <option value="Mythical">Mythical</option>
                            <option value="Legendary">Legendary</option>
                            <option value="Ancient">Ancient</option>
                            <option value="Immortal">Immortal</option>
                        </select>
                        <input className="input-field" name="exterior" placeholder="Exterior" onChange={handleFilterParamsChange} />
                        <input className="input-field" name="price" placeholder="Price" onChange={handleFilterParamsChange} />
                        <input className="input-field" name="paintSeed" type="number" placeholder="Paint Seed" onChange={handleFilterParamsChange} />
                        <input className="input-field" name="float" placeholder="Float" onChange={handleFilterParamsChange} />
                        <input className="input-field" name="weaponId" type="number" placeholder="Weapon ID" onChange={handleFilterParamsChange} />
                        <input className="input-field" name="weaponName" placeholder="Weapon Name" onChange={handleFilterParamsChange} />
                        <input className="input-field" name="csgoCaseId" type="number" placeholder="CSGO Case ID" onChange={handleFilterParamsChange} />
                        <input className="input-field" name="csgoCaseName" placeholder="CSGO Case Name" onChange={handleFilterParamsChange} />
                        <button className="button" onClick={handleFilterSkins}>Filter Skins</button>
                    </div>
                )}
                {currentFilterSkins.length > 0 && (
                    <table className="table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Rarity</th>
                                <th>Exterior</th>
                                <th>Price</th>
                                <th>Paint Seed</th>
                                <th>Float</th>
                            </tr>
                        </thead>
                        <tbody>
                        {filterSkins.slice((currentFilterPage - 1) * filteredSkinsPerPage, currentFilterPage * filteredSkinsPerPage).map(skin => (
                                <tr key={skin.id}>
                                    <td>{skin.id}</td>
                                    <td>{skin.name}</td>
                                    <td>{skin.rarity}</td>
                                    <td>{skin.exterior}</td>
                                    <td>{skin.price}</td>
                                    <td>{skin.paintSeed}</td>
                                    <td>{skin.float}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
                <div>
                    {Array(Math.ceil(filterSkins.length / filteredSkinsPerPage)).fill().map((_, i) => (
                        <button key={i} onClick={() => paginateFilter(i + 1)}>
                            {i + 1}
                        </button>
                    ))}
                </div>
            </div>


            <h2>Update skin price</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field" name="updateSkinIdPrice" type="number" step="1" placeholder="Skin ID" onChange={(e) => setSkinId(Math.ceil(e.target.value))} />
                    <input className="input-field" placeholder="New Price" onChange={(e) => setNewPrice(e.target.value)} />
                    <button className="button" onClick={handleUpdateSkinPrice}>Update Price</button>
                </div>
            </div>

            <h2>Update Skin Drops From</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field" name="updateDropsFromIdSkin" type="number" placeholder="Skin ID" onChange={(e) => setSkinId(Math.ceil(e.target.value))} />
                    <input className="input-field" name="updateDropsFromIdCase" type="number" placeholder="Case ID" onChange={(e) => setCaseId(Math.ceil(e.target.value))} />
                    <button onClick={handleUpdateSkinDropsFrom}>Update</button>
                </div>
            </div>


        </div>
    );
};

export default SkinController;