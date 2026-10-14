(() => {
    'use strict';
    const normalize = value => value.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('es');
    document.querySelectorAll('[data-catalog]').forEach(catalog => {
        const input = catalog.querySelector('[data-search]');
        const rows = [...catalog.querySelectorAll('[data-record]')];
        input?.addEventListener('input', () => {
            const query = normalize(input.value.trim());
            let visible = 0;
            rows.forEach(row => { row.hidden = !normalize(row.textContent).includes(query); if (!row.hidden) visible++; });
            const empty = catalog.querySelector('[data-no-results]');
            if (empty) empty.hidden = rows.length === 0 || visible > 0;
        });
    });
    document.querySelectorAll('.app-navbar .nav-link').forEach(link => {
        const path = window.location.pathname;
        if (link.pathname === '/' ? path === '/' || path === '/dashboard' : path === link.pathname || path.startsWith(link.pathname + '/')) {
            link.classList.add('active'); link.setAttribute('aria-current', 'page');
        }
    });
    // Existing edit links remain usable when JavaScript or Bootstrap is unavailable.
    document.addEventListener('click', async event => {
        const link = event.target.closest('[data-edit-dialog]');
        if (!link || !window.bootstrap?.Modal || event.ctrlKey || event.metaKey || event.shiftKey || event.altKey || event.button !== 0) return;
        event.preventDefault();
        link.setAttribute('aria-busy', 'true');
        try {
            const response = await fetch(link.href, { headers: { 'Accept': 'text/html' } });
            if (!response.ok) throw new Error('No se pudo cargar el formulario');
            const doc = new DOMParser().parseFromString(await response.text(), 'text/html');
            const form = doc.querySelector('form.form-surface');
            if (!form) throw new Error('Formulario no disponible');
            document.getElementById('editDialogTitle').textContent = doc.querySelector('h1')?.textContent || 'Editar registro';
            const body = document.getElementById('editDialogBody');
            body.replaceChildren(document.importNode(form, true));
            // Avoid duplicate field IDs between the registration form and the modal.
            body.querySelectorAll('[id]').forEach(field => {
                const old = field.id; field.id = 'dialog-' + old;
                body.querySelectorAll('label[for]').forEach(label => { if (label.htmlFor === old) label.htmlFor = field.id; });
            });
            const cancel = body.querySelector('.form-actions a');
            if (cancel) cancel.addEventListener('click', e => { e.preventDefault(); bootstrap.Modal.getInstance(document.getElementById('editDialog'))?.hide(); });
            bootstrap.Modal.getOrCreateInstance(document.getElementById('editDialog')).show();
        } catch { window.location.assign(link.href); }
        finally { link.removeAttribute('aria-busy'); }
    });
    document.addEventListener('submit', event => {
        const form = event.target;
        const action = new URL(form.action, window.location.href).pathname;
        const messages = { eliminar: '¿Eliminar este registro? Esta acción no se puede deshacer.', cancelar: '¿Cancelar esta reserva?', reembolsar: '¿Registrar el reembolso de este pago?' };
        const verb = action.split('/').pop();
        if (messages[verb] && !window.confirm(messages[verb])) event.preventDefault();
    });
})();
