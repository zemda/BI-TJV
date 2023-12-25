import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Controller.css';

const SkinController = () => {
    const [skins, setSkins] = useState('');
    const [newSkin, setNewSkin] = useState({});

    const [skinIdDelete, setSkinIdDelete] = useState('');
    const [skinIdUpdatePrice, setSkinIdUpdatePrice] = useState('');
    const [skinIdDropsFrom, setSkinIdDropsFrom] = useState('');
    const [caseIdDropsFrom, setCaseIdDropsFrom] = useState('');
    const [caseIdsCreateSkin, setCaseIdsCreateSkin] = useState('');

    const [newPrice, setNewPrice] = useState('');
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

    const [skinIdGetDropsFrom, setSkinIdGetDropsFrom] = useState('');
    const [casesForSkin, setCasesForSkin] = useState([]);
    const [casesCurrentPage, setCasesCurrentPage] = useState(1);
    const casesPerPage = 5;


    useEffect(() => {
        getSkins();
    }, []);

    const handleError = (message) => {
        setErrorMessage(message);
        setTimeout(() => setErrorMessage(null), 5000);
    }

    const getSkins = () => {
        axios.get('http://localhost:8080/skins')
            .then(response => {
                setSkins(response.data);
            })
            .catch(error => {
                console.error('Error fetching data: ', error);
                handleError(error.response.data);
            });
    };

    const createSkin = (skin, caseIds) => {
        const skinWithCases = { ...skin, dropsFrom: caseIds };
        axios.post('http://localhost:8080/skins', skinWithCases)
            .then(response => {
                console.log(response.data);
                getSkins();
                setNewSkin({});
                setCaseIdsCreateSkin('');
            })
            .catch(error => {
                console.error('Error creating skin: ', error);
                handleError(error.response.data);
            });
    };

    const updateSkinPrice = (skinId, newPrice) => {
        axios.put(`http://localhost:8080/skins/${skinId}/price`, null, {
            params: {
                newPrice: newPrice
            }
        })
            .then(response => {
                console.log(response.data);
                getSkins();
                setSkinIdUpdatePrice('');
                setNewPrice('');

            })
            .catch(error => {
                console.error('Error updating skin price: ', error);
                handleError(error.response.data);
            });
    };

    const updateSkinDropsFrom = (skinId, casesIds) => {
        axios.put(`http://localhost:8080/skins/${skinId}/cases`, casesIds)
            .then(response => {
                console.log(response.data);
                getSkins();
                setSkinIdDropsFrom('');
                setCaseIdDropsFrom('');

            })
            .catch(error => {
                console.error('Error updating skin drops from: ', error);
                handleError(error.response.data);
            });
    };

    const deleteSkin = (skinId) => {
        axios.delete(`http://localhost:8080/skins/${skinId}`)
            .then(response => {
                console.log(response.data);
                getSkins();
                setSkinIdDelete('');
            })
            .catch(error => {
                console.error('Error deleting skin: ', error);
                handleError(error.response.data);
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
            handleError('Name must be 50 characters or less');
            return;
        }

        if (!newSkin.rarity) {
            handleError('Rarity must be selected');
            return;
        }

        if (!newSkin.price || isNaN(newSkin.price) || newSkin.price < 0) {
            handleError('Price must be a positive number');
            return;
        }

        if (!newSkin.paintSeed || isNaN(newSkin.paintSeed) || newSkin.paintSeed < 0 || newSkin.paintSeed > 1000) {
            handleError('Paint Seed must be a number between 0 and 1000');
            return;
        }

        if (!newSkin.float || isNaN(newSkin.float) || newSkin.float < 0 || newSkin.float > 1) {
            handleError('Float must be a number between 0 and 1');
            return;
        }

        const trimmedInput = caseIdsCreateSkin.replace(/\s/g, '').replace(/,$/, '');
        let caseIds = null;
        if (trimmedInput !== '') {
            const regex = /^(\d+,)*\d+$/;
            if (!regex.test(trimmedInput)) {
                handleError('Case ID(s) should be number(s) (separated by commas) or empty');
                return;
            }
            caseIds = trimmedInput.split(/\s*,\s*/).map(Number);
        }

        createSkin(newSkin, caseIds);
    };

    const handleUpdateSkinPrice = () => {
        if (!skinIdUpdatePrice) {
            handleError('Skin ID can\'t be empty');
            return;
        }

        if (!newPrice || isNaN(newPrice) || newPrice < 0) {
            handleError('Price must be a positive number');
            return;
        }

        updateSkinPrice(skinIdUpdatePrice, newPrice);
    };

    const handleUpdateSkinDropsFrom = () => {
        if (!skinIdDropsFrom) {
            handleError('Skin ID must be provided');
            return;
        }

        const trimmedInput = caseIdDropsFrom.replace(/\s/g, '').replace(/,$/, '');
        let caseIds = null;
        if (trimmedInput !== '') {
            const regex = /^(\d+,)*\d+$/;
            if (!regex.test(trimmedInput)) {
                handleError('Case ID(s) should be number(s) (separated by commas) or empty');
                return;
            }
            caseIds = trimmedInput.split(/\s*,\s*/).map(Number);
        } else {
            handleError('You must enter at least one ID');
            return;
        }

        updateSkinDropsFrom(skinIdDropsFrom, caseIds);
    };

    const handleDeleteSkin = () => {
        if (!skinIdDelete) { 
            handleError('You need to insert skin id');
            return;
        }
        deleteSkin(skinIdDelete);
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

    const handleClearFilters = () => {
        setFilterParams({});
    };

    const handleFilterSkins = () => {
        if (filterParams.name && filterParams.name.length > 50) {
            handleError('Name must be 50 characters or less');
            return;
        }

        if (filterParams.price && (isNaN(filterParams.price) || filterParams.price < 0)) {
            handleError('Price must be a positive number');
            return;
        }

        if (filterParams.paintSeed && (isNaN(filterParams.paintSeed))) {
            handleError('Paint Seed must be a number');
            return;
        }

        if (filterParams.float && (isNaN(filterParams.float))) {
            handleError('Float must be a number');
            return;
        }

        getFilterSkins(filterParams);
    };

    const sortSkins = (skins, sortMethod) => {
        let sortedSkins;

        switch (sortMethod) {
            case 'price_asc':
                sortedSkins = [...skins].sort((a, b) => a.price - b.price);
                break;
            case 'price_desc':
                sortedSkins = [...skins].sort((a, b) => b.price - a.price);
                break;
            case 'float_asc':
                sortedSkins = [...skins].sort((a, b) => a.float - b.float);
                break;
            case 'float_desc':
                sortedSkins = [...skins].sort((a, b) => b.float - a.float);
                break;
            case 'id_asc':
                sortedSkins = [...skins].sort((a, b) => a.id - b.id);
                break;
            case 'id_desc':
                sortedSkins = [...skins].sort((a, b) => b.id - a.id);
                break;
            default:
                sortedSkins = skins;
        }

        return sortedSkins;
    };

    const handleSortChange = (event) => {
        const sortMethod = event.target.value;
        const sortedSkins = sortSkins(filterSkins, sortMethod);
        setFilterSkins(sortedSkins);
    };

    const handleAllSkinsSortChange = (event) => {
        const sortMethod = event.target.value;
        const sortedSkins = sortSkins(skins, sortMethod);
        setSkins(sortedSkins);
    };

    const getCasesForSkin = (id) => {
        axios.get(`http://localhost:8080/skins/${id}/cases`)
            .then(response => {
                setCasesForSkin(response.data);
                setSkinIdGetDropsFrom('');
            })
            .catch(error => {
                console.error('Error getting cases for skin: ', error);
                handleError(error.response.data);
            });
    };
    
    const handleGetCasesForSkin = () => {
        if (!skinIdGetDropsFrom) { 
            handleError('You need to insert skin id');
            return;
        }
        getCasesForSkin(skinIdGetDropsFrom);
    }

    const clearCasesForSkin = () => {
        setCasesForSkin([]);
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
            <select className="input-field" name="sort" onChange={handleAllSkinsSortChange} style={{ marginLeft: '10px' }}>
                <option value="">Select sort method</option>
                <option value="price_asc">Price (Low to High)</option>
                <option value="price_desc">Price (High to Low)</option>
                <option value="float_asc">Float (Low to High)</option>
                <option value="float_desc">Float (High to Low)</option>
                <option value="id_asc">ID (Low to High)</option>
                <option value="id_desc">ID (High to Low)</option>
            </select>
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
                    <p>No skins...</p>
                )}

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
                    <input className="input-field" name="name" placeholder="Name" onChange={handleNewSkinChange} value={newSkin.name || ''} />
                    <select className="input-field" name="rarity" onChange={handleNewSkinChange} value={newSkin.rarity || ''}>
                        <option value="">Select rarity</option>
                        <option value="Common">Common</option>
                        <option value="Uncommon">Uncommon</option>
                        <option value="Rare">Rare</option>
                        <option value="Mythical">Mythical</option>
                        <option value="Legendary">Legendary</option>
                        <option value="Ancient">Ancient</option>
                        <option value="Immortal">Immortal</option>
                    </select>
                    <input className="input-field" name="price" placeholder="Price" onChange={handleNewSkinChange} value={newSkin.price || ''} />
                    <input className="input-field" name="paintSeed" type="number" step="1" placeholder="Paint Seed" onChange={handleNewSkinChange} value={newSkin.paintSeed || ''} />
                    <input className="input-field" name="float" placeholder="Float" onChange={handleNewSkinChange} value={newSkin.float || ''} />
                    <input className="input-field" name="caseid" placeholder="Case IDs (optional, comma separated)" onChange={(e) => setCaseIdsCreateSkin(e.target.value)} value={caseIdsCreateSkin} />
                    <button className="button" onClick={handleCreateSkin}>Create Skin</button>
                </div>
            </div>

            <h2>Delete a skin</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field" name="deleteSkinId" type="number" step="1" placeholder="Skin ID" onChange={(e) => setSkinIdDelete(Math.ceil(e.target.value))} value={skinIdDelete} />
                    <button className="button" onClick={handleDeleteSkin}>Delete Skin</button>
                </div>
            </div>

            <h2>Filter skins</h2>
            <div className="form">
                <div className="form-group">
                    <button style={{ marginLeft: '10px', marginBottom: '10px' }} onClick={() => setIsFilterModalOpen(!isFilterModalOpen)}>Toggle Filters</button>
                    <button style={{ marginLeft: '10px', marginBottom: '10px' }} onClick={handleClearResults}>Clear Results</button>
                    <button style={{ marginLeft: '10px', marginBottom: '10px' }} onClick={handleClearFilters}>Clear Filter</button>
                </div>
                {isFilterModalOpen && (
                    <div>
                        <input className="input-field" name="skinId" type="number" placeholder="Skin ID" onChange={handleFilterParamsChange} value={filterParams.skinId || ''} />
                        <input className="input-field" name="name" placeholder="Name" onChange={handleFilterParamsChange} value={filterParams.name || ''} />
                        <select className="input-field" name="rarity" onChange={handleFilterParamsChange} value={filterParams.rarity || ''}>
                            <option value="">Select rarity</option>
                            <option value="Common">Common</option>
                            <option value="Uncommon">Uncommon</option>
                            <option value="Rare">Rare</option>
                            <option value="Mythical">Mythical</option>
                            <option value="Legendary">Legendary</option>
                            <option value="Ancient">Ancient</option>
                            <option value="Immortal">Immortal</option>
                        </select>
                        <input className="input-field" name="exterior" placeholder="Exterior" onChange={handleFilterParamsChange} value={filterParams.exterior || ''} />
                        <input className="input-field" name="price" placeholder="Price" onChange={handleFilterParamsChange} value={filterParams.price || ''} />
                        <input className="input-field" name="paintSeed" type="number" placeholder="Paint Seed" onChange={handleFilterParamsChange} value={filterParams.paintSeed || ''} />
                        <input className="input-field" name="float" placeholder="Float" onChange={handleFilterParamsChange} value={filterParams.float || ''} />
                        <input className="input-field" name="weaponId" type="number" placeholder="Weapon ID" onChange={handleFilterParamsChange} value={filterParams.weaponId || ''} />
                        <input className="input-field" name="weaponName" placeholder="Weapon Name" onChange={handleFilterParamsChange} value={filterParams.weaponName || ''} />
                        <input className="input-field" name="csgoCaseId" type="number" placeholder="CSGO Case ID" onChange={handleFilterParamsChange} value={filterParams.csgoCaseId || ''} />
                        <input className="input-field" name="csgoCaseName" placeholder="CSGO Case Name" onChange={handleFilterParamsChange} value={filterParams.csgoCaseName || ''} />
                        <select className="input-field" name="sort" onChange={handleSortChange}>
                            <option value="">Select sort method</option>
                            <option value="price_asc">Price (Low to High)</option>
                            <option value="price_desc">Price (High to Low)</option>
                            <option value="float_asc">Float (Low to High)</option>
                            <option value="float_desc">Float (High to Low)</option>
                            <option value="id_asc">ID (Low to High)</option>
                            <option value="id_desc">ID (High to Low)</option>
                        </select>
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
                    {[...Array(Math.ceil(filterSkins.length / filteredSkinsPerPage)).keys()].map(number => (
                        <button key={number} onClick={() => paginateFilter(number + 1)}>
                            {number + 1}
                        </button>
                    ))}
                </div>
            </div>


            <h2>Update skin price</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field" name="updateSkinIdPrice" type="number" step="1" placeholder="Skin ID" onChange={(e) => setSkinIdUpdatePrice(Math.ceil(e.target.value))} value={skinIdUpdatePrice} />
                    <input className="input-field" placeholder="New Price" onChange={(e) => setNewPrice(e.target.value)} value={newPrice} />
                    <button className="button" onClick={handleUpdateSkinPrice}>Update Price</button>
                </div>
            </div>

            <h2>Update Skin Drops From</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field" name="updateDropsFromIdSkin" type="number" placeholder="Skin ID" onChange={(e) => setSkinIdDropsFrom(Math.ceil(e.target.value))} value={skinIdDropsFrom} />
                    <input className="input-field" name="updateDropsFromIdCase" placeholder="Case IDs (comma separated)" onChange={(e) => setCaseIdDropsFrom(e.target.value)} value={caseIdDropsFrom} />
                    <button onClick={handleUpdateSkinDropsFrom}>Update</button>
                </div>
            </div>

            <h2>Get cases that contains skin</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field " type="number" min="0" placeholder="Enter skin ID" onChange={(e) => setSkinIdGetDropsFrom(Math.ceil(e.target.value))} value={skinIdGetDropsFrom} />
                    <button style={{ marginLeft: '10px', marginBottom: '10px' }} onClick={handleGetCasesForSkin}>Get Cases</button>
                    <button style={{ marginLeft: '10px', marginBottom: '10px' }} onClick={clearCasesForSkin}>Clear Results</button>
                </div>
                {casesForSkin.length > 0 && (
                    <>
                        <table className="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Price</th>
                                </tr>
                            </thead>
                            <tbody>
                                {casesForSkin.slice((casesCurrentPage - 1) * casesPerPage, casesCurrentPage * casesPerPage).map(csgoCase => (
                                    <tr key={csgoCase.id}>
                                        <td>{csgoCase.id}</td>
                                        <td>{csgoCase.name}</td>
                                        <td>{csgoCase.price}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                        <div>
                            {[...Array(Math.ceil(casesForSkin.length / casesPerPage)).keys()].map(number => (
                                <button key={number} onClick={() => setCasesCurrentPage(number + 1)}>
                                    {number + 1}
                                </button>
                            ))}
                        </div>
                    </>
                )}
            </div>


        </div>
    );
};

export default SkinController;