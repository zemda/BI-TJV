import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Controller.css';

const CsgoCaseController = () => {
    const [cases, setCases] = useState('');
    const [newCase, setNewCase] = useState({});
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

    const getCases = () => {
        axios.get('http://localhost:8080/csgoCase')
            .then(response => {
                setCases(response.data);
            })
            .catch(error => {
                console.error('Error fetching data: ', error);
                setErrorMessage(error.response.data);
                setTimeout(() => setErrorMessage(null), 5000);
            });
    };

    const createCase = (csgoCase) => {
        axios.post('http://localhost:8080/csgoCase', csgoCase)
            .then(response => {
                console.log(response.data);
                getCases();
                setNewCase({})
            })
            .catch(error => {
                console.error('Error creating case: ', error);
                setErrorMessage(error.response.data);
                setTimeout(() => setErrorMessage(null), 5000);
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
                setErrorMessage(error.response.data);
                setTimeout(() => setErrorMessage(null), 5000);
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
                setErrorMessage(error.response.data);
                setTimeout(() => setErrorMessage(null), 5000);
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
                setErrorMessage(error.response.data);
                setTimeout(() => setErrorMessage(null), 5000);
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
            setErrorMessage('Case name must be 1-50 characters long');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        if (!newCase.price || isNaN(newCase.price) || !newCase.price < 0) {
            setErrorMessage('Case price must be a positive number');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        createCase(newCase);
    };

    const handleDeleteCase = () => {
        if (!deleteCaseId) {
            setErrorMessage('Case ID can\'t be empty');
            setTimeout(() => setErrorMessage(null), 5000);
            return; 
        }
        deleteCase(deleteCaseId);
    };

    const handleChangeCasePrice = () => {
        if (!changeCaseId) {
            setErrorMessage('Case ID can\'t be empty');
            setTimeout(() => setErrorMessage(null), 5000);
            return; 
        }

        if (!newPrice || isNaN(newPrice) || newPrice < 0) {
            setErrorMessage('Price must be a positive number');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }

        changeCasePrice(changeCaseId, newPrice);
    }

    const handleAddOrRemoveSkinsToCase = () => {
        if (!addOrRemCaseId) {
            setErrorMessage('Case ID can\'t be empty');
            setTimeout(() => setErrorMessage(null), 5000);
            return; 
        }
        const regex = /^(\d+(,\d+)*)?$/;
        if (!regex.test(skinIdInput)) {
            setErrorMessage('Skin ID(s) must be number(s) (separated by commas)');
            setTimeout(() => setErrorMessage(null), 5000);
            return;
        }
        const skinIds = skinIdInput.split(',').map(Number);
        addOrRemoveSkinsToCase(addOrRemCaseId, skinIds, addSkins);
    }

    return (
        <div className="container">
            {errorMessage && (
                <div className="notification">
                    {errorMessage}
                </div>
            )}

            <h1>CSGO Cases</h1>
            <button className="button" onClick={() => setShowCases(!showCases)}>Toggle Show Cases</button>
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
                    <p>Loading cases...</p>
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