document.addEventListener('DOMContentLoaded', function () {
  const navbar = document.querySelector('.navbar');
  if (navbar) {
    window.addEventListener('scroll', function () {
      navbar.classList.add('scrolled');
    });
  }

  const modal = document.getElementById('servicioModal');
  const modalImg = document.getElementById('modalImg');
  const modalName = document.getElementById('modalName');
  const modalDesc = document.getElementById('modalDesc');
  const modalPrice = document.getElementById('modalPrice');
  const modalPriceType = document.getElementById('modalPriceType');
  const modalCapacity = document.getElementById('modalCapacity');
  const modalHorario = document.getElementById('modalHorario');
  const modalClose = document.getElementById('modalClose');

  function showModal(data) {
    if (!modal) return;
    modalImg.src = data.imagenUrl || '';
    modalName.textContent = data.nombre || '';
    modalDesc.textContent = data.descripcion || '';
    modalPrice.textContent = data.precio != null ? data.precio : '';
    if (modalPriceType) modalPriceType.textContent = data.precioTipo ? `(${data.precioTipo})` : '';
    if (modalCapacity) modalCapacity.textContent = data.capacidad != null ? data.capacidad : '-';
    if (modalHorario) modalHorario.textContent = data.horario || '-';
    modal.style.display = 'flex';
  }

  function hideModal() {
    if (!modal) return;
    modal.style.display = 'none';
  }

  document.body.addEventListener('click', async (e) => {
    const target = e.target.closest('[data-servicio-id]');
    if (target) {
      const id = target.getAttribute('data-servicio-id');
      if (!id) return;
      try {
        const resp = await fetch(`/servicios/api/servicio/${id}`);
        if (!resp.ok) {
          console.error('Error fetching servicio', resp.status);
          return;
        }
        const data = await resp.json();
        showModal(data);
      } catch (err) {
        console.error(err);
      }
    }

    if (e.target === modal || e.target === modalClose) {
      hideModal();
    }
  });

  // Prevent clicks inside modal content from closing it
  const modalContent = document.querySelector('.modal-content');
  if (modalContent) modalContent.addEventListener('click', (ev) => ev.stopPropagation());

});