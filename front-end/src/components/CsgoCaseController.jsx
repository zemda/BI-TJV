import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Controller.css';

const CsgoCaseController = () => {
    const [cases, setCases] = useState([]);
    const [newCase, setNewCase] = useState({});
    const [deleteCaseId, setDeleteCaseId] = useState(null);
    const [caseId, setCaseId] = useState(null);
    const [newPrice, setNewPrice] = useState(null);
    const [addSkins, setAddSkins] = useState(false);
    const [skinIdInput, setSkinIdInput] = useState('');

    const [showCases, setShowCases] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [casesPerPage,] = useState(10);

    const indexOfLastCase = currentPage * casesPerPage;
    const indexOfFirstCase = indexOfLastCase - casesPerPage;
    const currentCases = cases.slice(indexOfFirstCase, indexOfLastCase);

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
            });
    };

    const createCase = (csgoCase) => {
        axios.post('http://localhost:8080/csgoCase', csgoCase)
            .then(response => {
                console.log(response.data);
                getCases();
            })
            .catch(error => {
                console.error('Error creating case: ', error);
            });
    };

    const deleteCase = (id) => {
        axios.delete(`http://localhost:8080/csgoCase/${id}`)
            .then(response => {
                console.log(response.data);
                getCases();
            })
            .catch(error => {
                console.error('Error deleting case: ', error);
            });
    };

    const handleNewCaseChange = (event) => {
        setNewCase({
            ...newCase,
            [event.target.name]: event.target.value
        });
    };

    const handleCreateCase = () => {
        createCase(newCase);
    };

    const handleDeleteCase = () => {
        deleteCase(deleteCaseId);
    };

    const changeCasePrice = (caseId, newPrice) => {
        axios.put(`http://localhost:8080/csgoCase/${caseId}/changePrice/${newPrice}`)
            .then(response => {
                console.log(response.data);
                getCases();
            })
            .catch(error => {
                console.error('Error changing case price: ', error);
            });
    };


    const addOrRemoveSkinsToCase = (caseId, skinIdInput, addSkins) => {
        const skinIds = skinIdInput.split(',').map(Number);
        axios.put(`http://localhost:8080/csgoCase/${caseId}/addSkins`, skinIds, {
            params: {
                addSkins: addSkins
            }
        })
            .then(response => {
                console.log(response.data);
                getCases();
            })
            .catch(error => {
                console.error('Error adding or removing skins to case: ', error);
            });
    };

    return (
        <div className="container">
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
                    <input className="input-field" name="name" placeholder="Name" onChange={handleNewCaseChange} />
                    <input className="input-field" name="price" placeholder="Price" onChange={handleNewCaseChange} />
                    <button className="button" onClick={handleCreateCase}>Create Case</button>
                </div>
            </div>

            <h2>Delete a case</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field" name="deleteCaseId" type="number" step="1" placeholder="Case ID" onChange={(e) => setDeleteCaseId(Math.ceil(e.target.value))} />
                    <button className="button" onClick={handleDeleteCase}>Delete Case</button>
                </div>
            </div>

            <h2>Change Case Price</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field" name="caseId" type="number" step="1" placeholder="Case ID" onChange={(e) => setCaseId(Math.ceil(e.target.value))} />
                    <input className="input-field" name="newPrice" type="number" step="0.01" placeholder="New Price" onChange={(e) => setNewPrice(parseFloat(e.target.value))} />
                    <button className="button" onClick={() => changeCasePrice(caseId, newPrice)}>Change Price</button>
                </div>
            </div>

            <h2>Add/Remove Skins to Case</h2>
            <div className="form">
                <div className="form-group">
                    <input className="input-field" name="caseId" type="number" step="1" placeholder="Case ID" onChange={(e) => setCaseId(Math.ceil(e.target.value))} />
                    <input className="input-field" name="skinIds" placeholder="Skin IDs (comma separated)" onChange={(e) => setSkinIdInput(e.target.value)} />
                    <div className="checkbox-container">
                        <input className="input-field" name="addSkins" type="checkbox" onChange={(e) => setAddSkins(e.target.checked)} />
                    </div>
                    <button className="button" onClick={() => addOrRemoveSkinsToCase(caseId, skinIdInput, addSkins)}>Add/Remove Skins</button>
                </div>
            </div>

        </div>
    );
};

export default CsgoCaseController;