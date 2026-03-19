/* mis-reservas.js – Hotel Praia */

document.addEventListener('DOMContentLoaded', () => {

  /* ── Filtros ── */
  const filtros = document.querySelectorAll('.filtro-btn');
  const cards   = document.querySelectorAll('.reserva-card');

  filtros.forEach(btn => {
    btn.addEventListener('click', () => {
      // Activar botón
      filtros.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');

      const filter = btn.dataset.filter;

      cards.forEach(card => {
        if (filter === 'todas' || card.dataset.estado === filter) {
          card.classList.remove('hidden');
        } else {
          card.classList.add('hidden');
        }
      });
    });
  });

});