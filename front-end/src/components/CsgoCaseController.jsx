import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Controller.css';

const CsgoCaseController = () => {
    const [cases, setCases] = useState('');
    const [newCase, setNewCase] = useState({});
    const [createSkinIdInput, setCreateSkinIdInput] = useState('');
    const [deleteCaseId, setDeleteCaseId] = useState('');
    const [changeCaseId, setChangeCaseId] = useState('');
    const [addOrRemCaseId, setAddOrRemCaseId] = useState('');
    const [newPrice, setNewPrice] = useState('');
    const [addSkins, setAddSkins] = useState(true);
    const [skinIdInput, setSkinIdInput] = useState('');

    const [showCases, setShowCases] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [casesPerPage,] = useState(10);

    const indexOfLastCase = currentPage * casesPerPage;
    const indexOfFirstCase = indexOfLastCase - casesPerPage;
    const currentCases = cases.slice(indexOfFirstCase, indexOfLastCase);

    const [errorMessage, setErrorMessage] = useState(null);

    const paginate = (pageNumber) => setCurrentPage(pageNumber);

    useEffect(() => {
        getCases();
    }, []);

    const handleError = (message) => {
        setErrorMessage(message);
        setTimeout(() => setErrorMessage(null), 5000);
    }

    const getCases = () => {
        axios.get('http://localhost:8080/csgoCase')
            .then(response => {
                setCases(response.data);
            })
            .catch(error => {
                console.error('Error fetching data: ', error);
                handleError(error.response.data);
            });
    };

    const createCase = (csgoCase, skinIds) => {
        const caseWithSkins = { ...csgoCase, contains: skinIds };
        axios.post('http://localhost:8080/csgoCase', caseWithSkins)
            .then(response => {
                console.log(response.data);
                getCases();
                setNewCase({})
                setCreateSkinIdInput('');
            })
            .catch(error => {
                console.error('Error creating case: ', error);
                handleError(error.response.data);
            });
    };

    const deleteCase = (id) => {
        axios.delete(`http://localhost:8080/csgoCase/${id}`)
            .then(response => {
                console.log(response.data);
                getCases();
                setDeleteCaseId('');
            })
            .catch(error => {
                console.error('Error deleting case: ', error);
                handleError(error.response.data);
            });
    };

    const changeCasePrice = (caseId, newPrice) => {
        axios.put(`http://localhost:8080/csgoCase/${caseId}/changePrice/${newPrice}`)
            .then(response => {
                console.log(response.data);
                getCases();
                setChangeCaseId('');
                setNewPrice('');
            })
            .catch(error => {
                console.error('Error changing case price: ', error);
                handleError(error.response.data);
            });
    };

    const addOrRemoveSkinsToCase = (caseId, skinIds, addSkins) => {
        axios.put(`http://localhost:8080/csgoCase/${caseId}/addSkins`, skinIds, {
            params: {
                addSkins: addSkins
            }
        })
            .then(response => {
                console.log(response.data);
                getCases();
                setAddOrRemCaseId('');
                setSkinIdInput('');
                setAddSkins(true);
            })
            .catch(error => {
                console.error('Error adding or removing skins to case: ', error);
                handleError(error.response.data);
            });
    };

    const handleNewCaseChange = (event) => {
        setNewCase({
            ...newCase,
            [event.target.name]: event.target.value
        });
    };

    const handleCreateCase = () => {
        if (!newCase || !newCase.name || newCase.name.lenght > 50) {
            handleError('Case name must be 1-50 characters long');
            return;
        }

        if (!newCase.price || isNaN(newCase.price) || !newCase.price < 0) {
            handleError('Case price must be a positive number');
            return;
        }

        const trimmedInput = createSkinIdInput.replace(/\s/g, '').replace(/,$/, '');
        if (!trimmedInput) {
            handleError('You must enter at least one skin ID.');
            return;
        }

        const regex = /^(\d+,)*\d+$/;
        if (!regex.test(trimmedInput)) {
            handleError('Skin ID(s) must be number(s) (separated by commas)');
            return;
        }

        const skinIds = trimmedInput.split(/\s*,\s*/).map(Number);
        createCase(newCase, skinIds);
    };

    const handleDeleteCase = () => {
        if (!deleteCaseId) {
            handleError('Case ID can\'t be empty');
            return;
        }
        deleteCase(deleteCaseId);
    };

    const handleChangeCasePrice = () => {
        if (!changeCaseId) {
            handleError('Case ID can\'t be empty');
            return;
        }

        if (!newPrice || isNaN(newPrice) || newPrice < 0) {
            handleError('Price must be a positive number');
            return;
        }

        changeCasePrice(changeCaseId, newPrice);
    }

    const handleAddOrRemoveSkinsToCase = () => {
        if (!addOrRemCaseId) {
            handleError('Case ID can\'t be empty');
            return;
        }

        const trimmedInput = skinIdInput.replace(/\s/g, '').replace(/,$/, '');
        if (!trimmedInput) {
            handleError('You must enter at least one skin ID.');
            return;
        }

        const regex = /^(\d+,)*\d+$/;
        if (!regex.test(trimmedInput)) {
            handleError('Skin ID(s) must be number(s) (separated by commas)');
            return;
        }
        const skinIds = trimmedInput.split(/\s*,\s*/).map(Number);
        addOrRemoveSkinsToCase(addOrRemCaseId, skinIds, addSkins);
    }

    const handleSortChange = (event) => {
        const sortMethod = event.target.value;
        let sortedCases;

        switch (sortMethod) {
            case 'price_asc':
                sortedCases = [...cases].sort((a, b) => a.price - b.price);
                break;
            case 'price_desc':
                sortedCases = [...cases].sort((a, b) => b.price - a.price);
                break;
            case 'id_asc':
                sortedCases = [...cases].sort((a, b) => a.id - b.id);
                break;
            case 'id_desc':
                sortedCases = [...cases].sort((a, b) => b.id - a.id);
                break;
            default:
                sortedCases = cases;
        }

        setCases(sortedCases);
    };

    return (
        <div className="container">
            {errorMessage && (
                <div className="notification">
                    {errorMessage}
                </div>
            )}

            <h1>CSGO Cases</h1>
            <button className="button" onClick={() => setShowCases(!showCases)}>Toggle Show Cases</button>
            <select className="input-field" name="sort" onChange={handleSortChange} style={{ marginLeft: '10px' }}>
                <option value="">Select sort method</option>
                <option value="price_asc">Price (Low to High)</option>
                <option value="price_desc">Price (High to Low)</option>
                <option value="id_asc">ID (Low to High)</option>
                <option value="id_desc">ID (High to Low)</option>
            </select>
            <div style={{ display: showCases ? 'block' : 'none' }}>
                {showCases && currentCases.length > 0 ? (
                    <table className="table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Price</th>
                            </tr>
                        </thead>
                        <tbody>
                            {cases.slice((currentPage - 1) * casesPerPage, currentPage * casesPerPage).map(csgoCase => (
                                <tr key={csgoCase.id}>
                                    <td>{csgoCase.id}</td>
                                    <td>{csgoCase.name}</td>
                                    <td>{csgoCase.price}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                ) : (
                    <p>No cases...</p>
                )}

                {/* Pages */}
                <div>
                    {[...Array(Math.ceil(cases.length / casesPerPage)).keys()].map(number => (
                        <button key={number} onClick={() => paginate(number + 1)}>
                            {number + 1}
                        </button>
                    ))}
                </div>
            </div>

            <h2>Create a new case</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field" name="name" placeholder="Name" onChange={handleNewCaseChange} value={newCase.name || ''} />
                    <input className="input-field" name="price" placeholder="Price" onChange={handleNewCaseChange} value={newCase.price || ''} />
                    <input className="input-field" name="skinIds" placeholder="Skin IDs (comma separated)" onChange={(e) => setCreateSkinIdInput(e.target.value)} value={createSkinIdInput} />
                    <button className="button" onClick={handleCreateCase}>Create Case</button>
                </div>
            </div>

            <h2>Delete a case</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field" name="deleteCaseId" type="number" step="1" placeholder="Case ID" onChange={(e) => setDeleteCaseId(Math.ceil(e.target.value))} value={deleteCaseId} />
                    <button className="button" onClick={handleDeleteCase}>Delete Case</button>
                </div>
            </div>

            <h2>Change Case Price</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field" name="caseId" type="number" step="1" placeholder="Case ID" onChange={(e) => setChangeCaseId(Math.ceil(e.target.value))} value={changeCaseId} />
                    <input className="input-field" name="newPrice" type="number" step="0.01" placeholder="New Price" onChange={(e) => setNewPrice(parseFloat(e.target.value))} value={newPrice} />
                    <button className="button" onClick={handleChangeCasePrice}>Change Price</button>
                </div>
            </div>

            <h2>Add/Remove Skins to Case</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field" name="caseId" type="number" step="1" placeholder="Case ID" onChange={(e) => setAddOrRemCaseId(Math.ceil(e.target.value))} value={addOrRemCaseId} />
                    <input className="input-field" name="skinIds" placeholder="Skin IDs (comma separated)" onChange={(e) => setSkinIdInput(e.target.value)} value={skinIdInput} />
                    <div className="checkbox-container">
                        <input className="input-field" name="addSkins" type="checkbox" onChange={(e) => setAddSkins(e.target.checked)} checked={addSkins} />
                    </div>
                    <button className="button" onClick={handleAddOrRemoveSkinsToCase}>Add/Remove Skins</button>
                </div>
            </div>

        </div>
    );
};

export default CsgoCaseController;