import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { getPatients } from '../../../services/adminService';

export default function AiFeaturesPage() {
  const [query, setQuery] = useState('');
  const [response, setResponse] = useState('');
  const [patientId, setPatientId] = useState('');
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    getPatients()
      .then(setPatients)
      .catch(() => {});
  }, []);


  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const { data } = await axios.post('/api/ai/query/admin', {
        query,
        patient_id: patientId,
      });
      const formatted =
        typeof data === 'string' ? data : JSON.stringify(data, null, 2);
      setResponse(formatted);

    } catch (err) {
      console.error('AI request failed', err);
      setResponse('Error processing query');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-4 space-y-4">
      <h1 className="text-2xl font-bold">AI Features</h1>
      <form onSubmit={handleSubmit} className="space-y-2">
        <select
          className="w-full border rounded p-2"
          value={patientId}
          onChange={(e) => setPatientId(e.target.value)}
        >
          <option value="">— Select Patient —</option>
          {patients.map((p) => (
            <option key={p.id} value={p.id}>
              {p.firstName} {p.lastName}
            </option>
          ))}
        </select>
        <textarea
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          className="w-full border rounded p-2"
          rows={4}
        />
        <button
          type="submit"
          className="px-4 py-2 bg-blue-600 text-white rounded"
          disabled={loading}
        >
          {loading ? 'Sending…' : 'Send'}
        </button>
      </form>
      {response && (
        <pre className="bg-gray-100 p-4 rounded whitespace-pre-wrap">{response}</pre>
      )}
    </div>
  );
}