function askConfirmation(title, message, actionLabel) {
    const dialog = document.getElementById('confirmation-dialog');
    if (typeof dialog?.showModal !== 'function') {
        return Promise.resolve(window.confirm(`${title}\n\n${message}`));
    }
    if (dialog.open) return Promise.resolve(false);
    const trigger = document.activeElement;
    document.getElementById('confirmation-title').textContent = title;
    document.getElementById('confirmation-message').textContent = message;
    document.getElementById('confirmation-submit').textContent = actionLabel;
    dialog.returnValue = 'cancel';
    return new Promise(resolve => {
        dialog.addEventListener('close', () => {
            if (trigger instanceof HTMLElement && trigger.isConnected) trigger.focus();
            resolve(dialog.returnValue === 'confirm');
        }, { once: true });
        dialog.showModal();
    });
}

function openModal(id) {
    document.getElementById(id)?.classList.remove('hidden');
}

function closeModal(id) {
    document.getElementById(id)?.classList.add('hidden');
}

function populateInstituicoesSelects() {
    const modalSelect = document.getElementById('aluno-instituicao');
    const filterSelect = document.getElementById('aluno-instituicao-filter');

    if (modalSelect) {
        modalSelect.innerHTML = '<option value="">Selecione uma Instituição...</option>';
        APP_STATE.instituicoes.forEach(inst => {
            modalSelect.innerHTML += `<option value="${inst.id}">${escapeHtml(inst.nome)}</option>`;
        });
    }

    if (filterSelect) {
        const currentVal = filterSelect.value;
        filterSelect.innerHTML = '<option value="">Todas as Instituições</option>';
        APP_STATE.instituicoes.forEach(inst => {
            filterSelect.innerHTML += `<option value="${inst.id}">${escapeHtml(inst.nome)}</option>`;
        });
        filterSelect.value = currentVal;
    }
}

function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');

    const borderColors = {
        success: 'border-l-4 border-l-emerald-600',
        error: 'border-l-4 border-l-rose-600',
        info: 'border-l-4 border-l-blue-600'
    };

    toast.className = `p-3 rounded bg-white border border-slate-200 shadow-sm text-xs font-medium text-slate-800 flex items-center justify-between gap-2 transition-all transform translate-y-1 opacity-0 pointer-events-auto ${borderColors[type] || borderColors.info}`;
    toast.innerHTML = `<span>${escapeHtml(message)}</span>`;

    container.appendChild(toast);

    setTimeout(() => toast.classList.remove('translate-y-1', 'opacity-0'), 10);
    setTimeout(() => {
        toast.classList.add('opacity-0', 'translate-y-1');
        setTimeout(() => toast.remove(), 250);
    }, 3000);
}

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

function formatDate(val) {
    return val || 'Hoje';
}

document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        ['modal-send-coins', 'modal-aluno', 'modal-aluno-detalhes', 'modal-empresa', 'modal-voucher'].forEach(id => closeModal(id));
    }
});

/**
 * INICIALIZAÇÃO: VERIFICA SESSÃO SALVA
 */
window.addEventListener('DOMContentLoaded', async () => {
    localStorage.removeItem('moeda_auth_user');
    try {
        const response = await fetchFromApi('/auth/me');
        if (response.ok) {
            APP_STATE.currentUser = await response.json();
            showAppUI();
            await loadDataFromApi();
        }
    } catch (_) {
        showLoginUI();
    }
});
