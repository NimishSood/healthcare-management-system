import React, { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { getDocuments, uploadDocument, downloadDocument } from '../../../services/patientService'

export default function DocumentsPage() {
  const [documents, setDocuments] = useState([])
  const [uploading, setUploading] = useState(false)

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
      toast.success('Uploaded')
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

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">My Documents</h1>
        <input
          type="file"
          onChange={handleUpload}
          disabled={uploading}
        />
      </div>
      {documents.length > 0 ? (
        <table className="min-w-full bg-white">
          <thead>
            <tr>
              <th className="text-left p-2">File Name</th>
              <th className="text-left p-2">Uploaded</th>
              <th className="p-2"></th>
            </tr>
          </thead>
          <tbody>
            {documents.map(doc => (
              <tr key={doc.id} className="border-t">
                <td className="p-2">{doc.fileName}</td>
                <td className="p-2">{new Date(doc.uploadedAt).toLocaleString()}</td>
                <td className="p-2 text-right">
                  <button
                    onClick={() => handleDownload(doc)}
                    className="text-blue-600 hover:underline"
                  >
                    Download
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>No documents uploaded.</p>
      )}
    </div>
  )
}