(() => {
    const dialog = document.getElementById('confirmation-dialog');
    if (!dialog || typeof dialog.showModal !== 'function') return;
    const approved = new WeakSet();
    let pending = null;
    let submitter = null;
    document.addEventListener('submit', event => {
        const form = event.target;
        if (!(form instanceof HTMLFormElement) || !form.dataset.confirm) return;
        if (approved.delete(form)) return;
        event.preventDefault();
        if (pending) return;
        pending = form;
        submitter = event.submitter;
        document.getElementById('confirmation-message').textContent = form.dataset.confirm;
        dialog.returnValue = 'cancel';
        dialog.showModal();
    });
    dialog.addEventListener('close', () => {
        const form = pending;
        const button = submitter;
        pending = null;
        submitter = null;
        if (dialog.returnValue === 'confirm' && form) {
            approved.add(form);
            form.requestSubmit(button || undefined);
            approved.delete(form);
        }
    });
})();
