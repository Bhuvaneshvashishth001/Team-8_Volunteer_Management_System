const API_BASE = '/api';

export const api = {
  get: (url) => fetch(`${API_BASE}${url}`).then(handle),
  post: (url, body) => fetch(`${API_BASE}${url}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  }).then(handle),
  put: (url, body) => fetch(`${API_BASE}${url}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  }).then(handle),
  delete: (url) => fetch(`${API_BASE}${url}`, { method: 'DELETE' }).then(handle)
};

async function handle(response) {
  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || response.statusText);
  }
  const type = response.headers.get('content-type') || '';
  return type.includes('application/json') ? response.json() : response.text();
}
