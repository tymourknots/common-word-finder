import { useState } from 'react';
import axios from 'axios';

function App() {
  const [text, setText] = useState('');
  const [result, setResult] = useState([]);
  const [structure, setStructure] = useState('avl');
  const [limit, setLimit] = useState(5);
  const [runtime, setRuntime] = useState(null);
  const [uniqueCount, setUniqueCount] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(
        `${import.meta.env.VITE_API_URL}/api/process`,
        { text, structure, limit }
      );

      setResult(response.data.result || []);
      setRuntime(response.data.runtime ?? null);
      setUniqueCount(response.data.uniqueCount ?? null);
    } catch (error) {
      console.error('Error:', error);
      setResult([]);
      setRuntime(null);
      setUniqueCount(null);
    }
  };

  return (
    <div className="min-h-screen w-screen flex items-center justify-center bg-gray-900 text-white">
      <div className="flex flex-col items-center text-center">
        <h1 className="text-3xl font-bold text-blue-600 mb-4">Common Word Finder</h1>

        <form onSubmit={handleSubmit} className="flex flex-col items-center space-y-4">
          <textarea
            rows="6"
            cols="50"
            className="p-2 rounded bg-gray-800 border border-gray-600"
            placeholder="Enter text here..."
            value={text}
            onChange={(e) => setText(e.target.value)}
          />

          <div>
            <label htmlFor="structure" className="mr-2 font-semibold">Structure:</label>
            <select
              id="structure"
              value={structure}
              onChange={(e) => setStructure(e.target.value)}
              className="p-1 rounded text-black"
            >
              <option value="avl">AVL</option>
              <option value="bst">BST</option>
              <option value="hash">Hash</option>
            </select>
          </div>

          <div>
            <label htmlFor="limit" className="mr-2 font-semibold">Limit:</label>
            <input
              id="limit"
              type="number"
              min="1"
              value={limit === 0 ? '' : limit}
              onChange={(e) => {
                const val = e.target.value;
                setLimit(val === '' ? 0 : Number(val));
              }}
              className="p-1 rounded text-black w-20"
            />
          </div>

          <button type="submit" className="mt-4 bg-blue-600 px-4 py-2 rounded">Submit</button>
        </form>

        {result.length > 0 && (
          <div className="mt-4">
            <ul>
              {result.map(([word, count], index) => (
                <li key={index}>{word}: {count}</li>
              ))}
            </ul>

            {runtime !== null && (
              <p className="mt-2 text-sm text-gray-400">
                Runtime for <span className="font-semibold">{structure.toUpperCase()}</span>: {runtime.toFixed(2)} ms
              </p>
            )}

            {uniqueCount !== null && (
              <p className="mt-1 text-sm text-gray-400">
                Total unique words: <span className="font-semibold">{uniqueCount}</span>
              </p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
