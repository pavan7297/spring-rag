const escapeHtml = (value) => String(value).replace(/[&<>"]/g, c => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;'
}[c]));
document.querySelector('#upload-form')?.addEventListener('submit', async event => {
    event.preventDefault();
    const file = document.querySelector('#file').files[0], status = document.querySelector('#upload-status');
    if (!file) {
        status.textContent = 'Choose a file first.';
        return;
    }
    status.className = 'mt-4 text-sm text-slate-600';
    status.textContent = 'Uploading and indexing…';
    try {
        const response = await fetch('/api/documents', {method: 'POST', body: new FormData(event.target)});
        const body = await response.json();
        if (!response.ok) throw new Error(body.error || 'Upload failed');
        status.className = 'mt-4 text-sm text-emerald-700';
        status.textContent = body.status === 'READY' ? `Indexed ${body.chunks} chunks. Redirecting…` : `Upload finished with status ${body.status}.`;
        if (body.status === 'READY') setTimeout(() => location.assign('/documents'), 800);
    } catch (error) {
        status.className = 'mt-4 text-sm text-red-600';
        status.textContent = error.message;
    }
});
document.querySelectorAll('.delete-document').forEach(button => button.addEventListener('click', async () => {
    if (!confirm('Delete this document and all its vectors?')) return;
    const response = await fetch(`/api/documents/${button.dataset.id}`, {method: 'DELETE'});
    if (response.ok) location.reload(); else alert('Unable to delete document.');
}));
document.querySelector('#chat-form')?.addEventListener('submit', async event => {
    event.preventDefault();
    const input = document.querySelector('#question'), status = document.querySelector('#chat-status'),
        area = document.querySelector('#messages'), question = input.value.trim();
    if (!question) return;
    input.value = '';
    status.textContent = '';
    appendMessage('USER', question, true);
    appendMessage('ASSISTANT', 'Thinking…');
    try {
        const response = await fetch('/api/chat', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({sessionId: area.dataset.session || null, question})
        });
        const body = await response.json();
        if (!response.ok) throw new Error(body.error || 'Chat failed');
        area.lastElementChild.remove();
        appendMessage('ASSISTANT', body.answer);
        if (body.sessionId && !area.dataset.session) {
            history.replaceState(null, '', `/chat?session=${body.sessionId}`);
            area.dataset.session = body.sessionId;
        }
    } catch (error) {
        area.lastElementChild.remove();
        status.textContent = error.message;
    }
});

function appendMessage(role, content, own = false) {
    const area = document.querySelector('#messages'), article = document.createElement('article');
    article.className = `max-w-3xl rounded-xl p-4 ${own ? 'ml-auto bg-slate-900 text-white' : 'bg-slate-100'}`;
    article.innerHTML = `<p class="mb-1 text-xs font-bold uppercase opacity-60">${role}</p><p class="whitespace-pre-wrap">${escapeHtml(content)}</p>`;
    area.appendChild(article);
    area.scrollTop = area.scrollHeight;
}
