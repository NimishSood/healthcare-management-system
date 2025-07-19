import React, { useEffect, useState,useRef } from 'react'
import toast from 'react-hot-toast'
import {
  getDocuments,
  uploadDocument,
  downloadDocument,
  deleteDocument,
} from '../../../services/patientService'
import { TrashIcon, ArrowDownTrayIcon } from '@heroicons/react/24/outline'

export default function DocumentsPage() {
  const [documents, setDocuments] = useState([])
  const [uploading, setUploading] = useState(false)
  const [deletingId, setDeletingId] = useState(null)
  const fileRef = useRef(null)

  const loadDocs = () => {
    getDocuments()
      .then(setDocuments)
      .catch(() => toast.error('Failed to load documents'))
  }

  useEffect(() => {
    loadDocs()
  }, [])

  const handleUpload = async (e) => {
    const file = e.target.files[0]
    if (!file) return
    setUploading(true)
    try {
      await uploadDocument(file)
      toast.success('Document uploaded!')
      loadDocs()
    } catch (err) {
      toast.error('Upload failed')
    } finally {
      setUploading(false)
      e.target.value = ''
    }
  }

  const handleDownload = async (doc) => {
    try {
      const blob = await downloadDocument(doc.id)
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = doc.fileName
      a.click()
      window.URL.revokeObjectURL(url)
    } catch (err) {
      toast.error('Download failed')
    }
  }

  const handleDelete = async (doc) => {
    if (!window.confirm('Are you sure you want to delete this document?')) return
    setDeletingId(doc.id)
    try {
      await deleteDocument(doc.id)
      toast.success('Document deleted')
      loadDocs()
    } catch (err) {
      toast.error('Delete failed')
    } finally {
      setDeletingId(null)
    }
  }


  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">My Documents</h1>
        <div>
          <input
            ref={fileRef}
            type="file"
            onChange={handleUpload}
            disabled={uploading}
            className="hidden"
            aria-label="Upload document"
          />
          <button
            onClick={() => fileRef.current?.click()}
            disabled={uploading}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
            aria-label="Upload Document"
          >
            {uploading ? 'Uploading…' : 'Upload Document'}
          </button>
        </div>
        
      </div>
      {documents.length > 0 ? (
        <div className="bg-white shadow rounded-lg overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="text-left px-4 py-2">File Name</th>
                <th className="text-left px-4 py-2">Uploaded</th>
                <th className="px-4 py-2 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {documents.map(doc => (
                <tr key={doc.id} className="odd:bg-gray-50">
                  <td className="px-4 py-2">{doc.fileName}</td>
                  <td className="px-4 py-2">{new Date(doc.uploadedAt).toLocaleString()}</td>
                  <td className="px-4 py-2 text-right space-x-2">
                    <button
                      onClick={() => handleDownload(doc)}
                      className="inline-flex items-center bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700"
                      aria-label={`Download ${doc.fileName}`}
                    >
                      <ArrowDownTrayIcon className="h-4 w-4 mr-1" />
                      Download
                    </button>
                    <button
                      onClick={() => handleDelete(doc)}
                      disabled={deletingId === doc.id}
                      className="inline-flex items-center bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700 disabled:opacity-50"
                      aria-label={`Delete ${doc.fileName}`}
                    >
                      <TrashIcon className="h-4 w-4 mr-1" />
                      {deletingId === doc.id ? 'Deleting…' : 'Delete'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : (
        <div className="text-center text-gray-600 py-20 bg-white rounded-lg shadow">
          No documents uploaded yet.
        </div>
      )}
    </div>
  )
}