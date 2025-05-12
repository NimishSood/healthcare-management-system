// src/components/TailwindSmoke.jsx
export default function TailwindSmoke() {
  console.log('TailwindSmoke rendered')
  return (
    <div className="p-6 bg-red-200 text-red-800 rounded-lg shadow-lg">
      <div className="border-2 border-black p-2">
        If you can see a red box with red text...
      </div>
    </div>
  )
}